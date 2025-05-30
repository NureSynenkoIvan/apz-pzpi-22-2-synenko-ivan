package com.bastionserver.analysis.service;

import com.bastionserver.analysis.SkyStatePublisher;
import com.bastionserver.devices.Device;
import com.bastionserver.analysis.model.Signal;
import com.bastionserver.analysis.model.SkyObject;
import com.bastionserver.analysis.model.SkyState;
import com.bastionserver.devices.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RadioAggregationService {
    private static final long MAX_WAIT_MS = 2000;
    private static final Logger log = LoggerFactory.getLogger(RadioAggregationService.class);
    private final ScheduledExecutorService scheduler;
    private final Map <Integer, Device> radioStations;
    private final DeviceRepository deviceRepository;
    private final Map<Device, List<SkyObject>> currentData = new ConcurrentHashMap<>();
    private List<Signal> currentRawSignals;
    private final AtomicInteger radioStationsLeft;
    private final AtomicBoolean listening = new AtomicBoolean(false);
    private final SkyStatePublisher publisher;

    @Autowired
    public RadioAggregationService(DeviceRepository deviceRepository,
                                   ScheduledExecutorService scheduler,
                                   SkyStatePublisher publisher) {
        this.scheduler = scheduler;
        this.deviceRepository = deviceRepository;
        radioStations = new HashMap<>();
                deviceRepository
                .findAll()
                .stream()
                .filter(Device::isOnline)
                .map(device -> radioStations.put(device.getDeviceId(), device));
        this.radioStationsLeft = new AtomicInteger(radioStations.size());
        radioStations.values()
                .forEach(d -> currentData.put(d, new ArrayList<>()));
        this.publisher = publisher;
        currentRawSignals = new LinkedList<>();
        log.info("RadioAggregationService started");
    }

    public synchronized void onDroneData(List<SkyObject> data) {
        if (!listening.get()) {
            listening.set(true);
            scheduler.schedule(this::onTimeout, MAX_WAIT_MS, TimeUnit.MILLISECONDS);
        }
        int id = data.get(0).getDeviceId();
        Device device = radioStations.get(id);

        if (currentData.get(device).isEmpty()) radioStationsLeft.decrementAndGet();
        currentData.put(device, data);
        if (radioStationsLeft.get() == 0) {
            publishSkyState();
        }
    }

    public synchronized void onSignalData(List<Signal> data) {
        currentRawSignals.addAll(data);
    }

    private synchronized void onTimeout() {
        if (listening.get()) publishSkyState();
    }

    private void publishSkyState() {
        publisher.publish(new SkyState(currentData, currentRawSignals));

        // reset
        listening.set(false);
        radioStationsLeft.set(currentData.size());
        currentData.replaceAll((k,v)->new ArrayList<>());
        currentRawSignals = new ArrayList<>();
    }

    public void reloadDevices() {
        radioStations.clear();
        deviceRepository
                .findAll()
                .stream()
                .filter(Device::isOnline)
                .forEach(device -> radioStations.put(device.getDeviceId(), device));
        radioStationsLeft.set(radioStations.size());
        currentData.clear();
        radioStations.values().forEach(d -> currentData.put(d, new ArrayList<>()));
    }
}
