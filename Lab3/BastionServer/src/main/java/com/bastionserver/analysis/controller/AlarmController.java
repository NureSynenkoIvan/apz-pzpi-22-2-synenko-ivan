package com.bastionserver.analysis.controller;

import com.bastionserver.BastionServerApplication;
import com.bastionserver.analysis.service.AlarmEvent;
import com.bastionserver.analysis.service.thread.AlarmThread;
import com.bastionserver.notification.NotificationService;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/alarm")
@CrossOrigin
public class AlarmController {
    private AlarmThread alarmThread;

    @Autowired
    public AlarmController(AlarmThread alarmThread) {
        this.alarmThread = alarmThread;
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator", "ROLE_user"})
    @CrossOrigin
    public ResponseEntity<AlarmControllerResponse> getAlarm() {
        AlarmControllerResponse alarmControllerResponse = alarmThread.getEvent().isEmpty() ?
                (new AlarmControllerResponse(BastionServerApplication.isAlarm()))
                : (new AlarmControllerResponse(
                        BastionServerApplication.isAlarm(),
                        alarmThread.isAlarmSet(),
                        alarmThread.getEvent().get()));

        return ResponseEntity.ok(alarmControllerResponse);
    }

    @PostMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public ResponseEntity<AlarmControllerResponse> postAlarm(@RequestParam boolean isAlarm) {
        if (isAlarm) {
            alarmThread.setManualAlarm();
        } else {
            alarmThread.stopAlarm();
        }
        return ResponseEntity.ok(new AlarmControllerResponse(BastionServerApplication.isAlarm()));
    }

    public static class AlarmControllerResponse {
        public boolean isAlarm;
        public boolean isAlarmSet;
        public AlarmEvent currentEvent; // Замість Optional

        public AlarmControllerResponse(boolean isAlarm) {
            this.isAlarm = isAlarm;
            this.isAlarmSet = true;
            this.currentEvent = null;
        }

        public AlarmControllerResponse(boolean isAlarm, boolean isAlarmSet, AlarmEvent event) {
            this.isAlarm = isAlarm;
            this.isAlarmSet = isAlarmSet;
            this.currentEvent = event;
        }
    }
}
