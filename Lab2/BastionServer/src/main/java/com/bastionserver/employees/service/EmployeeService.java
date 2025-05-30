package com.bastionserver.employees.service;

import com.bastionserver.employees.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
        return employeeRepository.save(employee);
    }

    public void delete(Employee employee) {
        employeeRepository.deleteByPhoneNumber(employee.getPhoneNumber());
    }
}