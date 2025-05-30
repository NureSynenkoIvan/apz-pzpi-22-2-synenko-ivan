package com.bastionserver.employees.model;

import java.util.Objects;

public class Coordinates {
    private final double latitude;
    private final double longitude;

    public Coordinates(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceTo(Coordinates other) {
        return Math.sqrt(Math.pow(latitude - other.latitude, 2) + Math.pow(longitude - other.longitude, 2));
    }

    public static Coordinates rotatePoint(Coordinates centerPoint, Coordinates pointToRotate, double beta) {
        double angleInRadians = Math.toRadians(beta);

        double translatedX = pointToRotate.getLatitude() - centerPoint.getLatitude();
        double translatedY = pointToRotate.getLongitude() - centerPoint.getLongitude();

        double rotatedX = translatedX * Math.cos(angleInRadians) - translatedY * Math.sin(angleInRadians);
        double rotatedY = translatedX * Math.sin(angleInRadians) + translatedY * Math.cos(angleInRadians);

        double finalX = rotatedX + centerPoint.getLatitude();
        double finalY = rotatedY + centerPoint.getLongitude();

        return new Coordinates(finalX, finalY);
    }

    @Override
    public String toString() {
        return "Coordinates [" +
                "\n  latitude=" + latitude + "," +
                "\n  longitude=" + longitude + "\n]";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Coordinates that)) return false;
        return Double.compare(latitude, that.latitude) == 0 && Double.compare(longitude, that.longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

}