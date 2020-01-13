package com.bracso.test.routing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.assertj.core.api.Assertions;
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
        LOG.debug("--> Executing query over " + key);
        try (Connection c = ds.getConnection()) {
            LOG.debug("--> Connection url " + c.getMetaData().getURL());
            try (PreparedStatement ps = c.prepareStatement("SELECT count(*) FROM Product"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LOG.info("--> " + key + " - " + rs.getInt(1));
                }
            }
        } catch (Throwable ex) {
            LOG.error("--> Error executing query", ex);
            throw new RuntimeException(ex);
        }
    }

    @Test
    public void testConcurrencia() throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<String>> list = new ArrayList<>();

        Callable callable1 = () -> {
            for (int i = 0; i < 10; i++) {
                LocaleContextHolder.setLocale(LOCALE_ES);
                executeDummyQuery("routing es" + i, this.routingDatasource);
                LocaleContextHolder.clear();
            }

            return "OK " + "ES";
        };

        Callable callable2 = () -> {
            for (int i = 0; i < 10; i++) {
                LocaleContextHolder.setLocale(Locale.US);
                executeDummyQuery("routing us" + i, this.routingDatasource);
                LocaleContextHolder.clear();
            }

            return "OK " + "US";
        };

        for (int i = 0; i < 100; i++) {
            Future<String> future1 = executor.submit(callable1);
            Future<String> future2 = executor.submit(callable2);
            list.add(future1);
            list.add(future2);
        }

        list.forEach((fut) -> {
            try {
                LOG.info(new Date() + "::" + fut.get());
            } catch (InterruptedException | ExecutionException e) {
                Assertions.fail("Error", e);
            }
        });
        executor.shutdown();

    }

}
