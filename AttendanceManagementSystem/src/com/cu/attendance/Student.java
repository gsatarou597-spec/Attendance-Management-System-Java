package com.cu.attendance;

/** Student entity. */
public class Student extends User {
    private String email;
    private String programme;
    private int semester;

    public Student(String id, String name, String email, String programme, int semester) {
        super(id, name);
        this.email = email;
        this.programme = programme;
        this.semester = semester;
    }

    public String getEmail() { return email; }
    public String getProgramme() { return programme; }
    public int getSemester() { return semester; }
    public void setEmail(String email) { this.email = email; }
    public void setProgramme(String programme) { this.programme = programme; }
    public void setSemester(int semester) { this.semester = semester; }

    @Override
    public String getRole() { return "STUDENT"; }

    @Override
    public void displayInfo() {
        System.out.printf("%-10s %-24s %-10s Programme: %s, Semester: %d%n",
                getId(), getName(), getRole(), programme, semester);
    }

    public String toFileLine() {
        return FileStorage.sanitize(getId()) + "|" + FileStorage.sanitize(getName()) + "|"
                + FileStorage.sanitize(email) + "|" + FileStorage.sanitize(programme)
                + "|" + semester;
    }

    public static Student fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length != 5) throw new IllegalArgumentException("Invalid student record");
        return new Student(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]));
    }

    @Override
    public String toString() {
        return String.format("%-10s %-22s %-28s %-18s %d",
                getId(), getName(), email, programme, semester);
    }
}
