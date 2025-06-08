package com.bastionserver.notification.notifiers.impl.firebase.geolocation;

import com.bastionserver.BastionServerApplication;
import com.bastionserver.employees.model.Coordinates;
import com.bastionserver.employees.model.Employee;
import com.mongodb.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.bastionserver.notification.notifiers.impl.firebase.FirebaseConfiguration.sendMessage;


@Service
public class FirebaseGeolocationService {
    private static final Logger log = LoggerFactory.getLogger(FirebaseGeolocationService.class);
    private ConcurrentHashMap<Employee, String> activeFCMTokens;
    private BlockingQueue<Employee> locatedEmployees;

    public FirebaseGeolocationService(ConcurrentHashMap<Employee, String> activeFCMTokens, BlockingQueue<Employee> locatedEmployees) {
        this.activeFCMTokens = activeFCMTokens;
        this.locatedEmployees = locatedEmployees;
    }

    public void locateEmployees() {
        boolean isAlarm = BastionServerApplication.isAlarm();
        try {
            for (Employee employee : activeFCMTokens.keySet()) {
                sendMessage(activeFCMTokens.get(employee), isAlarm, "SEND_LOCATION");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
