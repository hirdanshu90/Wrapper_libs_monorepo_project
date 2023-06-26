package com.accenture.goss_authorisation.wrapper_classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

// Wrapper class.
public class JwtTokenGenerator {

    private final String loginEndpoint;

    public JwtTokenGenerator(String loginEndpoint) {
        this.loginEndpoint = loginEndpoint;
    }

    public String generateJwtToken(String email, String password) {
        try {
            String payload = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

            URL url = new URL(loginEndpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(payload.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                // Handle exception
                e.printStackTrace();
            }

            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                // Handle exception while reading response body
                e.printStackTrace();
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String jsonResponse = response.toString();
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    String jwtToken = jsonObject.getString("jwtToken");
                    String username = jsonObject.getString("username");

                    // Store the JWT Token and perform further actions

                    return jwtToken;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Handle the error response
                System.out.println("Error: " + response.toString());
            }

            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}