package org.example.heartbeat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HeartbeatService {

    private final HttpClient httpClient;
    private final String serverUrl;
    private final String deviceId;

    public HeartbeatService(String serverUrl, String deviceId) {
        this.httpClient = HttpClient.newHttpClient();
        this.serverUrl = serverUrl;
        this.deviceId = deviceId;
    }

    public void sendHeartbeat() {

        try {

            String url = serverUrl
                    + "/api/agent/heartbeat?deviceId="
                    + deviceId;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println("Heartbeat Response:");
            System.out.println("HTTP Status: " + response.statusCode());
            System.out.println("Response: " + response.body());

        } catch (Exception e) {

            System.out.println("Heartbeat failed: "
                    + e.getMessage());
        }
    }

}
