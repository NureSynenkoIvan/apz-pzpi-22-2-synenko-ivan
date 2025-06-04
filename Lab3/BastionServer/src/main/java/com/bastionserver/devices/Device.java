package com.bastionserver.devices;

import com.bastionserver.employees.model.Coordinates;

public class Device {
    public enum DeviceType {
        RADIO_STATION,
        RADAR
    }

    private int deviceId;
    private String name;
    private DeviceType type;
    private Coordinates location;
    private boolean online;

    public Device() {
    }

    public Device(int deviceId, String name, DeviceType type, Coordinates location) {
        this.deviceId = deviceId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.online = true;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public Coordinates getLocation() {
        return location;
    }

    public void setLocation(Coordinates location) {
        this.location = location;
    }

    public double getLatitude() {
        return this.location.getLatitude();
    }

    public double getLongitude() {
        return this.location.getLongitude();
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "Device [deviceId=" + deviceId + ", name=" + name + ", type=" + type + ", location=" + location + ", online=" + online + "]";
    }
}