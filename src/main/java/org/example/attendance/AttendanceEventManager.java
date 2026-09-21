package org.example.attendance;

public class AttendanceEventManager {

    private final AttendanceEventFactory eventFactory;

    private AttendanceState currentState;

    public AttendanceEventManager(
            AttendanceEventFactory eventFactory
    ) {
        this.eventFactory = eventFactory;
        this.currentState = AttendanceState.CLOCKED_OUT;
    }

    public AttendanceEvent startWork() {

        currentState = AttendanceState.WORKING;

        return eventFactory.create(
                AttendanceEventType.WORK_STARTED
        );
    }

    public AttendanceEvent startIdle() {

        currentState = AttendanceState.IDLE;

        return eventFactory.create(
                AttendanceEventType.IDLE_STARTED
        );
    }

    public AttendanceEvent endIdle() {

        currentState = AttendanceState.WORKING;

        return eventFactory.create(
                AttendanceEventType.IDLE_ENDED
        );
    }

    public AttendanceEvent startBreak() {

        currentState = AttendanceState.BREAK;

        return eventFactory.create(
                AttendanceEventType.BREAK_STARTED
        );
    }

    public AttendanceEvent endBreak() {

        currentState = AttendanceState.WORKING;

        return eventFactory.create(
                AttendanceEventType.BREAK_ENDED
        );
    }

    public AttendanceEvent startLunch() {

        currentState = AttendanceState.LUNCH;

        return eventFactory.create(
                AttendanceEventType.LUNCH_STARTED
        );
    }

    public AttendanceEvent endLunch() {

        currentState = AttendanceState.WORKING;

        return eventFactory.create(
                AttendanceEventType.LUNCH_ENDED
        );
    }

    public AttendanceEvent startMeeting() {

        currentState = AttendanceState.MEETING;

        return eventFactory.create(
                AttendanceEventType.MEETING_STARTED
        );
    }

    public AttendanceEvent endMeeting() {

        currentState = AttendanceState.WORKING;

        return eventFactory.create(
                AttendanceEventType.MEETING_ENDED
        );
    }

    public AttendanceState getCurrentState() {
        return currentState;
    }
}
