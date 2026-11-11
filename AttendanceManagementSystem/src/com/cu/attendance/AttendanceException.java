package com.cu.attendance;

/** Represents a business-rule violation in the attendance application. */
public class AttendanceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AttendanceException(String message) {
        super(message);
    }
}
