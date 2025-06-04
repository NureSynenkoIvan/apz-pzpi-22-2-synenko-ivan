package com.bastionserver.employees.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/shift")
public class ShiftEntranceController {
    private static final long QR_EXPIRATION_TIME_MILLIS = TimeUnit.MINUTES.toMillis(10);
    private String currentQRCodeKey;
    private Date currentQRCodeKeyCreateTime = new Date();

    private String getCurrentQRCodeKey() {
        long timeSinceCodeCreation = new Date().getTime() - currentQRCodeKeyCreateTime.getTime();

        if (timeSinceCodeCreation > QR_EXPIRATION_TIME_MILLIS) {
            currentQRCodeKey = null;
        }

        if (currentQRCodeKey == null) {
            currentQRCodeKey = UUID.randomUUID().toString();
            currentQRCodeKeyCreateTime = new Date();
        }
        return currentQRCodeKey;
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public String getQRKey() {
        return getCurrentQRCodeKey();
    }

    @PostMapping
    public boolean checkQRKey(@RequestParam("key") String key) {
        return key.equals(currentQRCodeKey);
    }

}
