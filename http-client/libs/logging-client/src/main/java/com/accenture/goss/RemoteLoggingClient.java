package com.accenture.goss;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * The RemoteLoggingClient class implements the MyLogger interface for logging messages to a remote logging service
 * via HTTP POST requests. It provides methods to log messages at different log levels and sends the log messages
 * as JSON payloads to the remote service.
 */
public class RemoteLoggingClient implements MyLogger {

    private static final Logger logger = Logger.getLogger(RemoteLoggingClient.class);

    // Remote service URL
    private static final String REMOTE_LOGGING_URL = "https://com.example/logs";

    private final HttpClient httpClient;

    /**
     * Constructs a RemoteLoggingClient instance and initializes the HTTP client.
     */
    public RemoteLoggingClient() {
        // Create an instance of HttpClient
        this.httpClient = HttpClientBuilder.create().build();
    }

    /**
     * Logs a message by sending it as an HTTP POST request to the remote logging service.
     *
     * @param message the message to be logged
     */
    @Override
    public void log(String message) {
        try {
            // Create a POST request to the remote logging service
            HttpPost request = new HttpPost(REMOTE_LOGGING_URL);

            // Set the log message as the request body
            StringEntity requestBody = new StringEntity(message);
            request.setEntity(requestBody);

            // Execute the request
            HttpResponse response = httpClient.execute(request);

            // Check the response status if needed

            // Consume the response entity if needed
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // Consume the response entity to release the connection
                responseEntity.consumeContent();
            }
        } catch (Exception e) {
            // Handle any exceptions or log errors
            logger.error("Failed to send log message to remote service", e);
        }
    }

    /**
     * Logs a debug message.
     *
     * @param message the debug message to be logged
     */
    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    /**
     * Logs an info message.
     *
     * @param message the info message to be logged
     */
    @Override
    public void info(String message) {
        logger.info(message);
    }

    /**
     * Logs a warning message.
     *
     * @param message the warning message to be logged
     */
    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    /**
     * Logs an error message by sending it as a JSON payload to the remote logging service.
     *
     * @param message the error message to be logged
     */
    @Override
    public void error(String message) {
        // Create a JSON log object for the error message
        String eventName = "error";
        JSONObject logObject = new JSONObject();
        JSONObject eventData = new JSONObject();
        eventData.put("errorType", "IO EXCEPTION");
        eventData.put("code", "ERROR400");
        logObject.put("event", eventName);
        logObject.put("data", eventData);

        logger.error(logObject.toString());
    }

    /**
     * Logs an error message with a throwable.
     *
     * @param message the error message to be logged
     * @param t       the throwable associated with the error
     */
    @Override
    public void error(String message, Throwable t) {
        logger.error(message);
    }
}
