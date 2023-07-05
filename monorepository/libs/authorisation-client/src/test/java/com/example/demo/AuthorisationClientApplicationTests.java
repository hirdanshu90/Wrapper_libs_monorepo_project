package com.example.demo;

import com.accenture.gossauthorisation.entities.User;
import com.accenture.gossauthorisation.security.JwtHelper;
import com.accenture.gossauthorisation.services.CustomUserDetailService;
import com.accenture.gossauthorisation.services.UserService;
import com.accenture.gossauthorisation.wrapperclasses.CreateUserWrapper;
import com.accenture.gossauthorisation.wrapperclasses.JwtTokenGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AuthorisationClientApplicationTests {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private JwtHelper jwtHelper;

    public AuthorisationClientApplicationTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_ValidUser_ReturnsCreatedUser() {
        // Arrange
        CreateUserWrapper createUserWrapper = new CreateUserWrapper(userService, passwordEncoder);
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("testpassword");

        when(userService.createUser(any(User.class))).thenReturn(user);

        // Act
        User createdUser = createUserWrapper.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), createdUser.getEmail());
    }

    @Test
    public void testValidateToken_ValidTokenAndCredentials_ReturnsUsername() {
        // Arrange
        JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator(jwtHelper, customUserDetailService);
        String token = "valid-token";
        String username = "john.doe@example.com";
        String password = "testpassword";

        when(jwtHelper.extractUsernameFromToken(token)).thenReturn(username);
        when(customUserDetailService.loadUserByUsername(username)).thenReturn(createUserDetails(username, password));
        when(passwordEncoder.matches(password, createUserDetails(username, password).getPassword())).thenReturn(true);
        when(jwtHelper.validateToken(token, createUserDetails(username, password))).thenReturn(true);

        // Act
        String validatedUsername = jwtTokenGenerator.validateToken(token, username, password);

        // Assert
        assertNotNull(validatedUsername);
        assertEquals(username, validatedUsername);
    }

    @Test
    public void testValidateToken_InvalidCredentials_ReturnsNull() {
        // Arrange
        JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator(jwtHelper, customUserDetailService);
        String token = "valid-token";
        String username = "john.doe@example.com";
        String password = "testpassword";

        when(jwtHelper.extractUsernameFromToken(token)).thenReturn(username);
        when(customUserDetailService.loadUserByUsername(username)).thenReturn(createUserDetails(username, password));
        when(passwordEncoder.matches(password, createUserDetails(username, password).getPassword())).thenReturn(false);

        // Act
        String validatedUsername = jwtTokenGenerator.validateToken(token, username, password);

        // Assert
        assertNull(validatedUsername);
    }

    private UserDetails createUserDetails(String username, String password) {
        // Implement this method based on your UserDetails implementation
        // Return a mock UserDetails object with the given username and password
        return null;
    }
}
