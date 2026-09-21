package org.example.activity;

import java.time.Duration;
import java.time.Instant;

public class ApplicationActivityTracker {

    private final ActiveWindowService activeWindowService;
    private final ApplicationActivitySender activitySender;

    private final String employeeCode;
    private final String deviceId;

    private String currentProcessName;
    private int currentProcessId;
    private String currentWindowTitle;
    private Instant activityStartedAt;

    public ApplicationActivityTracker(
            ActiveWindowService activeWindowService,
            ApplicationActivitySender activitySender,
            String employeeCode,
            String deviceId
    ) {
        this.activeWindowService = activeWindowService;
        this.activitySender = activitySender;
        this.employeeCode = employeeCode;
        this.deviceId = deviceId;
    }

    /**
     * Checks the currently active Windows application.
     */
    public ApplicationInfo checkActivity() {

        ActiveWindowService.ActiveWindowSnapshot snapshot =
                activeWindowService.getActiveWindowSnapshot();

        String processName =
                snapshot.getProcessName();

        String windowTitle =
                snapshot.getWindowTitle();

        int processId =
                snapshot.getProcessId();

        if (processName == null
                || processName.isBlank()) {

            return null;
        }

        /*
         * First application detected.
         */
        if (currentProcessName == null) {

            startNewActivity(
                    processName,
                    processId,
                    windowTitle
            );

            return null;
        }

        /*
         * Detect application/process change.
         */
        boolean applicationChanged =
                !currentProcessName.equalsIgnoreCase(
                        processName
                )
                        || currentProcessId != processId;

        /*
         * Detect window/title change.
         */
        boolean windowChanged =
                !currentWindowTitle.equals(
                        windowTitle
                );

        /*
         * Finish the previous activity when
         * either the application or window changes.
         */
        if (applicationChanged || windowChanged) {

            ApplicationInfo completedActivity =
                    finishCurrentActivity();

            startNewActivity(
                    processName,
                    processId,
                    windowTitle
            );

            return completedActivity;
        }

        return null;
    }

    /**
     * Starts tracking a new activity.
     */
    private void startNewActivity(
            String processName,
            int processId,
            String windowTitle
    ) {

        currentProcessName = processName;
        currentProcessId = processId;
        currentWindowTitle = windowTitle;
        activityStartedAt = Instant.now();

        System.out.println();
        System.out.println("APPLICATION STARTED");

        System.out.println(
                "Process: "
                        + currentProcessName
        );

        System.out.println(
                "Process ID: "
                        + currentProcessId
        );

        System.out.println(
                "Window: "
                        + currentWindowTitle
        );

        System.out.println(
                "Started At: "
                        + activityStartedAt
        );

        System.out.println(
                "------------------------------------------------"
        );
    }

    /**
     * Finishes the current activity and creates
     * an ApplicationInfo object.
     */
    private ApplicationInfo finishCurrentActivity() {

        if (activityStartedAt == null) {
            return null;
        }

        Instant activityEndedAt =
                Instant.now();

        long durationSeconds =
                Duration.between(
                        activityStartedAt,
                        activityEndedAt
                ).getSeconds();

        ApplicationInfo activity =
                new ApplicationInfo(
                        currentProcessName,
                        currentProcessId,
                        currentWindowTitle,
                        activityStartedAt,
                        activityEndedAt,
                        durationSeconds
                );

        System.out.println();
        System.out.println("APPLICATION ENDED");

        System.out.println(
                "Process: "
                        + activity.getProcessName()
        );

        System.out.println(
                "Process ID: "
                        + activity.getProcessId()
        );

        System.out.println(
                "Window: "
                        + activity.getWindowTitle()
        );

        System.out.println(
                "Started At: "
                        + activity.getStartedAt()
        );

        System.out.println(
                "Ended At: "
                        + activity.getEndedAt()
        );

        System.out.println(
                "Duration: "
                        + formatDuration(
                        activity.getDurationSeconds()
                )
        );

        System.out.println(
                "Duration Seconds: "
                        + activity.getDurationSeconds()
        );

        System.out.println(
                "------------------------------------------------"
        );

        return activity;
    }

    /**
     * Formats seconds as HH:mm:ss.
     */
    private String formatDuration(
            long totalSeconds
    ) {

        long hours =
                totalSeconds / 3600;

        long minutes =
                (totalSeconds % 3600) / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                "%02d:%02d:%02d",
                hours,
                minutes,
                seconds
        );
    }
}