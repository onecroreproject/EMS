package org.example.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AgentConfig {

    private static final String CONFIG_DIRECTORY =
            "C:\\ProgramData\\EmployeeAgent";

    private static final String CONFIG_FILE =
            "config.properties";

    private final Properties properties = new Properties();

    public AgentConfig() {
        loadConfig();
    }

    private void loadConfig() {

        Path configPath = Paths.get(
                CONFIG_DIRECTORY,
                CONFIG_FILE
        );

        if (!Files.exists(configPath)) {
            System.out.println(
                    "Configuration file not found: " + configPath
            );
            return;
        }

        try (InputStream inputStream =
                     Files.newInputStream(configPath)) {

            properties.load(inputStream);

            System.out.println(
                    "Configuration loaded successfully."
            );

        } catch (IOException e) {
            System.out.println(
                    "Unable to load configuration: "
                            + e.getMessage()
            );
        }
    }

    public String getServerUrl() {

        String serverUrl =
                properties.getProperty("server.url");

        if (serverUrl == null || serverUrl.trim().isEmpty()) {
            throw new IllegalStateException(
                    "server.url is missing in config.properties"
            );
        }

        return serverUrl.trim();
    }

    public int getRequiredWorkHours() {

        return Integer.parseInt(
                properties.getProperty(
                        "workday.required-hours",
                        "9"
                ).trim()
        );
    }

    public int getIdleGraceMinutes() {

        return Integer.parseInt(
                properties.getProperty(
                        "idle.grace-minutes",
                        "2"
                ).trim()
        );
    }



}