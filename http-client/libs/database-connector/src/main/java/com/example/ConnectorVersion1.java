package com.example;

import java.util.logging.Logger;

import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

class ConnectorVersion1 {

    private static final Logger logger = Logger.getLogger(ConnectorVersion1.class.getName());
    private static ConnectorVersion1 instance;
    private HikariDataSource dataSource;

    private ConnectorVersion1(HikariConfig config) {
        try {
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize HikariCP", e);
            throw new RuntimeException("Failed to initialize HikariCP", e);
        }
    }

    // The Singleton design pattern ensures that there is only one instance of a
    // class created throughout the execution of a program
    public static ConnectorVersion1 getInstance(HikariConfig config) {
        if (instance == null) {
            synchronized (ConnectorVersion1.class) {
                if (instance == null) {
                    instance = new ConnectorVersion1(config);
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error establishing database connection", e);
            throw e;
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error releasing database connection", e);
            }
        }
    }

    // Execute query
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

    // Execute update
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

    // Execute transaction
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
