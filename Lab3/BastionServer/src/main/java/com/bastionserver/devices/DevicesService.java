package com.bastionserver.devices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DevicesService {
    private DeviceRepository deviceRepository;

    @Autowired
    public DevicesService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device get(String deviceName) {
        return deviceRepository.findFirstByName(deviceName);
    }

    public List<Device> getAllSortedAsc() {
        return deviceRepository
                .findAll()
                .stream()
                .filter(el -> el.getLocation() != null)
                .toList();
    }

    public void save(Device device) {
        deviceRepository.save(device);
    }

    public void delete(Device device) {
        deviceRepository.deleteByName(device.getName());
    }

    public void update(Device device) {
        deviceRepository.deleteByName(device.getName());
        deviceRepository.save(device);
    }
}