package com.bracso.test.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import brave.Span;
import brave.Tracer;
import brave.Tracer.SpanInScope;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.bracso.test.routing.LocaleContextHolder.LOCALE_ES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RoutingTestApplication.class})
public class RoutingTestApplicationTests {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingTestApplicationTests.class);

    @Autowired
    private Map<String, DataSource> datasources;

    @Resource
    private DataSource routingDatasource;

    @Autowired
    private Tracer tracer;

    @Ignore
    @Test
    public void contextLoads() {
    }

    @Ignore
    @Test
    public void testDataSources() {
        assertThat(this.datasources.size(), is(3));
        this.datasources.forEach(RoutingTestApplicationTests::executeDummyQuery);
    }

    private static void executeDummyQuery(String key, DataSource ds) {
        LOG.info("--> Executing query over " + key);
        try (Connection c = ds.getConnection()) {
            LOG.info("--> Connection url " + c.getMetaData().getURL());
            try (PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM Product"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LOG.info("--> " + key + " - " + rs.getInt(1));
                }
            }
        } catch (Throwable ex) {
            LOG.error("--> obm error", ex);
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testConcurrencia() {

        Runnable runnable1 = () -> {
            LOG.info("Inicio run1");
            LocaleContextHolder.setLocale(LOCALE_ES);
            for (int i = 0; i < 100000; i++) {
                LocaleContextHolder.setLocale(LOCALE_ES);
                executeDummyQuery("routing", this.routingDatasource);
                LocaleContextHolder.clear();
            }

            LOG.info("Fin run1");
        };

        Runnable runnable2 = () -> {
            LOG.info("Inicio run2");
            for (int i = 0; i < 100000; i++) {
                LocaleContextHolder.setLocale(Locale.US);
                executeDummyQuery("routing", this.routingDatasource);
                LocaleContextHolder.clear();
            }
            LOG.info("Fin run2");
        };

        Span newSpan = tracer.nextSpan().name("newSpan").start();
        try (SpanInScope ss = tracer.withSpanInScope(newSpan.start())) {
            // Lanzamos a la vez ambas ejecuciones
            Thread thread1 = new Thread(runnable1);
            Thread thread2 = new Thread(runnable2);
            thread1.start();
            thread2.start();
        } finally {
            newSpan.finish();
        }

    }

}
