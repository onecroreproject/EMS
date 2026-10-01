package org.example.ota.updater;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AgentUpdater {

    private static final String INSTALLER_PROCESS =
            "msiexec.exe";

    private static final Path LOG_DIRECTORY =
            Path.of(
                    "C:\\ProgramData\\EmployeeAgent\\logs"
            );

    private static final Path UPDATE_LOG =
            LOG_DIRECTORY.resolve(
                    "updater.log"
            );


    public static void main(String[] args) {

        log(
                "========================================"
        );

        log(
                "Employee Monitoring Agent Updater"
        );

        log(
                "Updater started."
        );


        /*
         * Expected arguments:
         *
         * args[0] = MSI installer path
         * args[1] = current Agent PID
         * args[2] = Agent executable path
         */
        if (args.length < 3) {

            log(
                    "Usage: AgentUpdater "
                            + "<installer> <parentPid> <agentExe>"
            );

            return;
        }


        Path installer =
                Path.of(
                        args[0]
                );


        long parentPid;

        try {

            parentPid =
                    Long.parseLong(
                            args[1]
                    );

        } catch (NumberFormatException e) {

            log(
                    "Invalid parent PID: "
                            + args[1]
            );

            return;
        }


        Path agentExe =
                Path.of(
                        args[2]
                );


        try {

            /*
             * =========================================
             * WAIT FOR CURRENT AGENT
             * =========================================
             */

            waitForParentProcess(
                    parentPid
            );


            log(
                    "Agent process has stopped."
            );


            /*
             * =========================================
             * CHECK MSI
             * =========================================
             */

            if (!Files.exists(installer)) {

                log(
                        "Installer does not exist: "
                                + installer
                );

                return;
            }


            log(
                    "Installer found: "
                            + installer
            );


            /*
             * =========================================
             * INSTALL MSI
             * =========================================
             */

            log(
                    "Starting MSI installation: "
                            + installer
            );


            Process installerProcess =
                    new ProcessBuilder(
                            INSTALLER_PROCESS,
                            "/i",
                            installer.toString(),
                            "/qn",
                            "/norestart"
                    )
                            .inheritIO()
                            .start();


            int exitCode =
                    installerProcess.waitFor();


            log(
                    "MSI exit code: "
                            + exitCode
            );


            /*
             * Windows Installer:
             *
             * 0    = success
             * 3010 = success, reboot required
             *
             * Since /norestart is used, both are
             * treated as successful installation.
             */

            if (exitCode != 0
                    && exitCode != 3010) {

                log(
                        "MSI installation failed."
                );

                return;
            }


            log(
                    "MSI installation completed."
            );


            /*
             * =========================================
             * DELETE DOWNLOADED MSI
             * =========================================
             */

            try {

                Files.deleteIfExists(
                        installer
                );

                log(
                        "Downloaded installer deleted."
                );

            } catch (Exception e) {

                log(
                        "Could not delete installer: "
                                + e.getMessage()
                );
            }


            /*
             * =========================================
             * START UPDATED AGENT
             * =========================================
             */

            restartAgent(
                    agentExe
            );


            log(
                    "Updater completed successfully."
            );


        } catch (Exception e) {

            log(
                    "Updater failed: "
                            + e.getMessage()
            );

            e.printStackTrace();
        }
    }


    /*
     * =========================================================
     * WAIT FOR CURRENT AGENT
     * =========================================================
     */

    private static void waitForParentProcess(
            long parentPid)
            throws InterruptedException {

        log(
                "Waiting for Agent process "
                        + parentPid
                        + " to exit..."
        );


        while (
                ProcessHandle.of(parentPid)
                        .map(ProcessHandle::isAlive)
                        .orElse(false)
        ) {

            Thread.sleep(
                    1000
            );
        }
    }


    /*
     * =========================================================
     * RESTART UPDATED AGENT
     * =========================================================
     */

    private static void restartAgent(
            Path agentExe)
            throws IOException {

        if (!Files.exists(agentExe)) {

            log(
                    "Agent executable not found: "
                            + agentExe
            );

            return;
        }


        log(
                "Starting updated Agent: "
                        + agentExe
        );


        new ProcessBuilder(
                agentExe.toString()
        )
                .start();
    }


    /*
     * =========================================================
     * UPDATER LOG
     * =========================================================
     */

    private static synchronized void log(
            String message) {

        try {

            Files.createDirectories(
                    LOG_DIRECTORY
            );


            String line =
                    java.time.LocalDateTime.now()
                            + " | "
                            + message
                            + System.lineSeparator();


            Files.writeString(
                    UPDATE_LOG,
                    line,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.APPEND
            );

        } catch (Exception ignored) {

            /*
             * Updater must continue even if
             * logging fails.
             */
        }


        System.out.println(
                message
        );
    }
}