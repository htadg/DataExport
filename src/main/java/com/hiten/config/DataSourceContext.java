package com.hiten.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

public class DataSourceContext {

    protected static final Log logger = LogFactory.getLog(DataSourceContext.class);
    private static final ThreadLocal<String> dataSourceName = new ThreadLocal<>();

    public static String getDataSourceName() {
        return dataSourceName.get();
    }

    public static void setDataSourceName(String dataSourceName) {
        Assert.notNull(dataSourceName, "dataSourceName must not be null");
        DataSourceContext.logger.info("Switching to data source '" + dataSourceName + "'");
        DataSourceContext.dataSourceName.set(dataSourceName);
    }

    public static void clearDataSourceName() {
        DataSourceContext.logger.info("Clearing data source name");
        DataSourceContext.dataSourceName.remove();
    }

}
