package com.bastionserver.employees.controller;

import com.bastionserver.employees.model.Employee;
import com.bastionserver.employees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
@CrossOrigin
public class LoginController {
    private EmployeeService employeeService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(EmployeeService employeeService, PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @CrossOrigin
    @Secured({"ROLE_administrator"})
    public ResponseEntity<?> getHashedPassword(@RequestParam String password) {
        return ResponseEntity.ok(passwordEncoder.encode(password));
    }

    @PostMapping
    public ResponseEntity<Map> login(@RequestParam String phoneNumber, @RequestParam String password) {
        try {
            Employee employee = employeeService.getByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (passwordEncoder.matches(password, employee.getPasswordHash())) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("role", employee.getRole());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
    }
}
