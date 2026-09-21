package org.example.activity.request;

import org.example.activity.ApplicationInfo;

import java.time.Instant;

public class ApplicationActivityRequest {

    private String employeeCode;
    private String deviceId;

    private String processName;
    private int processId;
    private String windowTitle;

    private Instant startedAt;
    private Instant endedAt;

    private long durationSeconds;

    public ApplicationActivityRequest(
            String employeeCode,
            String deviceId,
            ApplicationInfo activity
    ) {

        this.employeeCode = employeeCode;
        this.deviceId = deviceId;

        this.processName = activity.getProcessName();
        this.processId = activity.getProcessId();
        this.windowTitle = activity.getWindowTitle();

        this.startedAt = activity.getStartedAt();
        this.endedAt = activity.getEndedAt();

        this.durationSeconds =
                activity.getDurationSeconds();
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getProcessName() {
        return processName;
    }

    public int getProcessId() {
        return processId;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public String toString() {

        return "ApplicationActivityRequest{" +
                "employeeCode='" + employeeCode + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", processName='" + processName + '\'' +
                ", processId=" + processId +
                ", windowTitle='" + windowTitle + '\'' +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", durationSeconds=" + durationSeconds +
                '}';
    }
}
