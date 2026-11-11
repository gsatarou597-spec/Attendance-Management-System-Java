package com.cu.attendance;

/** Teacher entity. */
public class Teacher extends User {
    private String department;

    public Teacher(String id, String name, String department) {
        super(id, name);
        this.department = department;
    }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String getRole() { return "TEACHER"; }

    @Override
    public void displayInfo() {
        System.out.printf("%-10s %-24s %-10s Department: %s%n",
                getId(), getName(), getRole(), department);
    }

    public String toFileLine() {
        return FileStorage.sanitize(getId()) + "|" + FileStorage.sanitize(getName())
                + "|" + FileStorage.sanitize(department);
    }

    public static Teacher fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 3) throw new IllegalArgumentException("Invalid teacher record");
        return new Teacher(p[0], p[1], p[2]);
    }

    @Override
    public String toString() {
        return String.format("%-10s %-24s %s", getId(), getName(), department);
    }
}
