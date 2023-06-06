package com.accenture.goss;
import org.apache.log4j.Logger;
/**
 * The MyLogger interface defines the contract for logging operations.
 * Implementing classes are responsible for providing the implementation
 * for logging messages at different log levels.
 */
public interface MyLogger {

    /**
     * Logs a general message.
     *
     * @param message the message to be logged
     */
    void log(String message);

    /**
     * Logs a debug message.
     *
     * @param message the message to be logged
     */
    void debug(String message);

    /**
     * Logs an info message.
     *
     * @param message the message to be logged
     */
    void info(String message);

    /**
     * Logs a warning message.
     *
     * @param message the message to be logged
     */
    void warn(String message);

    /**
     * Logs an error message.
     *
     * @param message the message to be logged
     */
    void error(String message);

    /**
     * Logs an error message with a throwable.
     *
     * @param message the message to be logged
     * @param t       the throwable associated with the error
     */
    void error(String message, Throwable t);
}

