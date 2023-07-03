                                                                   Documentation for the provided wrapper classes and their usage:

JwtTokenGenerator
The `JwtTokenGenerator` class is responsible for generating and validating JWT tokens. It has the following methods:

•	`generateToken(String username, String password)`
This method generates a JWT token based on the provided username and password. It uses the `JwtHelper` class to generate the token. If the token generation is successful, it returns the generated token; otherwise, it returns `null`.

•	`validateToken(String token, String username, String password)`
This method validates a JWT token by checking its authenticity and expiration. It also verifies the provided username and password. If the token is valid and the username and password match, it returns the validated username; otherwise, it returns `null`.

CreateUserWrapper
The `CreateUserWrapper` class is responsible for creating a user entity. It utilizes the `UserService` and `PasswordEncoder` to create and store the user details. The following methods are available:

•	`createUser(User userDto)`
This method takes a `User` object containing user details, such as name, email, password, and about. It generates a unique user ID, encrypts the password using the `PasswordEncoder`, and calls the `createUser` method of `UserService` to create and store the user entity.

JwtHelper
The `JwtHelper` class provides various utility methods for working with JWT tokens. It includes the following methods:

•	`getUsernameFromToken(String token)`
Retrieves the username from the JWT token.

•	`getExpirationDateFromToken(String token)`
Retrieves the expiration date from the JWT token.

•	`getClaimFromToken(String token, Function<Claims, T> claimsResolver)`
Retrieves a specific claim from the JWT token using a `claimsResolver` function.

•	`isTokenExpired(String token)`
Checks if the JWT token has expired.

•	`generateToken(UserDetails userDetails)`
Generates a JWT token for the provided `UserDetails` object. It includes the user's claims, subject (username), issued date, expiration date, and signs the token using a secret key.

•	`doGenerateToken(Map<String, Object> claims, String subject)`
Generates the JWT token using the provided claims and subject. It sets the issued date, expiration date, and signs the token with a secret key.

•	`validateToken(String token, UserDetails userDetails)`
Validates the JWT token by checking if it is authentic and not expired. It also compares the username in the token with the username from the `UserDetails` object.

•	`extractUsernameFromToken(String token)`
Extracts the username from the JWT token.

ProtectedEndpointClient
The `ProtectedEndpointClient` class demonstrates how to use the `JwtTokenGenerator` to generate a JWT token and make an authenticated request to a protected endpoint. It performs the following steps:

1. Retrieves an instance of the `JwtTokenGenerator` from the application context.
2. Calls the `generateToken` method of `JwtTokenGenerator` to generate a JWT token using a specific username and password.
3. Sends an HTTP GET request to the protected endpoint (`/protected`) with the generated token and additional headers (`username` and `Password`).
4. Handles the response from the protected endpoint and logs the status code and response body.

Overall,
These wrapper classes can be used in a Spring Boot application for handling JWT-based authentication and authorization. The `JwtTokenGenerator` class is responsible for token generation and validation, while the `CreateUserWrapper` class simplifies the process of creating a user entity. The `ProtectedEndpointClient` class demonstrates how to generate a token and make authenticated requests to protected endpoints.
