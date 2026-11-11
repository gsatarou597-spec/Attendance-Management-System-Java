# Attendance Management System (Java)

## Requirements
- JDK 8 or later
- Terminal / Command Prompt

## Compile
Run these commands from the project root:

```bash
javac -d out src/com/cu/attendance/*.java
```

## Run
```bash
java -cp out com.cu.attendance.Main
```

Run from the project root because the application stores data in `./data`.

## Data files
- `data/students.txt`: student profile records
- `data/teachers.txt`: teacher profile records
- `data/attendance.txt`: daily attendance records

Each line is a pipe-delimited record. Do not manually insert the `|` character into fields.

## Menu options
| # | Option | # | Option |
|---|--------|---|--------|
| 1 | Register student | 9 | Class attendance report |
| 2 | Register teacher | 10 | All users and roles (polymorphism) |
| 3 | View students | 11 | Update student |
| 4 | View teachers | 12 | Delete student |
| 5 | Mark attendance | 13 | Update teacher |
| 6 | Attendance history | 14 | Delete teacher |
| 7 | Student percentage | 15 | Search student by name |
| 8 | Students below 75% | 16 | Export class report to CSV |

## Project structure
```
AttendanceManagementSystem/
  src/com/cu/attendance/   Java source (Main, InputHelper, AttendanceManager, ...)
  data/                    students.txt, teachers.txt, attendance.txt (sample data)
  Documentation/           Case study (Word) and Screenshots/ (console output images)
  TEST_EXECUTION_LOG.txt   Test results
  SAMPLE_OUTPUT.txt        Real console output from the sample data
```

## Suggested first run
1. Register at least one teacher.
2. Register two or more students.
3. Mark attendance for each student using an ISO date such as `2026-11-11`.
4. View history, percentage, shortage list and class report.
5. Try menu option 10 to view Student/Teacher roles (runtime polymorphism).
6. Try menu options 11 and 12 to update or delete a student. Deleting a student also removes that student's attendance history. A teacher who has marked attendance cannot be deleted (option 14).
7. Exit and run again to confirm persistence.

## Attendance policy used in this project
- PRESENT and LATE count as attended.
- ABSENT counts as not attended.
- Percentage = (PRESENT + LATE) / total marked classes * 100.
- Students with at least one record and below 75% are shown in the shortage report.

## Design notes
The application is intentionally console-based and uses plain UTF-8 text files rather than a database. It demonstrates classes, objects, encapsulation, inheritance, abstraction, interfaces, runtime polymorphism through a `List<User>` containing both `Student` and `Teacher` objects, domain-specific validation exceptions, packages and file handling. Student and teacher records can be updated or deleted, students can be searched by name, and the class report can be exported to `data/class_report.csv`.

All data is loaded into memory once at start-up and written back to the text files after every change. Console input is validated in a separate `InputHelper` class (names, email format, semester range, no future dates, valid status).


## Sample data
The `data` folder contains small demonstration records so the reports are populated on first launch. Back up or clear these three text files if you want to start with an empty database.
