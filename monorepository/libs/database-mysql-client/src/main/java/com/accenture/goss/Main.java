package com.accenture.goss;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Configure the properties for the database connection
        Properties dbProperties = new Properties();
        dbProperties.setProperty("db.url", "jdbc:mysql://localhost:3306/goss_skill");
        dbProperties.setProperty("db.username", "root");
        dbProperties.setProperty("db.password", "gossadmin");
        dbProperties.setProperty("max.pool.size", "20");

        // Create an instance of DatabaseWrapper using the configured properties
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(dbProperties);

        try {
            // INSERT query
            String insertQuery = "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (?, ?, ?)";
            int insertedRows = databaseWrapper.insert(insertQuery, 12321, "tested1", "React");
            System.out.println("Inserted rows: " + insertedRows);

            // UPDATE query
            String updateQuery = "UPDATE goss_skill.skiils_table SET skills = ? WHERE ID = ?";
            int updatedRows = databaseWrapper.update(updateQuery, "Python", 30);
            System.out.println("Updated rows: " + updatedRows);

            // DELETE query
            String deleteQuery = "DELETE FROM goss_skill.skiils_table WHERE ID = ?";
            int deletedRows = databaseWrapper.delete(deleteQuery, 89);
            System.out.println("Deleted rows: " + deletedRows);

            // Execute the SELECT query
            String selectQuery = "SELECT id, enterpriseID, skills FROM goss_skill.skiils_table WHERE enterpriseID = ?";
            Object[] params = { "hirdanshu" };
            List<Map<String, Object>> results = databaseWrapper.executeQuery(selectQuery, params);

            // Process the results
            if (results != null) {
                for (Map<String, Object> row : results) {
                    int id = (int) row.get("id");
                    String enterpriseID = (String) row.get("enterpriseID");
                    String skills = (String) row.get("skills");

                    System.out.println("id: " + id + ", enterpriseID: " + enterpriseID + ", skills: " + skills);
                }
            } else {
                System.out.println("Query execution failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
