package com.bastionserver.employees.controller;

import com.bastionserver.employees.model.Employee;
import com.bastionserver.employees.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured; // або @PreAuthorize
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
@CrossOrigin
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

    @GetMapping("/all")
    @CrossOrigin
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
    @CrossOrigin
    public ResponseEntity<Void> addEmployee(@RequestBody Employee employee) {
        if (employee.getPasswordHash() == null || employee.getPasswordHash().isEmpty()) {
            employee = employeeService.setPasswordToDefault(employee);
        } else {
            employee = employeeService.hashEmployeePassword(employee);
        }

        employeeService.save(employee);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public ResponseEntity<Void> updateEmployee(@RequestBody Employee employee) {
        employeeService.update(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public ResponseEntity<Void> deleteEmployee(@RequestBody Employee employee) {
        employeeService.delete(employee);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public ResponseEntity<Void> deleteEmployee(@RequestParam String phoneNumber) {
        employeeService.deleteByPhoneNumber(phoneNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/shift")
    @Secured({"ROLE_dispatcher", "ROLE_administrator"})
    @CrossOrigin
    public ResponseEntity<Void> getEmployeeToShift(@RequestParam("phoneNumber") String phoneNumber) {
        try {
            employeeService.toggleOffShift(phoneNumber);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

