package com.bastionserver.analysis.service.strategy;

import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.devices.DeviceRepository;

import java.util.ArrayList;
import java.util.List;


//This is a sample, mock strategy used for test purposes.
//It can be changed.
@Deprecated
public class MockStrategy extends SignalAnalysisStrategy implements ThreatAnalysisStrategy {
    public MockStrategy(DeviceRepository deviceRepository) {
        super(deviceRepository);
    }

    @Override
    protected List<List<Signal>> groupSignalsOfSameFrequency(List<Signal> signalsOfSameFrequency) {
        return List.of(signalsOfSameFrequency);
    }

    @Override
    public List<SkyObject> analyze(SkyState skyState) {
        List<SkyObject> triangulatedObjects = new ArrayList<>();
        triangulatedObjects = this.triangulateSkyObjects(skyState.getRawSignals());

        if (!triangulatedObjects.isEmpty()) {
            skyState.addToSkyObjects(this.devices, triangulatedObjects);
        }
        List<SkyObject> threateningObjects = new ArrayList<SkyObject>();
        for (List<SkyObject> objects : skyState.getSkyObjects().values()) {
            for (SkyObject object : objects) {
                if (!object.isTriangulated()) {
                    //If "home" is in Russia
                    if (object.getDroneData().getLatitude_home() > 40
                            && object.getDroneData().getLongitude_home() > 40) {
                        threateningObjects.add(object);
                    }
                } else {
                    if (object.getCoordinates().distanceTo(pointToDefend) * MAP_DEGREE < 2) {
                        threateningObjects.add(object);
                    }
                }
            }
        }
        return threateningObjects;
    }
}
