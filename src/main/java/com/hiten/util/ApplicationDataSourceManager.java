package com.hiten.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;


public class ApplicationDataSourceManager {

    private static final Log logger = LogFactory.getLog(ApplicationDataSourceManager.class);
    private static final Map<String, DataSource> dataSources = new HashMap<>();

    static {
        File[] files = Paths.get("src/main/resources/db").toFile().listFiles();
        Assert.notNull(files, "At least one database connection must be setup!");
        for (File file : files) {
            if (!file.getName().endsWith("properties")) continue;
            Properties properties = new Properties();
            try {
                properties.load(file.toURI().toURL().openStream());
                DataSource dataSource = DataSourceBuilder.create()
                        .driverClassName(properties.getProperty("driverClassName"))
                        .url(properties.getProperty("jdbc-url"))
                        .username(properties.getProperty("username"))
                        .password(properties.getProperty("password"))
                        .build();
                dataSources.put(properties.getProperty("name"), dataSource);
            } catch (Exception e) {
                ApplicationDataSourceManager.logger.error("Error while setting datasource connection.", e);
            }
        }
    }

    public static DataSource getDataSource(String dataSourceName) {
        return dataSources.get(dataSourceName);
    }

    public static List<String> getDataSourceNames() {
        return new ArrayList<>(dataSources.keySet());
    }

}
