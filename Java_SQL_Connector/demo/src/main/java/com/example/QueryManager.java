package com.example;

class QueryManager {
    public static final String[] QUERIES = {
            "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (100, 'tested123', 'React')",
            "UPDATE goss_skill.skiils_table SET skills = 'Python' WHERE ID = 30",
            "DELETE FROM goss_skill.skiils_table WHERE ID = 5",
            "INSERT INTO goss_skill.skiils_table (ID, enterpriseID, skills) VALUES (1003, 'tested123', 'React')",
            "UPDATE goss_skill.skiils_table SET skills = 'Python' WHERE ID = 303",
            "DELETE FROM goss_skill.skiils_table WHERE ID = 55",
    };
}
