package com.cu.attendance;

/**
 * Common parent for people who use the attendance application.
 * Demonstrates abstraction and inheritance.
 */
public abstract class User {
    private final String id;
    private String name;

    protected User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public abstract String getRole();

    /** Prints a concise role-specific summary for polymorphic user listings. */
    public abstract void displayInfo();
}
