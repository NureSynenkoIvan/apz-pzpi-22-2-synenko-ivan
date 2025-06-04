package com.bastionserver;

import com.bastionserver.devices.Device;
import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.devices.DeviceRepository;
import com.bastionserver.analysis.service.strategy.SignalAnalysisStrategy;
import com.bastionserver.employees.model.Coordinates;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SignalAnalysisStrategyTests {

    private static class SignalAnalysisStrategyImpl extends SignalAnalysisStrategy {

        protected SignalAnalysisStrategyImpl(DeviceRepository deviceRepository) {
            super(deviceRepository);
        }

        protected SignalAnalysisStrategyImpl() {
            super();
        }

        protected SignalAnalysisStrategyImpl(List<Device> devices) {
            this.devices = new HashMap<>();
            for (Device device : devices) {
                this.devices.put(device.getDeviceId(), device);
            }
        }

        @Override
        protected List<List<Signal>> groupSignalsOfSameFrequency(List<Signal> signalsOfSameFrequency) {
            return List.of(signalsOfSameFrequency);
        }

        @Override
        public List<SkyObject> analyze(SkyState skyState) {
            return List.of();
        }
    }

    private record TestData(List<Signal> signals,
                            List<Device> devices,
                            List<SkyObject> expectedObjects) {
    }

    ;

    @Test
    void testTriangulationOK() {
        double delta = 0.001;


        List<TestData> testData = new ArrayList<>();

        testData.add(new TestData(List.of(
                new Signal(47.7, 1.1, 0.0, new Date(), 1),
                new Signal(47.7, 1.1, -30.0, new Date(), 2)),
                List.of(
                        new Device(
                                1,
                                "1",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(1, 1)),
                        new Device(
                                2,
                                "2",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(4, 1))),
                List.of(new SkyObject(new Coordinates(1.0, 6.196)))));

        testData.add(new TestData(List.of(
                new Signal(47.7, 1.1, 45, new Date(), 1),
                new Signal(47.7, 1.1, -90.0, new Date(), 2)),
                List.of(
                        new Device(
                                1,
                                "1",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(1, 1)),
                        new Device(
                                2,
                                "2",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(11, 5))),
                List.of(new SkyObject(new Coordinates(5, 5)))));

        for (TestData data : testData) {
            var strategy = new SignalAnalysisStrategyImpl(data.devices);

            List<SkyObject> expectedObjects = data.expectedObjects;

            for (SkyObject expectedObject : expectedObjects) {
                double expectedLatitude = expectedObject.getLatitude();
                double expectedLongitude = expectedObject.getLongitude();

                SkyObject actualObject = strategy.triangulateSkyObjects(data.signals).get(0);
                double actualLatitude = actualObject.getCoordinates().getLatitude();
                double actualLongitude = actualObject.getCoordinates().getLongitude();

                assertEquals(expectedLatitude, actualLatitude, delta, "Latitude mismatch");
                assertEquals(expectedLongitude, actualLongitude, delta, "Longitude mismatch");
            }
        }
    }

    @Test
    void testTrilaterationOK() {
        double delta = 0.001;

        List<TestData> testData = new ArrayList<>();

        testData.add(new TestData(List.of(
                new Signal(47.7, 1.1, 0.0, new Date(), 1),
                new Signal(47.7, 1.1, -30.0, new Date(), 2),
                new Signal(47.7, 1.1, -90.0, new Date(), 3)),
                List.of(
                        new Device(
                                1,
                                "1",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(1, 1)),
                        new Device(
                                2,
                                "2",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(1, 5)),
                        new Device(
                                3,
                                "3",
                                Device.DeviceType.RADIO_STATION,
                                new Coordinates(5, 5))),
                List.of(new SkyObject(new Coordinates(1, 5)))));

        for (TestData data : testData) {
            var strategy = new SignalAnalysisStrategyImpl(data.devices);

            List<SkyObject> expectedObjects = data.expectedObjects;

            for (SkyObject expectedObject : expectedObjects) {
                double expectedLatitude = expectedObject.getLatitude();
                double expectedLongitude = expectedObject.getLongitude();

                SkyObject actualObject = strategy.triangulateSkyObjects(data.signals).get(0);
                double actualLatitude = actualObject.getCoordinates().getLatitude();
                double actualLongitude = actualObject.getCoordinates().getLongitude();

                assertEquals(expectedLatitude, actualLatitude, delta, "Latitude mismatch");
                assertEquals(expectedLongitude, actualLongitude, delta, "Longitude mismatch");
            }
        }
    }

}
