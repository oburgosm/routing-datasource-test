package com.bracso.test.routing.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

/**
 *
 * @author oburgosm
 */
@Configuration
public class DatasourceUSConfiguration {

    @Bean
    public DataSource dsUS() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.h2.Driver");
        dataSourceBuilder.url("jdbc:h2:mem:us;DB_CLOSE_ON_EXIT=FALSE");
        dataSourceBuilder.username("SA");
        dataSourceBuilder.password("");
        return dataSourceBuilder.build();
    }

    @Bean
    public DataSourceInitializer dsUSInitializer() throws SQLException {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dsUS());
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("myschema.sql"));
        initializer.setDatabasePopulator(populator);
        initializer.setEnabled(true);
        return initializer;
    }

}
