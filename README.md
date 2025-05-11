package com.bfhl.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BfhlApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BfhlApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Step 1: Send POST to generate webhook
            String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

            ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String webhookUrl = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                // Step 2: Solve the SQL problem (Question 1)
                String finalQuery = "SELECT department, COUNT(*) AS emp_count FROM employees GROUP BY department HAVING COUNT(*) > 5;";

                Map<String, String> answer = new HashMap<>();
                answer.put("finalQuery", finalQuery);

                HttpHeaders answerHeaders = new HttpHeaders();
                answerHeaders.setContentType(MediaType.APPLICATION_JSON);
                answerHeaders.setBearerAuth(accessToken);  // JWT token

                HttpEntity<Map<String, String>> answerEntity = new HttpEntity<>(answer, answerHeaders);

                // Step 3: Submit answer to the webhook
                ResponseEntity<String> result = restTemplate.postForEntity(webhookUrl, answerEntity, String.class);
                System.out.println("Answer submitted. Status: " + result.getStatusCode());
            } else {
                System.out.println("Failed to receive webhook or access token.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.bfhl.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BfhlApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BfhlApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // Step 1: Request webhook
            String generateWebhookUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "John Doe");
            requestBody.put("regNo", "REG12347");
            requestBody.put("email", "john@example.com");

            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
            ResponseEntity<Map> response = restTemplate.postForEntity(generateWebhookUrl, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String webhookUrl = (String) response.getBody().get("webhook");
                String accessToken = (String) response.getBody().get("accessToken");

                // Step 2: Send SQL solution
                Map<String, String> sqlPayload = new HashMap<>();
                sqlPayload.put("finalQuery", "SELECT department, COUNT(*) AS emp_count FROM employees GROUP BY department HAVING COUNT(*) > 5;");

                HttpHeaders answerHeaders = new HttpHeaders();
                answerHeaders.setContentType(MediaType.APPLICATION_JSON);
                answerHeaders.setBearerAuth(accessToken);

                HttpEntity<Map<String, String>> answerEntity = new HttpEntity<>(sqlPayload, answerHeaders);
                restTemplate.postForEntity(webhookUrl, answerEntity, String.class);
            } else {
                System.out.println("Error in webhook response.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
