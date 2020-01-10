package com.bracso.test.routing.config;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.bracso.test.routing.CountryDataSourceRouter;

import static com.bracso.test.routing.LocaleContextHolder.LOCALE_ES;

@Configuration
public class RoutingTestConfiguration {
    
    @Bean
    @Primary
    public DataSource routingDatasource(@Qualifier("dsES") DataSource dsES, @Qualifier("dsUS") DataSource dsUS) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(LOCALE_ES, dsES);
        targetDataSources.put(Locale.US, dsUS);
        CountryDataSourceRouter clientRoutingDatasource = new CountryDataSourceRouter();
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        clientRoutingDatasource.setDefaultTargetDataSource(dsES);
        return clientRoutingDatasource;
    }

}
