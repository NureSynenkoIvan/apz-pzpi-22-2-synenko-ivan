package com.bastionserver.employees.service;

import com.bastionserver.employees.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private PasswordEncoder passwordEncoder;

    @Value("${default-password}")
    private String defaultPassword;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getById(String id) {
        return employeeRepository.findById(id);
    }

    public Optional<Employee> getByPhoneNumber(String phoneNumber) {
        return employeeRepository.findEmployeeByPhoneNumber(phoneNumber);
    }

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
    }

    public List<Employee> getAllSortedByLastNameAsc() {
        List<Employee> employees = employeeRepository.findAll();
        employees.sort(Comparator.comparing(Employee::getLastName));
        return employees;
    }

    public List<Employee> findByOnShiftTrueOrderByLastNameAsc() {
        return employeeRepository.findByOnDutyTrueOrderByLastNameAsc();
    }

    public Employee update(Employee employee) {
        try {
            Employee savedEmployee = employeeRepository.findEmployeeByPhoneNumber(employee.getPhoneNumber()).orElseThrow();
            if (! employee.getPasswordHash().equals(savedEmployee.getPasswordHash())) {
                employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
            }
            employeeRepository.deleteByPhoneNumber(employee.getPhoneNumber());
        } catch (Exception e) {

        }
        return employeeRepository.save(employee);
    }

    public void delete(Employee employee) {
        employeeRepository.deleteByPhoneNumber(employee.getPhoneNumber());
    }

    public void deleteByPhoneNumber(String phoneNumber) {
        employeeRepository.deleteByPhoneNumber(phoneNumber);
    }


    public void toggleOffShift(String phoneNumber) {
        Employee employee = employeeRepository.findEmployeeByPhoneNumber(phoneNumber).orElseThrow();
        if (employee.isOnDuty()) {
            getOffShift(employee);
        } else {
            getOntoShift(employee);
        }
    }

    public void getOntoShift(Employee employee) {
        employee.setOnDuty(true);
        update(employee);
    }

    public void getOffShift(Employee employee) {
        employee.setOnDuty(false);
        update(employee);
    }

    public Employee hashEmployeePassword(Employee employee) {
        employee.setPasswordHash(passwordEncoder.encode(employee.getPasswordHash()));
        return employee;
    }

    public Employee setPasswordToDefault(Employee employee) {
        employee.setPasswordHash(passwordEncoder.encode(defaultPassword));
        return employee;
    }
}