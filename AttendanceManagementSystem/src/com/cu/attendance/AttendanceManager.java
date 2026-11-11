package com.cu.attendance;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the attendance service using local text files.
 * All data is loaded into memory once at start-up; every change is written back to disk.
 */
public class AttendanceManager implements AttendanceService {
    private static final String STUDENTS_FILE = "students.txt";
    private static final String TEACHERS_FILE = "teachers.txt";
    private static final String ATTENDANCE_FILE = "attendance.txt";

    private final FileStorage storage;
    private final List<Student> students = new ArrayList<Student>();
    private final List<Teacher> teachers = new ArrayList<Teacher>();
    private final List<AttendanceRecord> records = new ArrayList<AttendanceRecord>();

    public AttendanceManager(FileStorage storage) throws IOException {
        this.storage = storage;
        load();
    }

    // ---------- loading and saving ----------

    private void load() throws IOException {
        for (String line : storage.readLines(STUDENTS_FILE)) {
            if (line.trim().isEmpty()) continue;
            try {
                students.add(Student.fromFileLine(line));
            } catch (RuntimeException e) {
                throw new AttendanceException("Corrupt line in " + STUDENTS_FILE + ": " + line);
            }
        }
        for (String line : storage.readLines(TEACHERS_FILE)) {
            if (line.trim().isEmpty()) continue;
            try {
                teachers.add(Teacher.fromFileLine(line));
            } catch (RuntimeException e) {
                throw new AttendanceException("Corrupt line in " + TEACHERS_FILE + ": " + line);
            }
        }
        for (String line : storage.readLines(ATTENDANCE_FILE)) {
            if (line.trim().isEmpty()) continue;
            try {
                records.add(AttendanceRecord.fromFileLine(line));
            } catch (RuntimeException e) {
                throw new AttendanceException("Corrupt line in " + ATTENDANCE_FILE + ": " + line);
            }
        }
    }

    private void saveStudents() throws IOException {
        List<String> lines = new ArrayList<String>();
        for (Student s : students) lines.add(s.toFileLine());
        storage.overwrite(STUDENTS_FILE, lines);
    }

    private void saveTeachers() throws IOException {
        List<String> lines = new ArrayList<String>();
        for (Teacher t : teachers) lines.add(t.toFileLine());
        storage.overwrite(TEACHERS_FILE, lines);
    }

    private void saveAttendance() throws IOException {
        List<String> lines = new ArrayList<String>();
        for (AttendanceRecord r : records) lines.add(r.toFileLine());
        storage.overwrite(ATTENDANCE_FILE, lines);
    }

    // ---------- students ----------

    @Override
    public void addStudent(Student student) throws IOException {
        if (findStudent(student.getId()) != null)
            throw new AttendanceException("A student with this ID already exists.");
        students.add(student);
        storage.appendLine(STUDENTS_FILE, student.toFileLine());
    }

    @Override
    public void updateStudent(Student updated) throws IOException {
        Student existing = findStudent(updated.getId());
        if (existing == null) throw new AttendanceException("Student ID was not found.");
        students.set(students.indexOf(existing), updated);
        saveStudents();
    }

    @Override
    public void deleteStudent(String studentId) throws IOException {
        Student existing = findStudent(studentId);
        if (existing == null) throw new AttendanceException("Student ID was not found.");
        students.remove(existing);
        // Remove the student's attendance history as part of the deletion.
        for (int i = records.size() - 1; i >= 0; i--) {
            if (records.get(i).getStudentId().equalsIgnoreCase(studentId)) records.remove(i);
        }
        saveStudents();
        saveAttendance();
    }

    // ---------- teachers ----------

    @Override
    public void addTeacher(Teacher teacher) throws IOException {
        if (findTeacher(teacher.getId()) != null)
            throw new AttendanceException("A teacher with this ID already exists.");
        teachers.add(teacher);
        storage.appendLine(TEACHERS_FILE, teacher.toFileLine());
    }

    @Override
    public void updateTeacher(Teacher updated) throws IOException {
        Teacher existing = findTeacher(updated.getId());
        if (existing == null) throw new AttendanceException("Teacher ID was not found.");
        teachers.set(teachers.indexOf(existing), updated);
        saveTeachers();
    }

    @Override
    public void deleteTeacher(String teacherId) throws IOException {
        Teacher existing = findTeacher(teacherId);
        if (existing == null) throw new AttendanceException("Teacher ID was not found.");
        for (AttendanceRecord r : records) {
            if (r.getMarkedBy().equalsIgnoreCase(teacherId))
                throw new AttendanceException("This teacher has marked attendance and cannot be deleted.");
        }
        teachers.remove(existing);
        saveTeachers();
    }

    // ---------- attendance ----------

    @Override
    public void markAttendance(String studentId, LocalDate date,
                               AttendanceStatus status, String teacherId) throws IOException {
        if (findStudent(studentId) == null)
            throw new AttendanceException("Student ID was not found.");
        if (findTeacher(teacherId) == null)
            throw new AttendanceException("Teacher ID was not found.");

        for (AttendanceRecord r : records) {
            if (r.getStudentId().equalsIgnoreCase(studentId) && r.getDate().equals(date))
                throw new AttendanceException(
                        "Attendance already exists for this student on " + date + ".");
        }
        AttendanceRecord record = new AttendanceRecord(studentId, date, status, teacherId);
        records.add(record);
        storage.appendLine(ATTENDANCE_FILE, record.toFileLine());
    }

    // ---------- queries ----------

    @Override
    public List<Student> getStudents() { return new ArrayList<Student>(students); }

    @Override
    public List<Teacher> getTeachers() { return new ArrayList<Teacher>(teachers); }

    @Override
    public Student findStudent(String id) {
        for (Student s : students)
            if (s.getId().equalsIgnoreCase(id)) return s;
        return null;
    }

    @Override
    public Teacher findTeacher(String id) {
        for (Teacher t : teachers)
            if (t.getId().equalsIgnoreCase(id)) return t;
        return null;
    }

    @Override
    public List<Student> searchStudentsByName(String text) {
        List<Student> result = new ArrayList<Student>();
        String needle = text.toLowerCase();
        for (Student s : students)
            if (s.getName().toLowerCase().contains(needle)) result.add(s);
        return result;
    }

    @Override
    public List<AttendanceRecord> getRecordsForStudent(String studentId) {
        List<AttendanceRecord> result = new ArrayList<AttendanceRecord>();
        for (AttendanceRecord r : records)
            if (r.getStudentId().equalsIgnoreCase(studentId)) result.add(r);
        return result;
    }

    @Override
    public double getAttendancePercentage(String studentId) {
        List<AttendanceRecord> own = getRecordsForStudent(studentId);
        if (own.isEmpty()) return 0.0;
        int attended = 0;
        for (AttendanceRecord r : own)
            if (r.getStatus() != AttendanceStatus.ABSENT) attended++; // PRESENT or LATE
        return (attended * 100.0) / own.size();
    }

    @Override
    public List<Student> getStudentsBelow(double threshold) {
        List<Student> result = new ArrayList<Student>();
        for (Student s : students) {
            if (!getRecordsForStudent(s.getId()).isEmpty()
                    && getAttendancePercentage(s.getId()) < threshold)
                result.add(s);
        }
        return result;
    }

    // ---------- export ----------

    @Override
    public void exportClassReport(String fileName) throws IOException {
        List<String> lines = new ArrayList<String>();
        lines.add("StudentID,Name,Programme,Semester,TotalClasses,AttendedClasses,Percentage");
        for (Student s : students) {
            List<AttendanceRecord> own = getRecordsForStudent(s.getId());
            int attended = 0;
            for (AttendanceRecord r : own)
                if (r.getStatus() != AttendanceStatus.ABSENT) attended++;
            lines.add(csv(s.getId()) + "," + csv(s.getName()) + "," + csv(s.getProgramme())
                    + "," + s.getSemester() + "," + own.size() + "," + attended + ","
                    + String.format("%.2f", getAttendancePercentage(s.getId())));
        }
        storage.overwrite(fileName, lines);
    }

    private static String csv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
