package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;

public class HikariCPConfig {
    // private static final String CONFIG_FILE = "HikariCP.properties";

    public static HikariConfig loadConfig(Properties properties) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(properties.getProperty("db.url"));
        config.setUsername(properties.getProperty("db.username"));
        config.setPassword(properties.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("max.pool.size")));
        // Set other HikariCP configuration properties as needed

        return config;
    }

    private static Properties loadProperties(String filename) {
        Properties properties = new Properties();
        try (InputStream input = HikariCPConfig.class.getClassLoader().getResourceAsStream(filename)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file: " + filename);
        }
        return properties;
    }

    public void validateProperties(Properties properties) {
        // Perform validation checks on the loaded properties
        if (properties.getProperty("db.url") == null) {
            throw new RuntimeException("Missing property: db.url");
        }
        // Perform validation for other required properties

        // Setting default values for optional properties if not present
        if (properties.getProperty("max.pool.size") == null) {
            properties.setProperty("max.pool.size", "20");
        }
        // Set default values for other optional properties
    }

    // Additional methods for SSL/TLS configuration

    public void enableSSL(HikariConfig config) {
        config.addDataSourceProperty("ssl", "true");
        // Set other SSL/TLS related properties
    }

    public void disableSSL(HikariConfig config) {
        config.addDataSourceProperty("ssl", "false");
        // Remove or reset other SSL/TLS related properties
    }

}
