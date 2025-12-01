package com.vitvellore.bajaj_test;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BajajTestApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            RestTemplate restTemplate = new RestTemplate();

            // ----------------------------------------------------------------
            // STEP 1: Connect to the Server
            // ----------------------------------------------------------------
            String url1 = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> myDetails = new HashMap<>();
            myDetails.put("name", "Kishan S");
            myDetails.put("regNo", "22BCT0238");
            myDetails.put("email", "kishan.s2022@vitstudent.ac.in");

            HttpHeaders headers1 = new HttpHeaders();
            headers1.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request1 = new HttpEntity<>(myDetails, headers1);

            try {
                System.out.println(">>> Sending Step 1 Request...");
                ResponseEntity<String> response1 = restTemplate.postForEntity(url1, request1, String.class);
                String jsonResponse = response1.getBody();
                
                System.out.println("SERVER RESPONSE: " + jsonResponse);

                // --- MANUAL PARSING (Updated to catch "webhook") ---
                String token = extractValue(jsonResponse, "accessToken");
                if (token == null) token = extractValue(jsonResponse, "access_token");

                // I added the specific check for "webhook" here:
                String webhookUrl = extractValue(jsonResponse, "webhook"); 
                if (webhookUrl == null) webhookUrl = extractValue(jsonResponse, "webhookUrl");
                if (webhookUrl == null) webhookUrl = extractValue(jsonResponse, "webhook_url");
                if (webhookUrl == null) webhookUrl = extractValue(jsonResponse, "url");

                if (webhookUrl == null || token == null) {
                    System.err.println("CRITICAL ERROR: Could not find 'webhook' or 'token'. Check Server Response above.");
                    return;
                }

                System.out.println("Step 1 Success.");
                System.out.println("Token: " + token.substring(0, 10) + "...");
                System.out.println("Webhook: " + webhookUrl);

                // ----------------------------------------------------------------
                // STEP 2: Submit the SQL Answer
                // ----------------------------------------------------------------
                String sqlAnswer = "SELECT d.DEPARTMENT_NAME, " +
                        "AVG(TIMESTAMPDIFF(YEAR, e.DOB, NOW())) AS AVERAGE_AGE, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) SEPARATOR ', '), ', ', 10) AS EMPLOYEE_LIST " +
                        "FROM DEPARTMENT d " +
                        "JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                        "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
                        "WHERE p.AMOUNT > 70000 " +
                        "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                        "ORDER BY d.DEPARTMENT_ID DESC;";

                Map<String, String> submissionBody = new HashMap<>();
                submissionBody.put("finalQuery", sqlAnswer);

                HttpHeaders headers2 = new HttpHeaders();
                headers2.set("Authorization", token);
                headers2.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, String>> request2 = new HttpEntity<>(submissionBody, headers2);
                
                System.out.println(">>> Sending Final Answer to: " + webhookUrl);
                ResponseEntity<String> finalResponse = restTemplate.postForEntity(webhookUrl, request2, String.class);
                
                System.out.println("************************************************");
                System.out.println(">>> MISSION COMPLETE <<<");
                System.out.println("Server Reply: " + finalResponse.getBody());
                System.out.println("************************************************");

            } catch (Exception e) {
                System.out.println("ERROR OCCURRED: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    // Helper method to extract values
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) return null;
        
        return json.substring(startIndex, endIndex);
    }
}