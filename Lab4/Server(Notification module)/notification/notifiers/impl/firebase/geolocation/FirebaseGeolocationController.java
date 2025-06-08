package com.bastionserver.notification.notifiers.impl.firebase.geolocation;

import com.bastionserver.employees.model.Coordinates;
import com.bastionserver.employees.model.Employee;
import com.bastionserver.employees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

@RestController
@RequestMapping("/geo")
public class FirebaseGeolocationController {
    private static final int AWAIT_TIME_MILLIS = 10000;
    private static Set<Employee> employees = new HashSet<>();
    private FirebaseGeolocationService geolocationService;
    private ConcurrentHashMap<Employee, String> activeFCMTokens;
    @Autowired
    public FirebaseGeolocationController(FirebaseGeolocationService geolocationService, ConcurrentHashMap activeFCMTokens) {
        this.geolocationService = geolocationService;
        this.activeFCMTokens = activeFCMTokens;
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public Set<Employee> getLocatedEmployees() throws InterruptedException {
        geolocationService.locateEmployees();
        sleep(AWAIT_TIME_MILLIS);
        return employees;
    }

    @PostMapping
    public ResponseEntity<Object> postLocationData(@RequestParam String fcmToken,
                                                   @RequestParam double lat,
                                                   @RequestParam double lon) {
        Employee employee = getKey(activeFCMTokens, fcmToken);
        if (employee == null) throw new RuntimeException();
        employee.setLocation(new Coordinates(lat, lon));
        employees.add(employee);
        return ResponseEntity.ok().build();
    }

    public <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

}
