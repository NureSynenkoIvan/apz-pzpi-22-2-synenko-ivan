package com.bastionserver.analysis.model;

import java.util.Date;

public class Signal {
    private double frequency;
    private double signalStrength;
    private double azimuth;
    private Date date;
    private int deviceId;

    public Signal(){}

    public Signal(double frequency, double signalStrength, double azimuth, Date date, int deviceId) {
        this.frequency = frequency;
        this.signalStrength = signalStrength;
        this.azimuth = azimuth;
        this.date = date;
        this.deviceId = deviceId;
    }

    public double getFrequency() {
        return frequency;
    }

    public double getSignalStrength() {
        return signalStrength;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public void setSignalStrength(double signalStrength) {
        this.signalStrength = signalStrength;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public double getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(double azimuth) {
        this.azimuth = azimuth;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
