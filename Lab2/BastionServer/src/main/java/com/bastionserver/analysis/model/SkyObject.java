package com.bastionserver.analysis.model;

import com.bastionserver.employees.model.Coordinates;

public class SkyObject {
    private final Coordinates coordinates;
    private double altitude;
    private DroneIdData droneIDData;
    private int deviceID;

    public SkyObject(double latitude, double longitude, double altitude, int deviceId) {
        this.coordinates = new Coordinates(latitude, longitude);
        this.altitude = altitude;
        this.deviceID = deviceId;
    }

    public SkyObject(Coordinates coordinates) {
        this.coordinates = coordinates;
    }


    public SkyObject(DroneIdData droneIDData) {

        this.deviceID = droneIDData.getReceiverDeviceId();
        this.coordinates = new Coordinates(droneIDData.getLatitude(), droneIDData.getLongitude());
        this.altitude = droneIDData.getAltitude();
        this.droneIDData = droneIDData;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public double getLatitude() {
        return coordinates.getLatitude();
    }

    public double getLongitude() {
        return coordinates.getLongitude();
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public DroneIdData getDroneData() {
        return droneIDData;
    }

    public void setDroneData(DroneIdData droneIDData) {
        this.droneIDData = droneIDData;
    }

    public int getDeviceId() {
        return deviceID;
    }

    //Sky objects that have no data from DroneID are discerned via triangulation.
    public boolean isTriangulated() {
        return this.droneIDData == null;
    }
}
