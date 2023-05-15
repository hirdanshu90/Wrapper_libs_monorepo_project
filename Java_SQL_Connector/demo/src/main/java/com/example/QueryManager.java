package com.example;

class QueryManager {
    public static final String[] QUERIES = {
            "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (10000, 'tested1', 'React')",
            "UPDATE goss_skill.skiils_table SET skills = 'Python' WHERE ID = 308",
            "DELETE FROM goss_skill.skiils_table WHERE ID = 89",
    };
}