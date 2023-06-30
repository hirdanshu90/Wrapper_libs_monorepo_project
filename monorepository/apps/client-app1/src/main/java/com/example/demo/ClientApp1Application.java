package com.example.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.accenture.gossauthorisation.wrapperclasses.CreateUserEndpointWrapper;
import com.accenture.gossauthorisation.wrapperclasses.JwtTokenGenerator;

@SpringBootApplication(scanBasePackages = { "com.accenture.gossauthorisation"
})
public class ClientApp1Application {

  public static void main(String[] args) {

    SpringApplication.run(ClientApp1Application.class, args);
  }
}