package com.accenture.goss;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.ErrorCode;
import org.json.JSONObject;
//import java.util.String;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The LoggingClient class demonstrates log rotation and structured logging using Log4j.
 */
@SpringBootApplication
public class LoggingClient {

	 /**
     * The logger instance for LoggingClient class.
     */
    private static final Logger logger = Logger.getLogger(LoggingClient.class);

    /**
     * Logs an event with a given name and associated data.
     *
     * @param eventName  the name of the event
     * @param eventData  the data associated with the event
     */
    public void logEvent(String eventName, JSONObject eventData) {
        JSONObject logObject = new JSONObject();
        logObject.put("event", eventName);
        logObject.put("data", eventData);

        logger.info(logObject.toString());
    }
    
    /**
     * The entry point of the LoggingClient application.
     *
     * @param args  the command line arguments
     */
    public static void main(String[] args) {
        
    	//PropertyConfigurator.configure("log4j.properties");
    	// Configure log rotation settings
        configureLogRotation();
    	
    	// Set log level to INFO
        logger.setLevel(Level.INFO);
        
        // Set log format to a custom pattern
        PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");
        logger.addAppender(new ConsoleAppender(layout));
        
		
		  // Set log filelocation and maximum file size 
        //RollingFileAppender appender = new RollingFileAppender();
        //appender.setName("RollingFileAppender");
        //appender.setFile("app.log");
        //appender.setMaxFileSize("50KB"); // Set the maximum file size (e.g., 50KB)
        //appender.setMaxBackupIndex(10); // Set the maximum number of backup files

		 
                
        
		
		  // Log some messages 
		  for (int i = 0; i < 10000; i++) {
		  logger.info("Logging message " + i); }
		 
        
        //Support structured logging:
        LoggingClient loggingClient = new LoggingClient();

        JSONObject eventData = new JSONObject();
        eventData.put("user", "john_doe");
        eventData.put("action", "login");

        loggingClient.logEvent("UserAction", eventData);
        
        
        logger.debug("This message won't be logged because log level is set to INFO");
        logger.info("This is an informational message");
        logger.warn("This is a warning message");
        logger.error("This is an error message");
        logger.fatal("This is a fatal message");
        
        // Perform logging through custom loggers
        MyLogger remoteLogging = new RemoteLoggingClient();
        remoteLogging.debug("debug in remote logging");
        remoteLogging.info("info in remote logging");
        remoteLogging.log("log in remote logging");
        remoteLogging.error("error in remote logging");
        
        MyLogger fileLogging = new FileLoggingClient();
        fileLogging.debug("debug in file logging");
        fileLogging.info("info in file logging");
        fileLogging.log("log in file logging");
        fileLogging.error("error in file logging");
        
        MyLogger consoleLogging = new ConsoleLoggingClient();
        consoleLogging.debug("debug in console logging");
        consoleLogging.info("info in console logging");
        consoleLogging.log("log in console logging");
        consoleLogging.error("error in console logging");
        
        
        SpringApplication.run(LoggingClient.class, args);        
      }

    
    /**
     * Configures log rotation settings for the RollingFileAppender.
     */
    private static void configureLogRotation() {
        // Set the log file path and name
        String logFilePath = "logFile.log";

        // Set the log message layout pattern
        String logPattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1} - %m%n";

        // Create a custom rolling file appender
        RollingFileAppender appender = new RollingFileAppender() {
            @Override
            public void rollOver() {
                // Override the rollOver method to change the file name
                super.rollOver();

                try {
                    setFile(getFile(), true, bufferedIO, bufferSize);
                } catch (IOException e) {
                    errorHandler.error("Error occurred while rolling over the log file.", e, ErrorCode.FILE_OPEN_FAILURE);
                }
            }
        };

        appender.setName("RollingFileAppender");
        appender.setFile(logFilePath);

        // Set the log message layout
        PatternLayout layout = new PatternLayout(logPattern);
        appender.setLayout(layout);

        // Set the log level
        appender.setThreshold(Level.INFO);

        // Set the append flag to true
        appender.setAppend(true);

        // Set the maximum file size (e.g., 500KB)
        appender.setMaxFileSize("200KB");

        // Set the maximum number of backup files
        appender.setMaxBackupIndex(10);

        // Activate the options and add the appender to the root logger
        appender.activateOptions();
        Logger.getRootLogger().addAppender(appender);
    }


}



