package com.accenture.gossauthorisation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.accenture.gossauthorisation.wrapperclasses.JwtTokenGenerator;

@RestController
@RequestMapping("/protected")
public class ProtectedController {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @GetMapping
    public ResponseEntity<String> protectedEndpoint(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestHeader("username") String username,
            @RequestHeader("Password") String password) {
        // Extract the token from the Authorization header
        String token = extractToken(authorizationHeader);
        System.out.println(token);

        // Verify the token and retrieve the username
        String validatedUsername = jwtTokenGenerator.validateToken(token, username, password);
        System.out.println("-------------------");
        System.out.println(validatedUsername);
        System.out.println(username);
        System.out.println(password);

        // Perform authentication logic based on the provided username and password
        if (validatedUsername != null && validatedUsername.equals(username)) {
            // User is authenticated, proceed with the desired logic
            return ResponseEntity.ok("This is a protected endpoint!, code is running okay.........................");
        } else {
            // Invalid token or authentication failed
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or authentication failed");
        }
    }

    // Helper method to extract the token from the Authorization header
    private String extractToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
