package com.bc.shredder.store;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

class DataSource {
    private String name;

    private final HikariDataSource dataSource;

    private DataSource(Properties props){
        dataSource = new HikariDataSource(new HikariConfig(props));
    }

    public static DataSource of(DbConnection cluster){
        Properties props = new Properties();
        props.setProperty("dataSourceClassName", cluster.getDriver());
        props.setProperty("dataSource.user", cluster.getUserName());
        props.setProperty("dataSource.password", cluster.getPassword());
        props.setProperty("dataSource.databaseName", cluster.getDbName());
        props.setProperty("dataSource.portNumber", String.valueOf(cluster.getPort()));
        props.setProperty("dataSource.serverName", cluster.getHost());
        DataSource dataSource = new DataSource(props);
        dataSource.setName(cluster.getName());
        return dataSource;
    }

    private void setName(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Connection connection() throws SQLException {
        return dataSource.getConnection();
    }
}
