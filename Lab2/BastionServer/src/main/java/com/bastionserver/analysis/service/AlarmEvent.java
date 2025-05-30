package com.bastionserver.analysis.service;

import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;

import java.util.Date;
import java.util.List;

public class AlarmEvent {
    private final Date alarmTime;
    private final SkyState skyState;
    private final List<SkyObject> threateningObjects;

    public AlarmEvent(SkyState skyState, List<SkyObject> threateningObjects) {
        this.alarmTime = new Date();
        this.skyState = skyState;
        this.threateningObjects = threateningObjects;
    }

    public SkyState getSkyState() {
        return skyState;
    }

    public List<SkyObject> getThreateningObjects() {
        return threateningObjects;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }
}
