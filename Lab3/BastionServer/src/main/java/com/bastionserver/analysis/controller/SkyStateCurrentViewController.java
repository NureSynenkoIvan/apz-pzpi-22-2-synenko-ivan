package com.bastionserver.analysis.controller;

import com.bastionserver.BastionServerApplication;
import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.analysis.service.thread.AlarmThread;
import com.bastionserver.analysis.service.thread.RadarViewerThread;
import com.bastionserver.devices.Device;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("radar-view")
public class SkyStateCurrentViewController {
    private RadarViewerThread radarViewerThread;
    private AlarmThread alarmThread;

    @Autowired
    public SkyStateCurrentViewController(
            RadarViewerThread radarViewerThread,
            AlarmThread alarmThread) {
        this.radarViewerThread = radarViewerThread;
        this.alarmThread = alarmThread;
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public ResponseEntity<SkyState> getSkyState() {
        SkyState skyState = radarViewerThread.getCurrentSkyState();
        if (BastionServerApplication.isAlarm()) {
            skyState.setAlarm(true);
        }
        if (skyState == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(skyState);
    }

    public static class SkyStateCurrentViewControllerResponse {
        private final Date timestamp;
        private final Map<Device, List<SkyObject>> skyObjects;
        private final List<Signal> rawSignals;
        private boolean isAlarm;
        private boolean isAlarmSet;

        public SkyStateCurrentViewControllerResponse(SkyState skyState, boolean IsAlarmSet) {
            this.timestamp = skyState.getTimestamp();
            this.skyObjects = skyState.getSkyObjects();
            this.rawSignals = skyState.getRawSignals();
            this.isAlarm = skyState.isAlarm();
            this.isAlarmSet = IsAlarmSet;
        }

    }


}
