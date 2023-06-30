// package com.example.demo;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.junit.jupiter.SpringExtension;

// import com.accenture.gossauthorisation.wrapperclasses.CreateUserEndpointWrapper;

// import static org.junit.jupiter.api.Assertions.*;

// @ExtendWith(SpringExtension.class)
// @SpringBootTest
// class AuthorisationClientApplicationTests {
//     private static final Logger logger = LogManager.getLogger(AuthorisationClientApplicationTests.class);

//     @Test
//     void testCreateUser_Success() {
//         String endpointUrl = "http://example.com/create-user";
//         CreateUserEndpointWrapper wrapper = new CreateUserEndpointWrapper(endpointUrl);

//         try {
//             logger.info("Running testCreateUser_Success");
//             assertDoesNotThrow(() -> wrapper.createUser("John Doe", "john@example.com", "password123", "About John"));
//         } catch (Exception e) {
//             logger.error("Error in testCreateUser_Success", e);
//             fail("An exception occurred: " + e.getMessage());
//         }
//     }

//     @Test
//     void testCreateUser_InvalidInputParameters() {
//         String endpointUrl = "http://example.com/auth/create-user";
//         CreateUserEndpointWrapper wrapper = new CreateUserEndpointWrapper(endpointUrl);

//         try {
//             logger.info("Running testCreateUser_InvalidInputParameters");

//             // Test when any of the input parameters is null or empty
//             assertThrows(IllegalArgumentException.class,
//                     () -> wrapper.createUser(null, "john@example.com", "password123", "About John"));

//             assertThrows(IllegalArgumentException.class,
//                     () -> wrapper.createUser("John Doe", "", "password123", "About John"));

//             // Add more test cases for empty input parameters

//             // Test when the 'about' parameter is optional (not required to be non-empty)
//             assertDoesNotThrow(() -> wrapper.createUser("John Doe", "john@example.com", "password123", ""));

//             logger.info("Completed testCreateUser_InvalidInputParameters");
//         } catch (Exception e) {
//             logger.error("Error in testCreateUser_InvalidInputParameters", e);
//             fail("An exception occurred: " + e.getMessage());
//         }
//     }

//     @Test
//     void testCreateUser_HttpRequestFailure() {
//         String endpointUrl = "http://example.com/invalid-url";
//         CreateUserEndpointWrapper wrapper = new CreateUserEndpointWrapper(endpointUrl);

//         try {
//             logger.info("Running testCreateUser_HttpRequestFailure");

//             // Test when the HTTP request fails (e.g., invalid URL, server unreachable)
//             assertThrows(Exception.class,
//                     () -> wrapper.createUser("John Doe", "john@example.com", "password123", "About John"));

//             logger.info("Completed testCreateUser_HttpRequestFailure");
//         } catch (Exception e) {
//             logger.error("Error in testCreateUser_HttpRequestFailure", e);
//             fail("An exception occurred: " + e.getMessage());
//         }
//     }

// }
