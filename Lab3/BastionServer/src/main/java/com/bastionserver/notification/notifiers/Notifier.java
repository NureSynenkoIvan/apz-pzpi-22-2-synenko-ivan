package com.bastionserver.notification.notifiers;


import com.bastionserver.notification.NotificationMessage;

public interface Notifier {
    public void sendAlarmNotification(NotificationMessage event);

    public void sendStopAlarmNotification();
}
