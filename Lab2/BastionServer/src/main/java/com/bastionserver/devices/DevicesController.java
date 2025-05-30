package com.bastionserver.devices;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
public class DevicesController {

    private DevicesService devicesService;

    @Autowired
    public DevicesController(DevicesService deviceService) {
        this.devicesService = deviceService;
    }


    @GetMapping("/view")
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<Device> getDevice(@RequestParam String name) {
        Device device = devicesService.get(name);
        return device != null ? ResponseEntity.ok(device) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<List<Device>> getAllDevices() {
        List<Device> devices = devicesService.getAllSortedAsc();
        return ResponseEntity.ok(devices);
    }

    @PostMapping
    @Secured("ROLE_administrator")
    public ResponseEntity<Void> addDevice(@RequestBody Device device) {
        devicesService.save(device);
        return ResponseEntity.status(201).build(); // HTTP 201 CREATED
    }

    @PutMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<Void> updateDevice(@RequestBody Device device) {
        devicesService.update(device);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Secured("ROLE_administrator")
    public ResponseEntity<Void> deleteDevice(@RequestBody Device device) {
        devicesService.delete(device);
        return ResponseEntity.ok().build();
    }
}