# -BFHL-Webhook-SQL-Submission
// Complete Spring Boot Project for BFHL Qualifier

// 1. Main Application (Webhook and SQL Submission)
package com.bfhl.webhook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class WebhookApplication {

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    private static final String NAME = "Your Name";
    private static final String REG_NO = "REG12347";
    private static final String EMAIL = "your-email@example.com";

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            // Generate webhook on startup
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", NAME);
            requestBody.put("regNo", REG_NO);
            requestBody.put("email", EMAIL);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(GENERATE_WEBHOOK_URL, requestEntity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("webhook") && responseBody.containsKey("accessToken")) {
                String webhookUrl = (String) responseBody.get("webhook");
                String accessToken = (String) responseBody.get("accessToken");

                // Prepare SQL based on regNo (odd for Q1, even for Q2)
                String finalSqlQuery = REG_NO.endsWith("7") ? getSqlForQuestion1() : getSqlForQuestion2();
                submitFinalQuery(webhookUrl, accessToken, finalSqlQuery);
            } else {
                System.out.println("Error: Webhook URL or Access Token not received");
            }
        } catch (Exception e) {
            System.err.println("Error during webhook generation: " + e.getMessage());
        }
    }

    private String getSqlForQuestion1() {
        // Actual SQL for Question 1 (Replace this with the correct SQL query)
        return "SELECT name, email FROM users WHERE active = 1;";
    }

    private String getSqlForQuestion2() {
        // Actual SQL for Question 2 (Replace this with the correct SQL query)
        return "SELECT department, COUNT(*) FROM employees GROUP BY department;";
    }

    private void submitFinalQuery(String webhookUrl, String accessToken, String finalQuery) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
            System.out.println("Response from Webhook: " + response.getBody());
        } catch (Exception e) {
            System.err.println("Error during SQL submission: " + e.getMessage());
        }
    }
}

// Maven pom.xml (for JAR packaging)

/*
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.bfhl</groupId>
    <artifactId>webhook</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
*/
