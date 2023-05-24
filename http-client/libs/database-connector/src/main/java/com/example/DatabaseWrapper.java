package com.example;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.zaxxer.hikari.HikariConfig;

public class DatabaseWrapper {
    private ConnectorVersion1 connector;
    private static final Logger logger = Logger.getLogger(DatabaseWrapper.class.getName());

    public DatabaseWrapper(Properties properties) {
        HikariConfig config = HikariCPConfig.loadConfig(properties);
        connector = ConnectorVersion1.getInstance(config);
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        try {
            return connector.executeQuery(sql, params);
        } catch (SQLException e) {
            // Exception handling
            logger.log(Level.SEVERE, "Error executing query: " + sql, e);
            return null;
        }
    }

    public int executeUpdate(String sql, Object... params) {
        try {
            return connector.executeUpdate(sql, params);
        } catch (SQLException e) {
            // Exception handling
            logger.log(Level.SEVERE, "Error executing update: " + sql, e);
            return -1;
        }
    }

    public void executeTransaction(String[] sqlStatements) {
        try {
            connector.executeTransaction(sqlStatements);
        } catch (SQLException e) {
            // Exception handling
            logger.log(Level.SEVERE, "Error executing transaction", e);
        }
    }

    public List<Map<String, Object>> select(String sql, Object... params) {
        return executeQuery(sql, params);
    }

    public int insert(String sql, Object... params) {
        return executeUpdate(sql, params);
    }

    public int update(String sql, Object... params) {
        return executeUpdate(sql, params);
    }

    public int delete(String sql, Object... params) {
        return executeUpdate(sql, params);
    }
}
