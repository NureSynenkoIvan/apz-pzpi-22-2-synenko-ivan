package com.bastionserver.notification;

import com.bastionserver.notification.notifiers.Notifier;
import com.bastionserver.notification.notifiers.impl.MobileNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private List<Notifier> notifierList;

    public NotificationService(List<Notifier> notifierList) {
        this.notifierList = notifierList;
        log.info("NotificationService constructor");
    }

    // Sends notifications via all registered channels.
    // Each notifier represents a channel.
    public void sendAlarmNotifications(NotificationMessage message) {
        log.info("Sending alarm notifications to all channels...");
        for (Notifier notifier : notifierList) {
            notifier.sendAlarmNotification(message);
        }
    }

    public void sendStopAlarmNotifications() {
        log.info("Sending stop alarm notifications to all channels...");
        for (Notifier notifier : notifierList) {
            notifier.sendStopAlarmNotification();
        }
    }
}
