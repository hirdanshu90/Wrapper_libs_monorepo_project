package com.accenture.goss_authorisation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.accenture.goss_authorisation.wrapper_classes.CreateUserEndpointWrapper;
import com.accenture.goss_authorisation.wrapper_classes.JwtTokenGenerator;

import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootApplication
public class LibraryApplicationMain {

	private static final String BASE_URL = "http://localhost:8080";

	public static void main(String[] args) {

		SpringApplication.run(LibraryApplicationMain.class, args);
		String loginEndpoint = BASE_URL + "/auth/login";
		JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator(loginEndpoint);

		// Specify the email and password
		String email = "hirdanshu.vij@accenture.com";
		String password = "hir";

		// Generate the JWT token
		String jwtToken = jwtTokenGenerator.generateJwtToken(email, password);
		System.out.println("JWT token: " + jwtToken);

		String endpointUrl = "http://localhost:8080/auth/create-user";
		CreateUserEndpointWrapper wrapper = new CreateUserEndpointWrapper(endpointUrl);

		String name = "John";
		String email_user = "john@accenture.com";
		String password_user = "joh";
		String about = "testing123";

		try {
			wrapper.createUser(name, email_user, password_user, about);
		} catch (IllegalArgumentException e) {
			System.err.println("Invalid input parameters: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Failed to create user: " + e.getMessage());
		}

		System.out.println("------------");

		String endpoint = "http://localhost:8080/home/users";
		String jwtToken_used = jwtToken;
		String username_used = email_user;
		String password_used = password_user;

		// Create the basic authentication header value
		String auth = username_used + ":" + password_used;
		String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
		String authHeader = "Basic " + encodedAuth;

		// Create HttpClient
		HttpClient httpClient = HttpClient.newHttpClient();

		// Create HttpRequest
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(endpoint))
				.header("Authorization", "Bearer " + jwtToken_used)
				.header("Content-Type", "application/json")
				.header("Authorization", authHeader) // Add the basic authentication header
				.build();

		// Send the request and retrieve the response
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			// Check the response status code
			int statusCode = response.statusCode();
			System.out.println("Response Status Code: " + statusCode);

			// Print the response body
			String responseBody = response.body();
			System.out.println("Response Body: " + responseBody);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
