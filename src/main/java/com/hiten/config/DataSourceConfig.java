package com.hiten.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.initialization-script.dropSchema}")
    private String dropSchema;

    @Value("${spring.initialization-script.createSchema}")
    private String createSchema;

    /**
     * Primary datasource for the Batch application.
     * Uses in-memory h2 database to store batch job details
     * @return {@link DataSource}
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix="spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Returns a {@link DataSourceInitializer} that is initialized with the primary data source and a
     * {@link DatabasePopulator} that will execute the schema DDL scripts.
     *
     * @param dataSource the primary data source to initialize
     * @return a {@link DataSourceInitializer}
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    /**
     * A {@link DatabasePopulator} that will populate the database with schema DDL.
     * It uses the spring.batch.initialization-script.dropSchema and
     * spring.batch.initialization-script.createSchema properties to determine the
     * scripts to execute.
     * @return A DatabasePopulator
     */
    private DatabasePopulator databasePopulator() {
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        Resource dropSchemaSql = new ClassPathResource(dropSchema);
        Resource createSchemaSql = new ClassPathResource(createSchema);
        populator.addScript(dropSchemaSql);
        populator.addScript(createSchemaSql);
        return populator;
    }

    @Bean
    public JdbcTransactionManager transactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    @ConfigurationProperties(prefix="application.datasource")
    public DataSource applicationDataSource() {
        return DataSourceBuilder.create().build();
    }

}
