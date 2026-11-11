package com.cu.attendance;

public enum AttendanceStatus {
    PRESENT, ABSENT, LATE;

    public static AttendanceStatus fromChoice(String choice) {
        if ("P".equalsIgnoreCase(choice)) return PRESENT;
        if ("A".equalsIgnoreCase(choice)) return ABSENT;
        if ("L".equalsIgnoreCase(choice)) return LATE;
        throw new IllegalArgumentException("Choose P, A or L.");
    }
}
