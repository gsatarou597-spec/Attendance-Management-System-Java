package com.cu.attendance;

import java.time.LocalDate;

/** One attendance entry for one student on one date. */
public class AttendanceRecord {
    private final String studentId;
    private final LocalDate date;
    private final AttendanceStatus status;
    private final String markedBy;

    public AttendanceRecord(String studentId, LocalDate date,
                            AttendanceStatus status, String markedBy) {
        this.studentId = studentId;
        this.date = date;
        this.status = status;
        this.markedBy = markedBy;
    }

    public String getStudentId() { return studentId; }
    public LocalDate getDate() { return date; }
    public AttendanceStatus getStatus() { return status; }
    public String getMarkedBy() { return markedBy; }

    public String toFileLine() {
        return FileStorage.sanitize(studentId) + "|" + date + "|" + status.name() + "|"
                + FileStorage.sanitize(markedBy);
    }

    public static AttendanceRecord fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 4) throw new IllegalArgumentException("Invalid attendance record");
        return new AttendanceRecord(p[0], LocalDate.parse(p[1]),
                AttendanceStatus.valueOf(p[2]), p[3]);
    }
}
