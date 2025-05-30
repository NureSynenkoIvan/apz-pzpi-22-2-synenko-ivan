package com.bastionserver.analysis.service.strategy;

import com.bastionserver.devices.Device;
import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.devices.DeviceRepository;
import com.bastionserver.employees.model.Coordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

public abstract class SignalAnalysisStrategy implements ThreatAnalysisStrategy {
    private static final Logger log = LoggerFactory.getLogger(SignalAnalysisStrategy.class);
    protected static final int MAP_DEGREE = 111320;

    @Value("${k-coefficient}")
    protected static double kCoefficient;

    @Value("${defence-point.x}")
    private static double defencePointX;
    @Value("${defence-point.y}")
    private static double defencePointY;

    protected static Coordinates pointToDefend = new Coordinates(defencePointX, defencePointY);

    private DeviceRepository deviceRepository;
    protected Map<Integer, Device> devices;

    @Autowired
    protected SignalAnalysisStrategy(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
        this.devices = new HashMap<>();
        deviceRepository
                .findAll()
                .stream()
                .forEach(device -> {
                    devices.put(device.getDeviceId(), device);
                });
    }

    protected SignalAnalysisStrategy() {
    }

    protected abstract List<List<Signal>> groupSignalsOfSameFrequency(List<Signal> signalsOfSameFrequency);

    public List<SkyObject> triangulateSkyObjects(List<Signal> rawSignals) {
        // We group signals by frequency, assuming that equal frequency means that signals originate from same source.
        Map<Double, List<Signal>> frequencyGroupedSignals = rawSignals.stream()
                .collect(Collectors.groupingBy(Signal::getFrequency));

        //We group signals of same frequency, forming groups that most likely belong to one source
        List<List<Signal>> signalGroups = new LinkedList<>();

        frequencyGroupedSignals.values().forEach(signals -> {
            signalGroups.addAll(groupSignalsOfSameFrequency(signals));
        });

        List<SkyObject> skyObjects = new ArrayList<>();

        for (List<Signal> signals : signalGroups) {

            if (signals.size() < 2) {
                log.warn("Signal " + signals.get(0).toString()
                        + " is observed only from " + signals.size() + " points, needs 2 to triangulate");
                //We should have at least two points to triangulate
            } else {
                try {
                    skyObjects.add(triangulateObject(signals));
                } catch (TriangulationFailedException e) {
                    log.warn("Triangulation failed", e);
                }
            }
        }
        return skyObjects;
    }

    private SkyObject triangulateObject(List<Signal> signals) throws TriangulationFailedException {
        Device stationA = devices.get(signals.get(0).getDeviceId());
        Device stationB = devices.get(signals.get(1).getDeviceId());

        double azimuthA = signals.get(0).getAzimuth();
        double azimuthB = signals.get(1).getAzimuth();

        return triangulate(stationA, stationB, azimuthA, azimuthB);
    }

    private static SkyObject triangulate(Device stationA, Device stationB, double azimuthA, double azimuthB) {
        double distanceBetweenStations = stationA.getLocation().distanceTo(stationB.getLocation());

        double dx = stationB.getLatitude() - stationA.getLatitude();
        double dy = stationB.getLongitude() - stationA.getLongitude();

        double angleBetweenLineAndAxis = Math.toDegrees(Math.atan2(dx, dy));

        double gamma = azimuthA - azimuthB;
        double alpha = angleBetweenLineAndAxis - azimuthA;
        double beta = 180 - gamma - alpha;

        double distance = distanceBetweenStations * (Math.sin(Math.toRadians(alpha)) * Math.sin(Math.toRadians(beta)))
                / (Math.sin(Math.toRadians(alpha + beta)));

        double distanceFromAtoTarget = distance / Math.sin(Math.toRadians(alpha));

        double x = stationA.getLatitude() + Math.sin(Math.toRadians(azimuthA)) * distanceFromAtoTarget;
        double y = stationA.getLongitude() + Math.cos(Math.toRadians(azimuthA)) * distanceFromAtoTarget;

        return new SkyObject(new Coordinates(x, y));
    }

    private static class TriangulationFailedException extends Exception {
        public TriangulationFailedException(String message) {
            super(message);
        }
    }
}
