package com.example.logger;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.JSONObject;

/**
 * The FileLoggingClient class implements the MyLogger interface for logging messages to a file using log4j.
 * It provides methods to log messages at different log levels and configures a file appender to write logs to
 * a specified file.
 */
public class FileLoggingClient implements MyLogger {

    private static final Logger logger = Logger.getLogger(FileLoggingClient.class);

    /**
     * Constructs a FileLoggingClient instance and configures a file appender to write logs to a specified file.
     */
    public FileLoggingClient() {
        // Set log file location
        String logFilePath = "logs/app.log";

        // Create a file appender
        FileAppender fileAppender;
        try {
            fileAppender = new FileAppender(new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n"), logFilePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create FileAppender", e);
        }

        // Set log level
        fileAppender.setThreshold(Level.INFO);

        // Activate the options and add the appender to the logger
        fileAppender.activateOptions();
        logger.addAppender(fileAppender);
    }

    /**
     * Logs a message to the file.
     *
     * @param message the message to be logged
     */
    @Override
    public void log(String message) {
        logger.info(message);
    }

    /**
     * Logs a debug message to the file.
     *
     * @param message the debug message to be logged
     */
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * Logs an info message to the file.
     *
     * @param message the info message to be logged
     */
    @Override
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a warning message to the file.
     *
     * @param message the warning message to be logged
     */
    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    /**
     * Logs an error message to the file.
     *
     * @param message the error message to be logged
     */
    @Override
    public void error(String message) {
        // Create a JSON log object for error message
        String eventName = "error";
        JSONObject logObject = new JSONObject();
        JSONObject eventData = new JSONObject();
        eventData.put("errorType", "parsing error");
        eventData.put("code", "ERROR404");
        logObject.put("event", eventName);
        logObject.put("data", eventData);
        logger.error(logObject.toString());
    }

    /**
     * Logs an error message with a throwable to the file.
     *
     * @param message the error message to be logged
     * @param t       the throwable associated with the error
     */
    @Override
    public void error(String message, Throwable t) {
        logger.error(message);
    }
}

