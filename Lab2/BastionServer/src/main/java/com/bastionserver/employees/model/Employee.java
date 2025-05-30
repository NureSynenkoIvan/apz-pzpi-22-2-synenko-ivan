package com.bastionserver.employees.model;

import com.bastionserver.employees.service.HashUtil;

import java.util.Objects;

public class Employee {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
    private String position;
    private Coordinates location;
    private WorkTime workTime;
    private boolean onDuty;
    private String passwordHash;

    public Employee() {

    }

    public Employee(String firstName,
                String lastName,
                String phoneNumber,
                Role role,
                String position,
                Coordinates location,
                WorkTime workTime,
                boolean onDuty,
                    String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.position = position;
        this.location = location;
        this.workTime = workTime;
        this.onDuty = onDuty;
        this.passwordHash = HashUtil.hashPassword(password);
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public WorkTime getWorkTime() {
        return workTime;
    }

    public void setWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public void setOnDuty(boolean onDuty) {
        this.onDuty = onDuty;
    }

    //Returns role in an application - text used to distinct roles and access to sources in HTTP API.
    public String getApplicationRole() {
        return switch (role){
            case USER -> "user";
            case DISPATCHER -> "dispatcher";
            case ADMINISTRATOR -> "administrator";
            default -> throw new IllegalStateException("Unexpected value: " + role);
        };
    }


    @Override
    public String toString() {
        return "Employee " +
                "[\n  firstName=" + firstName + "," +
                "\n  lastName=" + lastName + "," +
                "\n  phoneNumber=" + phoneNumber +
                ",\n  role=" + (role != null ? role.toString() : "null") +
                ",\n  position=" + position +
                ",\n  location=" + (location != null ? location.toString() : "null") +
                ",\n  workTime=" + (workTime != null ? workTime.toString() : "null") +
                "]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Employee employee)) return false;
        return passwordHash == employee.passwordHash
                && Objects.equals(firstName, employee.firstName)
                && Objects.equals(lastName, employee.lastName)
                && Objects.equals(phoneNumber, employee.phoneNumber)
                && role == employee.role
                && Objects.equals(position, employee.position)
                && Objects.equals(location, employee.location)
                && Objects.equals(workTime, employee.workTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phoneNumber,
                role, position, location, workTime, passwordHash);
    }
}
