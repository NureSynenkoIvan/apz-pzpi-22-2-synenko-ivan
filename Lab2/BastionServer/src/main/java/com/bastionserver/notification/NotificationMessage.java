package com.bastionserver.notification;

import java.util.Date;

public class NotificationMessage {
    public Date date;
    public String message;

    public NotificationMessage(String message) {
        this.date = new Date();
        this.message = message;
    }

    @Override
    public String toString() {
        return message + "\nДата: " + date.toString();
    }
}
