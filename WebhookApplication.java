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
    private static final String NAME = "Your Name";
    private static final String REG_NO = "REG12347";
    private static final String EMAIL = "your-email@example.com";

    public static void main(String[] args) {
        SpringApplication.run(WebhookApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
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
                String finalSqlQuery = REG_NO.endsWith("7") ? getSqlForQuestion1() : getSqlForQuestion2();
                submitFinalQuery(webhookUrl, accessToken, finalSqlQuery);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private String getSqlForQuestion1() {
        return "SELECT name, email FROM users WHERE active = 1;";
    }

    private String getSqlForQuestion2() {
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
            restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
        } catch (Exception e) {
            System.err.println("Submission error: " + e.getMessage());
        }
    }
}
