package com.cu.attendance;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console entry point. Run from the project root so that ./data is used.
 */
public class Main {
    private static final double THRESHOLD = 75.0;
    private static final String REPORT_FILE = "class_report.csv";

    private static final Scanner scanner = new Scanner(System.in);
    private static final InputHelper in = new InputHelper(scanner);
    private static AttendanceService manager;

    public static void main(String[] args) {
        try {
            manager = new AttendanceManager(new FileStorage("data"));
            runMenu();
        } catch (IOException e) {
            System.out.println("Unable to initialize data storage: " + e.getMessage());
        } catch (AttendanceException e) {
            System.out.println("Unable to load data: " + e.getMessage());
        } catch (NoSuchElementException e) {
            System.out.println("\nInput closed. Exiting.");
        } finally {
            scanner.close();
        }
    }

    private static void printMenu() {
        System.out.println("\n==============================================");
        System.out.println("       ATTENDANCE MANAGEMENT SYSTEM");
        System.out.println("==============================================");
        System.out.println(" 1. Register student        2. Register teacher");
        System.out.println(" 3. View students           4. View teachers");
        System.out.println(" 5. Mark attendance         6. View attendance history");
        System.out.println(" 7. Student percentage      8. Students below 75%");
        System.out.println(" 9. Class attendance report 10. All users and roles");
        System.out.println("11. Update student         12. Delete student");
        System.out.println("13. Update teacher         14. Delete teacher");
        System.out.println("15. Search student by name 16. Export class report (CSV)");
        System.out.println(" 0. Exit");
        System.out.print("Enter choice: ");
    }

    private static void runMenu() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": registerStudent(); break;
                    case "2": registerTeacher(); break;
                    case "3": viewStudents(); break;
                    case "4": viewTeachers(); break;
                    case "5": markAttendance(); break;
                    case "6": viewHistory(); break;
                    case "7": viewPercentage(); break;
                    case "8": viewShortage(); break;
                    case "9": classReport(); break;
                    case "10": viewAllUsersAndRoles(); break;
                    case "11": updateStudent(); break;
                    case "12": deleteStudent(); break;
                    case "13": updateTeacher(); break;
                    case "14": deleteTeacher(); break;
                    case "15": searchStudents(); break;
                    case "16": exportReport(); break;
                    case "0": running = false; System.out.println("Goodbye!"); break;
                    default: System.out.println("Invalid menu choice.");
                }
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            } catch (AttendanceException e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    // ---------- students ----------

    private static void registerStudent() throws IOException {
        System.out.println("\n--- Student Registration ---");
        String id = in.readRequired("Student ID: ");
        String name = in.readName("Full name: ");
        String email = in.readEmail("Email: ");
        String programme = in.readRequired("Programme: ");
        int semester = in.readInt("Semester (1-12): ", 1, 12);
        manager.addStudent(new Student(id, name, email, programme, semester));
        System.out.println("Student registered successfully.");
    }

    private static void viewStudents() {
        printStudents(manager.getStudents(), "No students registered.");
    }

    private static void printStudents(List<Student> students, String emptyMessage) {
        System.out.println("\nID         NAME                   EMAIL                        PROGRAMME          SEM");
        System.out.println("--------------------------------------------------------------------------------------");
        if (students.isEmpty()) System.out.println(emptyMessage);
        for (Student s : students) System.out.println(s);
    }

    private static void updateStudent() throws IOException {
        System.out.println("\n--- Update Student ---");
        Student student = manager.findStudent(in.readRequired("Student ID to update: "));
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        student.setName(in.readName("New full name: "));
        student.setEmail(in.readEmail("New email: "));
        student.setProgramme(in.readRequired("New programme: "));
        student.setSemester(in.readInt("New semester (1-12): ", 1, 12));
        manager.updateStudent(student);
        System.out.println("Student updated successfully.");
    }

    private static void deleteStudent() throws IOException {
        System.out.println("\n--- Delete Student ---");
        Student student = manager.findStudent(in.readRequired("Student ID to delete: "));
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        if (in.readYes("Delete " + student.getName() + " and their attendance history? [Y/N]: ")) {
            manager.deleteStudent(student.getId());
            System.out.println("Student and associated attendance history deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static void searchStudents() {
        System.out.println("\n--- Search Student by Name ---");
        String text = in.readRequired("Name contains: ");
        printStudents(manager.searchStudentsByName(text), "No matching students.");
    }

    // ---------- teachers ----------

    private static void registerTeacher() throws IOException {
        System.out.println("\n--- Teacher Registration ---");
        String id = in.readRequired("Teacher ID: ");
        String name = in.readName("Full name: ");
        String department = in.readRequired("Department: ");
        manager.addTeacher(new Teacher(id, name, department));
        System.out.println("Teacher registered successfully.");
    }

    private static void viewTeachers() {
        List<Teacher> teachers = manager.getTeachers();
        System.out.println("\nID         NAME                     DEPARTMENT");
        System.out.println("----------------------------------------------------------");
        if (teachers.isEmpty()) System.out.println("No teachers registered.");
        for (Teacher t : teachers) System.out.println(t);
    }

    private static void updateTeacher() throws IOException {
        System.out.println("\n--- Update Teacher ---");
        Teacher teacher = manager.findTeacher(in.readRequired("Teacher ID to update: "));
        if (teacher == null) {
            System.out.println("Teacher not found.");
            return;
        }
        teacher.setName(in.readName("New full name: "));
        teacher.setDepartment(in.readRequired("New department: "));
        manager.updateTeacher(teacher);
        System.out.println("Teacher updated successfully.");
    }

    private static void deleteTeacher() throws IOException {
        System.out.println("\n--- Delete Teacher ---");
        Teacher teacher = manager.findTeacher(in.readRequired("Teacher ID to delete: "));
        if (teacher == null) {
            System.out.println("Teacher not found.");
            return;
        }
        if (in.readYes("Delete " + teacher.getName() + "? [Y/N]: ")) {
            manager.deleteTeacher(teacher.getId());
            System.out.println("Teacher deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // ---------- attendance ----------

    private static void markAttendance() throws IOException {
        System.out.println("\n--- Mark Attendance ---");
        String studentId = in.readRequired("Student ID: ");
        LocalDate date = in.readDate("Date (YYYY-MM-DD): ");
        AttendanceStatus status = in.readStatus();
        String teacherId = in.readRequired("Teacher ID marking attendance: ");
        manager.markAttendance(studentId, date, status, teacherId);
        System.out.println("Attendance saved successfully.");
    }

    private static void viewHistory() {
        String id = in.readRequired("Student ID: ");
        Student student = manager.findStudent(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.println("\nAttendance history for " + student.getName());
        System.out.println("DATE         STATUS       MARKED BY");
        System.out.println("-------------------------------------");
        List<AttendanceRecord> records = manager.getRecordsForStudent(id);
        if (records.isEmpty()) System.out.println("No attendance records.");
        for (AttendanceRecord r : records)
            System.out.printf("%-12s %-12s %s%n", r.getDate(), r.getStatus(), r.getMarkedBy());
    }

    // ---------- reports ----------

    private static void viewPercentage() {
        String id = in.readRequired("Student ID: ");
        Student student = manager.findStudent(id);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        int total = manager.getRecordsForStudent(id).size();
        System.out.printf("%s (%s): %.2f%% attendance across %d marked classes.%n",
                student.getName(), student.getId(), manager.getAttendancePercentage(id), total);
    }

    private static void viewShortage() {
        List<Student> low = manager.getStudentsBelow(THRESHOLD);
        System.out.println("\nStudents below " + THRESHOLD + "% attendance");
        System.out.println("--------------------------------");
        if (low.isEmpty()) System.out.println("No students currently below the threshold.");
        for (Student s : low)
            System.out.printf("%s - %s : %.2f%%%n", s.getId(), s.getName(),
                    manager.getAttendancePercentage(s.getId()));
    }

    private static void classReport() {
        List<Student> students = manager.getStudents();
        System.out.println("\nCLASS ATTENDANCE REPORT");
        System.out.println("ID         STUDENT NAME                  ATTENDANCE");
        System.out.println("----------------------------------------------------");
        if (students.isEmpty()) System.out.println("No students registered.");
        for (Student s : students) {
            if (manager.getRecordsForStudent(s.getId()).isEmpty()) {
                System.out.printf("%-10s %-28s %8s%n", s.getId(), s.getName(), "No records");
            } else {
                System.out.printf("%-10s %-28s %7.2f%%%n", s.getId(), s.getName(),
                        manager.getAttendancePercentage(s.getId()));
            }
        }
    }

    private static void exportReport() throws IOException {
        manager.exportClassReport(REPORT_FILE);
        System.out.println("Class report exported to data/" + REPORT_FILE);
    }

    private static void viewAllUsersAndRoles() {
        // One parent-type list stores both subclasses: runtime polymorphism.
        List<User> users = new ArrayList<User>();
        users.addAll(manager.getStudents());
        users.addAll(manager.getTeachers());

        System.out.println("\nID         NAME                     ROLE       DETAILS");
        System.out.println("---------------------------------------------------------------------");
        if (users.isEmpty()) System.out.println("No users registered.");
        for (User user : users) {
            user.displayInfo(); // each subclass prints itself differently
        }
    }
}
