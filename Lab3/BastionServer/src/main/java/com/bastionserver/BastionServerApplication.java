package com.bastionserver;

import com.bastionserver.analysis.service.AlarmEvent;
import com.bastionserver.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BastionServerApplication {
    private static final Logger log = LoggerFactory.getLogger(BastionServerApplication.class);
    private NotificationService notificationService;
    public volatile static boolean isAlarm = false;

    @Autowired
    public BastionServerApplication(NotificationService notificationService) {
        this.notificationService = notificationService;
    }



    public static boolean isAlarm() {
        return isAlarm;
    }

    public synchronized static void startAlarm(AlarmEvent alarmEvent) {
        BastionServerApplication.isAlarm = true;
        log.info("ALARM STARTED " + alarmEvent.toString());
    }

    public synchronized static void stopAlarm() {
        BastionServerApplication.isAlarm = false;
        log.info("ALARM STOPPED ");
    }

    public static void main(String[] args) {
        SpringApplication.run(BastionServerApplication.class, args);
    }

}
