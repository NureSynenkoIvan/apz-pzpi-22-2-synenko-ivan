package com.bastionserver.devices;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
    Collection<Object> findDeviceByName(String name);

    Device findFirstByName(String deviceName);

    void deleteByName(String deviceName);
}
