package com.example;


import java.sql.SQLException;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    ConnectorVersion1 connector = ConnectorVersion1.getInstance();

    // Execute queries from QueryManager
    for (String query : QueryManager.QUERIES) {
      try {
        int rowsAffected = connector.executeUpdate(query);
        System.out.println("Rows affected: " + rowsAffected);
      } catch (SQLException e) {
        logger.error("Error executing query: " + query, e);
      }
    }

    // Example: Select query
    String selectQuery = "SELECT id, enterpriseID, skills FROM goss_skill.skiils_table where id =78";

    try {
      List<Map<String, Object>> results = connector.executeQuery(selectQuery);
      if (!results.isEmpty()) {
        for (Map<String, Object> row : results) {
          int id = row.get("id") != null ? (int) row.get("id") : 0;
          String enterpriseId = row.get("enterpriseID") != null ? (String) row.get("enterpriseID") : "";
          String skills = row.get("skills") != null ? (String) row.get("skills") : "";

          System.out.println("ID: " + id + ", Enterprise ID: " + enterpriseId + ", Skills: " + skills);
        }
        System.out.println("Transactions executed successfully.");
      } else {
        System.out.println("No results found.");
      }
    } catch (SQLException e) {
      logger.error("Error executing select query: " + selectQuery, e);
    }
  }
}
