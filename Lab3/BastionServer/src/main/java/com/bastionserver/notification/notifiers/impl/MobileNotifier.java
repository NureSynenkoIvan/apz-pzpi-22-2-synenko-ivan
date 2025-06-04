package com.bastionserver.notification.notifiers.impl;


import com.bastionserver.notification.NotificationMessage;
import com.bastionserver.notification.notifiers.Notifier;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MobileNotifier implements Notifier {
    private static final Logger log = LoggerFactory.getLogger(MobileNotifier.class);

    public MobileNotifier() {
        log.info("Mobile notifier created");
    }

    @Override
    public void sendAlarmNotification(NotificationMessage event) {
        log.info("Alarm notifications sent! \nMobile notifications will be implemented with the mobile release");
    }

    @Override
    public void sendStopAlarmNotification() {
        log.info("All clear alarm notifications sent! \nMobile notifications will be implemented with the mobile release");
    }
}
