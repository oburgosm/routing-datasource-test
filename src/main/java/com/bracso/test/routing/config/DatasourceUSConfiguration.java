package com.bracso.test.routing.config;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.inditex.aqsw.framework.data.jdbc.datasources.DataSourceType;

/**
 *
 * @author oburgosm
 */
@Configuration
public class DatasourceUSConfiguration {

//    @Bean
//    public DataSource dsUS() {
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.driverClassName("org.h2.Driver");
//        dataSourceBuilder.url("jdbc:h2:mem:us;DB_CLOSE_ON_EXIT=FALSE");
//        dataSourceBuilder.username("SA");
//        dataSourceBuilder.password("");
//        return dataSourceBuilder.build();
//    }
    
    @Autowired
    private com.inditex.aqsw.framework.data.jdbc.datasources.DataSourceBuilder dataSourceBuilder;

    @Bean
    @ConfigurationProperties(prefix = "amiga.data.jdbc.datasource.ds-us")
    public DataSource dsUS()
            throws SQLException {
        return dataSourceBuilder.build(DataSourceType.XA);
    }
    
    @Bean
    public DataSourceInitializer batchDS2Initializer() throws SQLException {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dsUS());
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new ClassPathResource("myschema.sql"));
        initializer.setDatabasePopulator(populator);
        initializer.setEnabled(true);
        return initializer;
    }

}
