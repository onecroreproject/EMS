package org.example.activity;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class ActiveWindowService {

    private interface User32 extends Library {

        User32 INSTANCE =
                Native.load("user32", User32.class);

        Pointer GetForegroundWindow();

        int GetWindowTextW(
                Pointer hWnd,
                char[] lpString,
                int nMaxCount
        );

        int GetWindowThreadProcessId(
                Pointer hWnd,
                int[] processId
        );
    }

    // ---------------------------------------------------------
    // Active window snapshot
    // ---------------------------------------------------------

    public ActiveWindowSnapshot getActiveWindowSnapshot() {

        /*
         * Get the foreground window ONCE.
         *
         * All information below will be obtained
         * from this same window handle.
         */
        Pointer windowHandle =
                User32.INSTANCE.GetForegroundWindow();

        if (windowHandle == null) {

            return new ActiveWindowSnapshot(
                    "",
                    0,
                    ""
            );
        }

        // -----------------------------------------------------
        // Get window title
        // -----------------------------------------------------

        char[] windowTitle =
                new char[512];

        int titleLength =
                User32.INSTANCE.GetWindowTextW(
                        windowHandle,
                        windowTitle,
                        windowTitle.length
                );

        String title = "";

        if (titleLength > 0) {

            title =
                    new String(
                            windowTitle,
                            0,
                            titleLength
                    );
        }

        // -----------------------------------------------------
        // Get process ID
        // -----------------------------------------------------

        int[] processId =
                new int[1];

        User32.INSTANCE.GetWindowThreadProcessId(
                windowHandle,
                processId
        );

        int pid =
                processId[0];

        // -----------------------------------------------------
        // Get process name
        // -----------------------------------------------------

        String processName =
                getProcessName(pid);

        return new ActiveWindowSnapshot(
                processName,
                pid,
                title
        );
    }

    // ---------------------------------------------------------
    // Get process name
    // ---------------------------------------------------------

    private String getProcessName(
            int processId
    ) {

        if (processId == 0) {
            return "";
        }

        try {

            Process process =
                    new ProcessBuilder(
                            "tasklist",
                            "/FI",
                            "PID eq " + processId,
                            "/FO",
                            "CSV",
                            "/NH"
                    ).start();

            java.io.BufferedReader reader =
                    new java.io.BufferedReader(
                            new java.io.InputStreamReader(
                                    process.getInputStream()
                            )
                    );

            String line =
                    reader.readLine();

            if (line == null
                    || line.isBlank()) {

                return "";
            }

            int firstQuote =
                    line.indexOf('"');

            int secondQuote =
                    line.indexOf(
                            '"',
                            firstQuote + 1
                    );

            if (firstQuote >= 0
                    && secondQuote > firstQuote) {

                return line.substring(
                        firstQuote + 1,
                        secondQuote
                );
            }

        } catch (Exception e) {

            System.out.println(
                    "Unable to detect process name: "
                            + e.getMessage()
            );
        }

        return "";
    }

    // ---------------------------------------------------------
    // Snapshot class
    // ---------------------------------------------------------

    public static class ActiveWindowSnapshot {

        private final String processName;
        private final int processId;
        private final String windowTitle;

        public ActiveWindowSnapshot(
                String processName,
                int processId,
                String windowTitle
        ) {
            this.processName = processName;
            this.processId = processId;
            this.windowTitle = windowTitle;
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

        @Override
        public String toString() {

            return "ActiveWindowSnapshot{" +
                    "processName='" + processName + '\'' +
                    ", processId=" + processId +
                    ", windowTitle='" + windowTitle + '\'' +
                    '}';
        }
    }

    // ---------------------------------------------------------
    // Test
    // ---------------------------------------------------------

    public static void main(String[] args) {

        ActiveWindowService service =
                new ActiveWindowService();

        while (true) {

            ActiveWindowSnapshot snapshot =
                    service.getActiveWindowSnapshot();

            System.out.println(
                    "Process Name: "
                            + snapshot.getProcessName()
            );

            System.out.println(
                    "Process ID: "
                            + snapshot.getProcessId()
            );

            System.out.println(
                    "Window Title: "
                            + snapshot.getWindowTitle()
            );

            System.out.println(
                    "------------------------------------------------"
            );

            try {

                Thread.sleep(2000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                break;
            }
        }
    }
}