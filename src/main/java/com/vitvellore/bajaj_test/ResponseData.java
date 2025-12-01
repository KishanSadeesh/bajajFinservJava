package com.vitvellore.bajaj_test;

// This class helps us read the server's response
public class ResponseData {
    private String webhookUrl;
    private String accessToken;

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}