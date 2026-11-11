package com.cu.attendance;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Reads and validates console input; every method re-prompts until the value is valid. */
public class InputHelper {
    private final Scanner input;

    public InputHelper(Scanner input) {
        this.input = input;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    public String readRequired(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty() && value.indexOf('|') < 0) return value;
            System.out.println("Value cannot be empty or contain the | character.");
        }
    }

    public String readName(String prompt) {
        while (true) {
            String value = readRequired(prompt);
            if (value.matches("[\\p{L} .'-]+")) return value;
            System.out.println("Name may contain only letters, spaces, . ' and -.");
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            String email = readRequired(prompt);
            if (email.contains("@") && email.indexOf('@') < email.lastIndexOf('.')
                    && !email.endsWith(".")) return email;
            System.out.println("Enter a valid email address.");
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(readLine(prompt));
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a whole number between " + min + " and " + max + ".");
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            try {
                LocalDate date = LocalDate.parse(readLine(prompt));
                if (date.isAfter(LocalDate.now())) {
                    System.out.println("Attendance date cannot be in the future.");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Use the date format YYYY-MM-DD.");
            }
        }
    }

    public AttendanceStatus readStatus() {
        while (true) {
            try {
                return AttendanceStatus.fromChoice(
                        readLine("Status [P=Present, A=Absent, L=Late]: "));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Enter P, A or L.");
            }
        }
    }

    public boolean readYes(String prompt) {
        return "Y".equalsIgnoreCase(readLine(prompt));
    }
}
