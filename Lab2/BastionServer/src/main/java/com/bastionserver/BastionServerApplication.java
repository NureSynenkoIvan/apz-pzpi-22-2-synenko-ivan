package com.bastionserver;

import com.bastionserver.analysis.service.AlarmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BastionServerApplication {
    private static final Logger log = LoggerFactory.getLogger(BastionServerApplication.class);
    public volatile static boolean isAlarm = false;

    public static boolean isAlarm() {
        return isAlarm;
    }

    public static void startAlarm(AlarmEvent alarmEvent) {
        BastionServerApplication.isAlarm = true;
        log.info("ALARM STARTED " + alarmEvent.toString());
    }

    public static void main(String[] args) {
        SpringApplication.run(BastionServerApplication.class, args);
    }

}
