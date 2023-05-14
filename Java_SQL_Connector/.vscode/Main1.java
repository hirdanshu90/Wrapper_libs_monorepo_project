package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main1 {
    public static void main(String[] args) {
        ConnectorVersion1 connector = ConnectorVersion1.getInstance();

        try {
            // Example of executing multiple queries in a transaction
            String[] sqlStatements = {

                    "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (1001, 'testing123', 'JDBC')",
                    "UPDATE goss_skill.skiils_table SET skills = 'Python' WHERE ID = 30",
                    "DELETE FROM goss_skill.skiils_table WHERE ID = 5",

            };

            connector.executeTransaction(sqlStatements);
            System.out.println("Transaction executed successfully.");

            // Example of executing multiple independent queries
            String selectQuery = "SELECT * FROM goss_skill.skiils_table";
            String updateQuery = "UPDATE goss_skill.skiils_table SET EnterpriseID ='someone123' where id = 6";

            ResultSet resultSet = connector.select(selectQuery);
            // ResultSet resultSet1 = connector.select(updateQuery);

            int rowsAffected = connector.update(updateQuery);
            System.out.println("Number of rows updated: " + rowsAffected);
        } catch (SQLException e) {
            System.out.println("An error occurred while executing queries: " + e.getMessage());
            // Handle the exception...
        }
    }
}
