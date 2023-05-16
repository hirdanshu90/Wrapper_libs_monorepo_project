package com.example.logger;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.JSONObject;

/**
 * The ConsoleLoggingClient class implements the MyLogger interface for logging messages to the console. It uses the
 * log4j library to configure a console appender and provides methods to log messages at different log levels.
 */
public class ConsoleLoggingClient implements MyLogger {

    private static final Logger logger = Logger.getLogger(ConsoleLoggingClient.class);

    /**
     * Constructs a ConsoleLoggingClient instance and configures a console appender for logging to the console.
     */
    public ConsoleLoggingClient() {
        // Set log format to a custom pattern
        PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");

        // Create a console appender
        ConsoleAppender consoleAppender = new ConsoleAppender(layout);

        // Set log level
        consoleAppender.setThreshold(Level.INFO);

        // Activate the options and add the appender to the logger
        consoleAppender.activateOptions();
        logger.addAppender(consoleAppender);
    }

    /**
     * Logs a message to the console.
     *
     * @param message the message to be logged
     */
    @Override
    public void log(String message) {
        logger.info(message);
    }

    /**
     * Logs a debug message to the console.
     *
     * @param message the debug message to be logged
     */
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * Logs an info message to the console.
     *
     * @param message the info message to be logged
     */
    @Override
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a warning message to the console.
     *
     * @param message the warning message to be logged
     */
    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    /**
     * Logs an error message to the console as a JSON payload.
     *
     * @param message the error message to be logged
     */
    @Override
    public void error(String message) {
        // Create a JSON log object for the error message
        String eventName = "error";
        JSONObject logObject = new JSONObject();
        JSONObject eventData = new JSONObject();
        eventData.put("errorType", "null pointer exception");
        eventData.put("code", "ERROR401");
        logObject.put("event", eventName);
        logObject.put("data", eventData);

        logger.error(logObject.toString());
    }

    /**
     * Logs an error message with a throwable to the console.
     *
     * @param message the error message to be logged
     * @param t       the throwable associated with the error
     */
    @Override
    public void error(String message, Throwable t) {
        logger.error(message);
    }
}

