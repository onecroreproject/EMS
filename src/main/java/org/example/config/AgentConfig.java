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

        Path configDirectory = Paths.get(CONFIG_DIRECTORY);
        Path configPath = configDirectory.resolve(CONFIG_FILE);

        try {

            /*
             * 1. If external configuration exists,
             *    always use it.
             *
             * This means existing user configuration
             * will NEVER be overwritten.
             */
            if (Files.exists(configPath)) {

                loadFromFile(configPath);

                System.out.println(
                        "Configuration loaded from: "
                                + configPath
                );

                return;
            }

            /*
             * 2. Configuration does not exist.
             *    Create the ProgramData directory.
             */
            Files.createDirectories(configDirectory);

            /*
             * 3. Load the default configuration
             *    packaged inside the application JAR.
             */
            try (InputStream inputStream =
                         AgentConfig.class.getResourceAsStream(
                                 "/config.properties"
                         )) {

                if (inputStream == null) {

                    throw new IllegalStateException(
                            "Default config.properties was not found "
                                    + "inside the application."
                    );
                }

                /*
                 * 4. Create the external configuration file.
                 *
                 * Files.copy() does not overwrite an existing file.
                 */
                Files.copy(
                        inputStream,
                        configPath
                );
            }

            /*
             * 5. Load the newly created configuration.
             */
            loadFromFile(configPath);

            System.out.println(
                    "Default configuration created at: "
                            + configPath
            );

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Unable to initialize configuration: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private void loadFromFile(Path configPath)
            throws IOException {

        try (InputStream inputStream =
                     Files.newInputStream(configPath)) {

            properties.load(inputStream);
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

    public long getAutoWorkEndIdleMinutes() {

        String value =
                properties.getProperty(
                        "idle.auto-work-end-minutes",
                        "60"
                );

        try {

            long minutes =
                    Long.parseLong(value);

            return Math.max(1L, minutes);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid idle.auto-work-end-minutes value: "
                            + value
                            + ". Using default: 60 minutes."
            );

            return 60L;
        }
    }
}