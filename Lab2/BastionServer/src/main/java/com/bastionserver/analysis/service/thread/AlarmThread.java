package com.bastionserver.analysis.service.thread;

import com.bastionserver.BastionServerApplication;
import com.bastionserver.analysis.service.AlarmEvent;
import com.bastionserver.notification.NotificationMessage;
import com.bastionserver.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AlarmThread implements Runnable {

    private final BlockingQueue<AlarmEvent> alarmsQueue;
    private final NotificationService notificationService;

    @Value("${time-to-autoalarm}")
    private int timeUntilAutoAlarm;

    private final AtomicBoolean isSetManually = new AtomicBoolean(false);

    public AlarmThread(
            BlockingQueue<AlarmEvent> alarmsQueue,
            NotificationService notificationService
    ) {
        this.alarmsQueue = alarmsQueue;
        this.notificationService = notificationService;
    }

    @Override
    public void run() {
        try {
            while (true) {
                AlarmEvent event = alarmsQueue.take();

                if (!BastionServerApplication.isAlarm()) {
                    isSetManually.set(false);
                    System.out.println("Object considered dangerous! Alarm has been triggered!");

                    for (int i = 0; i < 10; i++) {
                        System.out.println("Waiting for alarm!");
                        Thread.sleep(timeUntilAutoAlarm / 10);
                    }

                    System.out.println("Starting air alert!");
                    BastionServerApplication.startAlarm(event);

                    // Надсилаємо повідомлення
                    notificationService.sendAlarmNotifications(
                            new NotificationMessage("🚨 Увага! Виявлено загрозу: " + event)
                    );
                } else {
                    System.out.println("New alarming object!: " + event);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void setManualAlarm() {
        isSetManually.set(true);
    }

    public boolean isManuallySet() {
        return isSetManually.get();
    }
}