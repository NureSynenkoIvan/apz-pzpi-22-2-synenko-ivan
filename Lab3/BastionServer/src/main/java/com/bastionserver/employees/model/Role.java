package com.bastionserver.employees.model;

public enum Role {
    ADMINISTRATOR,
    DISPATCHER,
    USER,
    //We also need roles for "system users": Frontend, and IoT
    DETECTOR,
    FRONTEND;

    @Override
    public String toString() {
        return switch (this) {
            case ADMINISTRATOR -> "Administrator";
            case DISPATCHER -> "Dispatcher";
            case USER -> "User";
            //
            case DETECTOR -> "Detector";
            case FRONTEND -> "Frontend";
            default -> super.toString();
        };
    }
}
