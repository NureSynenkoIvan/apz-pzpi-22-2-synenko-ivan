package com.bastionserver.employees.service;

import com.bastionserver.employees.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    Optional<Employee> findEmployeeByPhoneNumber(String phoneNumber);

    List<Employee> findByOnDutyTrueOrderByLastNameAsc();

    void deleteByPhoneNumber(String phoneNumber);
}