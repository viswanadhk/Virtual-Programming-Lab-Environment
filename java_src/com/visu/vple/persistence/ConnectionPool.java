package com.visu.vple.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp2.*;

public class ConnectionPool {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String JDBC_DB_URL = "jdbc:mysql://localhost:3307/vpledb";
    static final String JDBC_USER = "root";
    static final String JDBC_PASS = "123456789";

    private static BasicDataSource connectionPool;

    static {
        connectionPool = new BasicDataSource();
        connectionPool.setUsername(JDBC_USER);
        connectionPool.setPassword(JDBC_PASS);
        connectionPool.setDriverClassName(JDBC_DRIVER);
        connectionPool.setUrl(JDBC_DB_URL);
        connectionPool.setInitialSize(1);
    }

    public static Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }
}
