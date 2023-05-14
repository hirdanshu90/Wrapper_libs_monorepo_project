package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionPool {
    private final String url;
    private final String username;
    private final String password;
    private final int maxPoolSize;
    private final int connectionTimeout;

    private final List<Connection> availableConnections = new ArrayList<>();
    private final List<Connection> usedConnections = new ArrayList<>();

    public ConnectionPool(String url, String username, String password, int maxPoolSize, int connectionTimeout) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
        this.connectionTimeout = connectionTimeout;

        initializeConnections();
    }

    private void initializeConnections() {
        try {
            for (int i = 0; i < maxPoolSize; i++) {
                Connection connection = createConnection();
                availableConnections.add(connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        long startTime = System.currentTimeMillis();
        while (availableConnections.isEmpty()) {
            if (usedConnections.size() < maxPoolSize) {
                Connection connection = createConnection();
                usedConnections.add(connection);
                return connection;
            }
            if (System.currentTimeMillis() - startTime >= connectionTimeout) {
                throw new SQLException("Connection pool timeout, no available connections.");
            }
            try {
                Thread.sleep(100); // Wait for 100 milliseconds before retrying
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Connection connection = availableConnections.remove(availableConnections.size() - 1);
        usedConnections.add(connection);
        return connection;
    }

    public synchronized void releaseConnection(Connection connection) {
        usedConnections.remove(connection);
        availableConnections.add(connection);
    }

    public synchronized void close() throws SQLException {
        for (Connection connection : availableConnections) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Handle connection close error if necessary
            }
        }
        for (Connection connection : usedConnections) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Handle connection close error if necessary
            }
        }
        availableConnections.clear();
        usedConnections.clear();
    }

    private Connection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        if (!isValidConnection(connection)) {
            connection.close();
            throw new SQLException("Failed to create a valid database connection.");
        }
        return connection;
    }

    private boolean isValidConnection(Connection connection) {
        try {
            return connection.isValid(2); // Timeout value in seconds
        } catch (SQLException e) {
            return false;
        }
    }
}
