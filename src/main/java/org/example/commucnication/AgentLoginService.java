package org.example.commucnication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AgentLoginService {

    private final HttpClient httpClient;
    private final String serverUrl;

    public AgentLoginService(String serverUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.serverUrl = serverUrl;
    }

    public LoginResult login(String username, String password) {

        try {

            String url = serverUrl + "/api/agent/login";

            String requestBody = """
                    {
                        "username": "%s",
                        "password": "%s"
                    }
                    """.formatted(username, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println("Agent Login Response:");
            System.out.println("HTTP Status: " + response.statusCode());
            System.out.println("Response: " + response.body());

            if (response.statusCode() != 200) {

                return new LoginResult(
                        false,
                        null,
                        null
                );
            }

            String responseBody = response.body();

            String employeeId =
                    extractValue(responseBody, "employeeId");

            String employeeCode =
                    extractValue(responseBody, "employeeCode");

            return new LoginResult(
                    true,
                    employeeId,
                    employeeCode
            );

        } catch (Exception e) {

            System.out.println(
                    "Agent login failed: " + e.getMessage()
            );

            return new LoginResult(
                    false,
                    null,
                    null
            );
        }
    }

    private String extractValue(
            String json,
            String key) {

        String searchKey = "\"" + key + "\":\"";

        int startIndex =
                json.indexOf(searchKey);

        if (startIndex == -1) {
            return null;
        }

        startIndex += searchKey.length();

        int endIndex =
                json.indexOf("\"", startIndex);

        if (endIndex == -1) {
            return null;
        }

        return json.substring(
                startIndex,
                endIndex
        );
    }

    public static class LoginResult {

        private final boolean success;
        private final String employeeId;
        private final String employeeCode;

        public LoginResult(
                boolean success,
                String employeeId,
                String employeeCode) {

            this.success = success;
            this.employeeId = employeeId;
            this.employeeCode = employeeCode;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public String getEmployeeCode() {
            return employeeCode;
        }
    }
}