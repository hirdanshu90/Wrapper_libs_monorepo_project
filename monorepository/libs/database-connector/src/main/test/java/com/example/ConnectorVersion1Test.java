package com.example;

import org.junit.*;
import java.util.*;

public class ConnectorVersion1Test {
    private DatabaseWrapper databaseWrapper;

    @Before
    public void setUp() {
        // Initialize the database wrapper with test configuration
        Properties properties = new Properties();
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/goss_skill");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "gossadmin");
        properties.setProperty("max.pool.size", "20");

        databaseWrapper = new DatabaseWrapper(properties);
    }

    @Test
    public void testExecuteQuery() {
        String sql = "SELECT id, enterpriseID, skills FROM goss_skill.skiils_table WHERE enterpriseID = ?";
        Object[] params = { "233" };

        List<Map<String, Object>> results = databaseWrapper.executeQuery(sql, params);

        // Assert the expected number of rows returned
        Assert.assertEquals(1, results.size());

        // Assert the values in the first row
        Map<String, Object> firstRow = results.get(0);
        Assert.assertEquals(123, firstRow.get("id"));
        Assert.assertEquals("233", firstRow.get("enterpriseID"));
        Assert.assertEquals("React", firstRow.get("skills"));
        // Assert other expected values in the row
    }

    @Test
    public void testExecuteUpdate() {
        String sql = "UPDATE goss_skill.skiils_table SET skills = ? WHERE ID = ?";
        Object[] params = { "Python", 308 };

        int rowsAffected = databaseWrapper.executeUpdate(sql, params);

        // Assert the expected number of rows affected
        Assert.assertEquals(1, rowsAffected);

        // Verify that the update was successful by retrieving the updated row
        String selectSql = "SELECT skills FROM goss_skill.skiils_table WHERE ID = ?";
        Object[] selectParams = { 308 };

        List<Map<String, Object>> results = databaseWrapper.executeQuery(selectSql, selectParams);

        // Assert the updated value
        Assert.assertEquals("Python", results.get(0).get("skills"));
    }

    @Test
    public void testExecuteTransaction() {
        String[] sqlStatements = {
                "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (10000, 'tested1', 'React')"
        };

        databaseWrapper.executeTransaction(sqlStatements);

        // Verify that the transaction was successful by retrieving the inserted row
        String selectSql = "SELECT * FROM goss_skill.skiils_table WHERE ID = ?";
        Object[] selectParams = { 10000 };

        List<Map<String, Object>> results = databaseWrapper.executeQuery(selectSql, selectParams);

        // Assert the expected number of inserted rows
        Assert.assertEquals(1, results.size());
        // Assert other values in the inserted row
        Assert.assertEquals("tested1", results.get(0).get("enterpriseID"));
        Assert.assertEquals("React", results.get(0).get("skills"));
    }

    @Test
    public void testDelete() {
        String sql = "DELETE FROM goss_skill.skiils_table WHERE ID = ?";
        Object[] params = { 89 };

        int rowsAffected = databaseWrapper.delete(sql, params);

        // Assert the expected number of rows deleted
        Assert.assertEquals(1, rowsAffected);

        // Verify that the row is deleted by attempting to retrieve it
        String selectSql = "SELECT * FROM goss_skill.skiils_table WHERE ID = ?";
        Object[] selectParams = { 89 };

        List<Map<String, Object>> results = databaseWrapper.executeQuery(selectSql, selectParams);

        // Assert that no rows are returned
        Assert.assertEquals(0, results.size());
    }
}
