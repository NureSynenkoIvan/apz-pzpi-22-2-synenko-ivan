package com.bastionserver.employees.controller;

import com.bastionserver.employees.model.Employee;
import com.bastionserver.employees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured; // або @PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/view")
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<Employee> getEmployee(@RequestParam String phoneNumber) {
        Optional<Employee> employee = employeeService.getByPhoneNumber(phoneNumber);
        return employee.isPresent() ? ResponseEntity.ok(employee.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllSortedByLastNameAsc();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/on-shift")
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<List<Employee>> getAllOnShift() {
        List<Employee> employees = employeeService.findByOnShiftTrueOrderByLastNameAsc();
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<Void> addEmployee(@RequestBody Employee employee) {
        employeeService.save(employee);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<Void> updateEmployee(@RequestBody Employee employee) {
        employeeService.update(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    public ResponseEntity<Void> deleteEmployee(@RequestBody Employee employee) {
        employeeService.delete(employee);
        return ResponseEntity.ok().build();
    }
}

