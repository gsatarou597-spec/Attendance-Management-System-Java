package com.cu.attendance;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/** Contract for attendance operations exposed to the application menu. */
public interface AttendanceService {
    void addStudent(Student student) throws IOException;
    void updateStudent(Student student) throws IOException;
    void deleteStudent(String studentId) throws IOException;

    void addTeacher(Teacher teacher) throws IOException;
    void updateTeacher(Teacher teacher) throws IOException;
    void deleteTeacher(String teacherId) throws IOException;

    void markAttendance(String studentId, LocalDate date,
                        AttendanceStatus status, String teacherId) throws IOException;

    List<Student> getStudents();
    List<Teacher> getTeachers();
    Student findStudent(String id);
    Teacher findTeacher(String id);
    List<Student> searchStudentsByName(String text);
    List<AttendanceRecord> getRecordsForStudent(String studentId);
    double getAttendancePercentage(String studentId);
    List<Student> getStudentsBelow(double threshold);

    /** Writes the class attendance report to a CSV file inside the data folder. */
    void exportClassReport(String fileName) throws IOException;
}
