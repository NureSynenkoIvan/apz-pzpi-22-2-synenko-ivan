package com.bastionserver.analysis.model;

import com.bastionserver.devices.Device;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SkyState {
    private final Date timestamp;
    private final Map<Device, List<SkyObject>> skyObjects;
    private final List<Signal> rawSignals;

    public SkyState(Map<Device, List<SkyObject>> skyObjects, List<Signal> uncheckedSignals) {
        this.timestamp = new Date();
        this.skyObjects = skyObjects;
        this.rawSignals = uncheckedSignals;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<Device, List<SkyObject>> getSkyObjects() {
        return skyObjects;
    }

    public void addToSkyObjects(Map<Integer, Device> devices, List<SkyObject> skyObjects) {
        skyObjects.forEach(skyObject -> {
            Device device = devices.get(skyObject.getDeviceId());
            this.skyObjects.get(device).add(skyObject);
        });
    }

    public List<Signal> getRawSignals() {
        return rawSignals;
    }
}
