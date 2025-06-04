package com.bastionserver.analysis.service.thread;

import com.bastionserver.BastionServerApplication;
import com.bastionserver.analysis.service.AlarmEvent;
import com.bastionserver.notification.NotificationMessage;
import com.bastionserver.notification.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class AlarmThread implements Runnable {

    private final BlockingQueue<AlarmEvent> alarmsQueue;
    private final NotificationService notificationService;
    private AlarmEvent event;

    @Value("${time-to-autoalarm}")
    private int timeUntilAutoAlarm;

    private final AtomicBoolean isAlarmSet = new AtomicBoolean(true);

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
                event = alarmsQueue.take();

                if (!BastionServerApplication.isAlarm()) {
                    isAlarmSet.set(false);
                    System.out.println("Object considered dangerous! Alarm has been triggered!");

                    for (int i = 0; i < 10; i++) {
                        if (isAlarmSet()) {
                            break;
                        }
                        System.out.println("Waiting for alarm!");
                        Thread.sleep(timeUntilAutoAlarm / 10);
                    }

                    System.out.println("Starting air alert!");
                    BastionServerApplication.startAlarm(event);
                    isAlarmSet.set(true);
                    notificationService.sendAlarmNotifications(
                            new NotificationMessage("Увага! Виявлено загрозу: " + event)
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

    public Optional<AlarmEvent> getEvent() {
        return Optional.ofNullable(event);
    }

    public void setManualAlarm() {
        isAlarmSet.set(true);
        BastionServerApplication.startAlarm(event);
    }

    public void stopAlarm() {
        isAlarmSet.set(false);
        notificationService.sendStopAlarmNotifications();
        BastionServerApplication.stopAlarm();
    }

    public boolean isAlarmSet() {
        return isAlarmSet.get();
    }
}