package com.bastionserver;

import com.bastionserver.employees.controller.EmployeeController;
import com.bastionserver.employees.model.Employee;
import com.bastionserver.employees.model.Role;
import com.bastionserver.employees.service.EmployeeRepository;
import com.bastionserver.employees.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(SecurityConfiguration.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @Test
    @WithMockUser(username = "dispatcher1", roles = {"dispatcher"})
    void testGetAllEmployees() throws Exception {
        List<Employee> mockEmployees = List.of(
                new Employee("John", "Doe", "123", Role.USER, "Tech", null, null, false, "pass")
        );

        when(employeeService.getAllSortedByLastNameAsc()).thenReturn(mockEmployees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser(username = "admin1", roles = {"administrator"})
    void testGetEmployeeFound() throws Exception {
        Employee mockEmployee = new Employee("Anna", "Smith", "456", Role.USER, "Manager", null, null, false, "pass");
        when(employeeService.getByPhoneNumber("456")).thenReturn(Optional.of(mockEmployee));

        mockMvc.perform(get("/employees/view")
                        .param("phoneNumber", "456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Anna"));
    }

    @Test
    @WithMockUser(username = "dispatcher1", roles = {"dispatcher"})
    void testGetEmployeeNotFound() throws Exception {
        when(employeeService.getByPhoneNumber("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/employees/view")
                        .param("phoneNumber", "999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAccessDeniedWithoutLogin() throws Exception {
        mockMvc.perform(get("/employees"))
                .andExpect(status().isUnauthorized());
    }
}