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

            // Silent success for heartbeat.

        } catch (Exception e) {

            // Too noisy to print every 10 seconds, but we can print a single line
            // or just suppress it entirely. Let's make it a WARN if we must print.
            System.out.println("WARN  Heartbeat failed: " + e.getMessage());
        }
    }

}
