package com.example;

import java.util.logging.Logger;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public class ConnectorVersion1 {

    private static final Logger logger = Logger.getLogger(ConnectorVersion1.class.getName());
    private static final String CONFIG_FILE = "demo/src/main/java/com/example/Configuration.properties";
    private static final int MAX_POOL_SIZE = 20;
    private static ConnectorVersion1 instance;
    private ConnectionPool connectionPool;

    private ConnectorVersion1() {
        Properties properties = loadProperties(CONFIG_FILE);
        String dbUrl = properties.getProperty("db.url");
        String dbschema = properties.getProperty("db.schema");
        String dbUsername = properties.getProperty("db.username");
        String dbPassword = properties.getProperty("db.password");

        connectionPool = new ConnectionPool(dbUrl + dbschema, dbUsername, dbPassword, MAX_POOL_SIZE, 10);
    }

    public static ConnectorVersion1 getInstance() {
        if (instance == null) {
            synchronized (ConnectorVersion1.class) {
                if (instance == null) {
                    instance = new ConnectorVersion1();
                }
            }
        }
        return instance;
    }

    private Properties loadProperties(String filename) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(filename)) {
            properties.load(input);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load properties file: " + filename, e);
            throw new RuntimeException("Failed to load properties file: " + filename);
        }
        return properties;
    }

    public Connection getConnection() throws SQLException {
        try {
            return connectionPool.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error establishing database connection", e);
            throw e;
        }
    }

    public void releaseConnection(Connection connection) {
        connectionPool.releaseConnection(connection);
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            resultSet = statement.executeQuery();

            List<Map<String, Object>> results = new ArrayList<>();

            ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = resultSet.getObject(i);
                    row.put(columnName, value);
                }
                results.add(row);
            }

            return results;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing query: " + sql, e);
            throw e;
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            releaseConnection(connection);
        }
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            int rowsAffected = statement.executeUpdate();
            return rowsAffected;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error executing update: " + sql, e);
            throw e;
        } finally {
            closeStatement(statement);
            releaseConnection(connection);
        }
    }

    public void executeTransaction(String[] sqlStatements) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            for (String sql : sqlStatements) {
                statement = connection.prepareStatement(sql);
                statement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            logger.log(Level.SEVERE, "Error executing transaction", e);
            throw e;
        } finally {
            closeStatement(statement);
            releaseConnection(connection);
        }
    }

    public ResultSet select(String sql, Object... params) throws SQLException {
        return (ResultSet) executeQuery(sql, params);
    }

    public int insert(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, params);
    }

    public int update(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, params);
    }

    public int delete(String sql, Object... params) throws SQLException {
        return executeUpdate(sql, params);
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing result set", e);
            }
        }
    }

    private void closeStatement(PreparedStatement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing statement", e);
            }
        }
    }
}
