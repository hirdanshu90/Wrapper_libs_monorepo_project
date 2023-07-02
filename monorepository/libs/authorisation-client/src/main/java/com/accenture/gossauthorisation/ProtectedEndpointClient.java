package com.accenture.gossauthorisation;

import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.accenture.gossauthorisation.wrapperclasses.JwtTokenGenerator;

@SpringBootApplication
public class ProtectedEndpointClient {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedEndpointClient.class);
    private static final String BASE_URL = "http://localhost:8080";
    private static final String ENDPOINT_URL = BASE_URL + "/protected";

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ProtectedEndpointClient.class, args);

        JwtTokenGenerator jwtTokenGenerator = context.getBean(JwtTokenGenerator.class);
        String jwtToken = jwtTokenGenerator.generateToken("hirdanshu.vij@accenture.com", "hir");

        logger.info("Generated JWT Token: {}", jwtToken);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(ENDPOINT_URL);

        httpGet.setHeader("username", "hirdanshu.vij@accenture.com");
        httpGet.setHeader("Password", "hir");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            logger.info("Status Code: {}", statusCode);
            logger.info("Response Body: {}", responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
