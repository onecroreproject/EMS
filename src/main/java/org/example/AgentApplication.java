package org.example;

import org.example.activity.ActiveWindowService;
import org.example.activity.ApplicationActivitySender;
import org.example.activity.ApplicationActivityTracker;
import org.example.activity.ApplicationInfo;
import org.example.activity.UserActivityMonitor;
import org.example.activity.request.ApplicationActivityRequest;
import org.example.attendance.*;
import org.example.commucnication.DeviceRegistrationService;
import org.example.device.DeviceIdService;
import org.example.heartbeat.HeartbeatService;
import org.example.ui.LoginWindow;

import javax.swing.*;

import org.example.config.AgentConfig;

public class AgentApplication {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println(" Employee Monitoring Agent");
        System.out.println(" Agent started successfully");
        System.out.println("=================================");


        // =========================
        // Load Configuration
        // =========================

        AgentConfig config =
                new AgentConfig();

        String serverUrl =
                config.getServerUrl();

        System.out.println(
                "Server URL: " + serverUrl
        );


        // =========================
        // Generate Device ID
        // =========================

        DeviceIdService deviceIdService =
                new DeviceIdService();

        String deviceId =
                deviceIdService.generateDeviceId();

        if (deviceId == null) {

            System.out.println(
                    "Unable to generate Device ID."
            );

            return;
        }

        System.out.println(
                "Device ID: " + deviceId
        );


        // =========================
        // Login Window
        // =========================

        SwingUtilities.invokeLater(() -> {

            LoginWindow loginWindow =
                    new LoginWindow(loginResult -> {

                        System.out.println(
                                "Login successful!"
                        );


                        String employeeId =
                                loginResult.getEmployeeId();

                        String employeeCode =
                                loginResult.getEmployeeCode();


                        System.out.println(
                                "Employee ID: "
                                        + employeeId
                        );

                        System.out.println(
                                "Employee Code: "
                                        + employeeCode
                        );


                        // =========================
                        // Device Registration
                        // =========================

                        DeviceRegistrationService
                                registrationService =
                                new DeviceRegistrationService(
                                        serverUrl,
                                        deviceId
                                );

                        boolean registered =
                                registrationService.registerDevice(
                                        employeeId,
                                        employeeCode
                                );


                        if (!registered) {

                            System.out.println(
                                    "Device registration failed."
                            );

                            return;
                        }


                        System.out.println(
                                "Device registration successful."
                        );


                        // =========================
                        // Start Application Tracking
                        // =========================

                        ActiveWindowService activeWindowService =
                                new ActiveWindowService();

                        ApplicationActivitySender
                                applicationActivitySender =
                                new ApplicationActivitySender(
                                        serverUrl
                                );

                        ApplicationActivityTracker
                                applicationActivityTracker =
                                new ApplicationActivityTracker(
                                        activeWindowService,
                                        applicationActivitySender,
                                        employeeCode,
                                        deviceId
                                );


                        // =========================
                        // Application Activity Thread
                        // =========================

                        Thread applicationActivityThread =
                                new Thread(() -> {

                                    while (true) {

                                        try {

                                            ApplicationInfo
                                                    completedActivity =
                                                    applicationActivityTracker
                                                            .checkActivity();


                                            /*
                                             * When the active application
                                             * or window changes,
                                             * checkActivity() returns
                                             * the completed activity.
                                             */

                                            if (completedActivity != null) {

                                                ApplicationActivityRequest
                                                        request =
                                                        new ApplicationActivityRequest(
                                                                employeeCode,
                                                                deviceId,
                                                                completedActivity
                                                        );


                                                boolean activitySent =
                                                        applicationActivitySender
                                                                .sendActivity(
                                                                        request
                                                                );


                                                if (activitySent) {

                                                    System.out.println(
                                                            "Application activity sent successfully."
                                                    );

                                                } else {

                                                    System.out.println(
                                                            "Application activity failed to send."
                                                    );
                                                }
                                            }


                                            /*
                                             * Check active application
                                             * every 1 second.
                                             */

                                            Thread.sleep(1000);


                                        } catch (InterruptedException e) {

                                            Thread.currentThread()
                                                    .interrupt();

                                            System.out.println(
                                                    "Application activity monitor stopped."
                                            );

                                            break;


                                        } catch (Exception e) {

                                            System.out.println(
                                                    "Application activity monitoring error: "
                                                            + e.getMessage()
                                            );
                                        }
                                    }

                                });


                        applicationActivityThread.setName(
                                "Application-Activity-Monitor"
                        );

                        applicationActivityThread.setDaemon(true);

                        applicationActivityThread.start();


                        System.out.println(
                                "Application activity monitoring started."
                        );


                        // =========================
                        // Start Attendance Tracking
                        // =========================

                        AttendanceEventQueue attendanceEventQueue =
                                new AttendanceEventQueue();

                        AttendanceEventFactory eventFactory =
                                new AttendanceEventFactory(
                                        employeeCode,
                                        deviceId
                                );


                        AttendanceEventManager eventManager =
                                new AttendanceEventManager(
                                        eventFactory
                                );


                        AttendanceEventSender eventSender =
                                new AttendanceEventSender(
                                        serverUrl
                                );


                        // =========================
                        // Queue Processor
                        // =========================

                        AttendanceEventQueueProcessor queueProcessor =
                                new AttendanceEventQueueProcessor(
                                        attendanceEventQueue,
                                        eventSender
                                );


                        // =========================
                        // Start Attendance Queue Processor
                        // =========================

                        Thread attendanceQueueThread =
                                new Thread(() -> {

                                    while (true) {

                                        try {

                                            queueProcessor.processQueue();

                                            Thread.sleep(10_000);

                                        } catch (InterruptedException e) {

                                            Thread.currentThread().interrupt();

                                            System.out.println(
                                                    "Attendance queue processor stopped."
                                            );

                                            break;

                                        } catch (Exception e) {

                                            System.out.println(
                                                    "Attendance queue processor error: "
                                                            + e.getMessage()
                                            );
                                        }
                                    }

                                });


                        attendanceQueueThread.setName(
                                "Attendance-Queue-Processor"
                        );

                        attendanceQueueThread.setDaemon(true);

                        attendanceQueueThread.start();


                        System.out.println(
                                "Attendance queue processor started."
                        );


                        // =========================
                        // WORK_STARTED Event
                        // =========================

                        AttendanceEvent workStartedEvent =
                                eventManager.startWork();


                        AttendanceSendResult eventResult =
                                eventSender.sendEvent(
                                        workStartedEvent
                                );


                        if (eventResult == AttendanceSendResult.SUCCESS) {

                            System.out.println(
                                    "WORK_STARTED event sent successfully."
                            );

                        } else if (
                                eventResult == AttendanceSendResult.REJECTED
                        ) {

                            System.out.println(
                                    "WORK_STARTED event was rejected by server. "
                                            + "Event will NOT be queued."
                            );

                        } else {

                            System.out.println(
                                    "WORK_STARTED event failed to send. "
                                            + "Event will be queued for retry."
                            );

                            attendanceEventQueue.add(
                                    workStartedEvent
                            );
                        }


                        // =========================
                        // Start User Activity Monitor
                        // =========================

                        UserActivityMonitor activityMonitor =
                                new UserActivityMonitor();


                        int idleGraceMinutes =
                                config.getIdleGraceMinutes();


                        IdleDetectionService
                                idleDetectionService =
                                new IdleDetectionService(
                                        activityMonitor,
                                        idleGraceMinutes
                                );


                        Thread activityMonitorThread =
                                new Thread(() -> {

                                    while (true) {

                                        try {

                                            Thread.sleep(1000);


                                            long inactiveSeconds =
                                                    activityMonitor
                                                            .getInactiveDurationSeconds();


                                            AttendanceEventType idleEvent =
                                                    idleDetectionService
                                                            .checkIdleStatus();


                                            boolean isIdle =
                                                    idleDetectionService
                                                            .isIdle();


                                            System.out.println(
                                                    "User inactive for "
                                                            + inactiveSeconds
                                                            + " seconds"
                                                            + " | State: "
                                                            + (isIdle
                                                            ? "IDLE"
                                                            : "WORKING")
                                            );


                                            // =========================
                                            // Handle Idle Events
                                            // =========================

                                            if (idleEvent != null) {

                                                AttendanceEvent
                                                        idleAttendanceEvent =
                                                        eventFactory.create(
                                                                idleEvent
                                                        );


                                                AttendanceSendResult idleEventResult =
                                                        eventSender.sendEvent(
                                                                idleAttendanceEvent
                                                        );


                                                if (
                                                        idleEventResult
                                                                == AttendanceSendResult.SUCCESS
                                                ) {

                                                    System.out.println(
                                                            idleEvent
                                                                    + " event sent successfully."
                                                    );

                                                } else if (
                                                        idleEventResult
                                                                == AttendanceSendResult.REJECTED
                                                ) {

                                                    System.out.println(
                                                            idleEvent
                                                                    + " event was rejected by server. "
                                                                    + "Event will NOT be queued."
                                                    );

                                                } else {

                                                    System.out.println(
                                                            idleEvent
                                                                    + " event failed to send. "
                                                                    + "Event will be queued for retry."
                                                    );

                                                    attendanceEventQueue.add(
                                                            idleAttendanceEvent
                                                    );
                                                }
                                            }


                                        } catch (InterruptedException e) {

                                            Thread.currentThread()
                                                    .interrupt();

                                            System.out.println(
                                                    "Activity monitor stopped."
                                            );

                                            break;
                                        }
                                    }

                                });


                        activityMonitorThread.setName(
                                "User-Activity-Monitor"
                        );

                        activityMonitorThread.setDaemon(true);

                        activityMonitorThread.start();


                        // =========================
                        // Start Heartbeat
                        // =========================

                        System.out.println(
                                "Starting heartbeat..."
                        );


                        HeartbeatService heartbeatService =
                                new HeartbeatService(
                                        serverUrl,
                                        deviceId
                                );


                        Thread heartbeatThread =
                                new Thread(() -> {

                                    while (true) {

                                        heartbeatService
                                                .sendHeartbeat();


                                        try {

                                            Thread.sleep(
                                                    10_000
                                            );


                                        } catch (
                                                InterruptedException e) {

                                            Thread.currentThread()
                                                    .interrupt();

                                            System.out.println(
                                                    "Heartbeat stopped."
                                            );

                                            break;
                                        }
                                    }

                                });


                        heartbeatThread.setName(
                                "Heartbeat-Monitor"
                        );

                        heartbeatThread.setDaemon(true);

                        heartbeatThread.start();


                        // =========================
                        // Graceful Shutdown
                        // =========================

                        Runtime.getRuntime().addShutdownHook(
                                new Thread(() -> {

                                    System.out.println(
                                            "Agent shutdown detected."
                                    );

                                    try {

                                        AttendanceEvent workEndedEvent =
                                                eventFactory.create(
                                                        AttendanceEventType.WORK_ENDED
                                                );


                                        AttendanceSendResult workEndedResult =
                                                eventSender.sendEvent(
                                                        workEndedEvent
                                                );


                                        if (
                                                workEndedResult
                                                        == AttendanceSendResult.SUCCESS
                                        ) {

                                            System.out.println(
                                                    "WORK_ENDED event sent successfully."
                                            );

                                        } else if (
                                                workEndedResult
                                                        == AttendanceSendResult.REJECTED
                                        ) {

                                            System.out.println(
                                                    "WORK_ENDED event was rejected by server. "
                                                            + "Event will NOT be queued."
                                            );

                                        } else {

                                            System.out.println(
                                                    "WORK_ENDED event failed to send. "
                                                            + "Event will be queued for retry."
                                            );

                                            attendanceEventQueue.add(
                                                    workEndedEvent
                                            );
                                        }


                                    } catch (Exception e) {

                                        System.out.println(
                                                "Unable to send WORK_ENDED event: "
                                                        + e.getMessage()
                                        );


                                        /*
                                         * If an unexpected exception
                                         * occurs while sending the
                                         * shutdown event, keep the event
                                         * in the queue.
                                         */

                                        try {

                                            AttendanceEvent
                                                    workEndedEvent =
                                                    eventFactory.create(
                                                            AttendanceEventType.WORK_ENDED
                                                    );

                                            attendanceEventQueue.add(
                                                    workEndedEvent
                                            );

                                        } catch (Exception queueException) {

                                            System.out.println(
                                                    "Unable to queue WORK_ENDED event: "
                                                            + queueException.getMessage()
                                            );
                                        }
                                    }

                                })
                        );

                    });


            loginWindow.setVisible(true);

        });
    }
}