package org.example.activity;

import java.time.Instant;

public class ApplicationInfo {

    private final String processName;
    private final int processId;
    private final String windowTitle;

    private final Instant startedAt;
    private final Instant endedAt;

    private final long durationSeconds;

    public ApplicationInfo(
            String processName,
            int processId,
            String windowTitle,
            Instant startedAt,
            Instant endedAt,
            long durationSeconds
    ) {
        this.processName = processName;
        this.processId = processId;
        this.windowTitle = windowTitle;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationSeconds = durationSeconds;
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

        return "ApplicationInfo{" +
                "processName='" + processName + '\'' +
                ", processId=" + processId +
                ", windowTitle='" + windowTitle + '\'' +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", durationSeconds=" + durationSeconds +
                '}';
    }
}