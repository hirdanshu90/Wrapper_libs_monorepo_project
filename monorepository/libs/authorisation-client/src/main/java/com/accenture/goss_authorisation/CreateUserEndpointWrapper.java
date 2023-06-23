package com.accenture.goss_authorisation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CreateUserEndpointWrapper {
    private final String endpointUrl;

    public CreateUserEndpointWrapper(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public void createUser(String name, String email, String password, String about) throws Exception {
        // Validate input
        if (name == null || name.isEmpty() || email == null || email.isEmpty()
                || password == null || password.isEmpty() || about == null || about.isEmpty()) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String payload = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\",\"about\":\"%s\"}",
                name, email, password, about);

        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpointUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (var outputStream = connection.getOutputStream()) {
                outputStream.write(payload.getBytes());
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("User created successfully");
            } else {
                // Read error response, if any
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    errorResponse.append(line);
                }
                reader.close();

                throw new Exception("Failed to create user. Response code: " + responseCode + ", Response: " +
                        errorResponse.toString());
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}


