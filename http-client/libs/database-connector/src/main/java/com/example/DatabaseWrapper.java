package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseWrapper {
    private ConnectorVersion1 connector;

    public DatabaseWrapper() {
        connector = ConnectorVersion1.getInstance();
    }

    public ResultSet executeQuery(String sql, Object... params) {
        try {
            return (ResultSet) connector.executeQuery(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int executeUpdate(String sql, Object... params) {
        try {
            return connector.executeUpdate(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void executeTransaction(String[] sqlStatements) {
        try {
            connector.executeTransaction(sqlStatements);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet select(String sql, Object... params) {
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
