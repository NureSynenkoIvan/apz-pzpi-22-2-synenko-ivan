package com.bastionserver;

import com.bastionserver.devices.Device;
import com.bastionserver.devices.DeviceRepository;
import com.bastionserver.employees.model.Coordinates;
import com.bastionserver.employees.model.WorkTime;
import com.bastionserver.employees.service.EmployeeRepository;
import com.bastionserver.employees.model.*;
import com.bastionserver.notification.scenarios.MessageScenario;
import com.bastionserver.notification.scenarios.MessageScenarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Profile("dev")
@Component
public class DatabaseSeeder implements CommandLineRunner {
    private final DeviceRepository deviceRepo;
    private final EmployeeRepository employeeRepo;
    private final MessageScenarioRepository scenarioRepo;

    public DatabaseSeeder(DeviceRepository deviceRepo, EmployeeRepository employeeRepo, MessageScenarioRepository scenarioRepo) {
        this.deviceRepo = deviceRepo;
        this.employeeRepo = employeeRepo;
        this.scenarioRepo = scenarioRepo;
    }

    @Override
    public void run(String... args) {

        if (deviceRepo.count() > 0 || employeeRepo.count() > 0 || scenarioRepo.count() > 0) {
            System.out.println("Дані вже існують, пропускаємо заповнення.");
            return;
        }

        List<Device> devices = List.of(
                new Device(1, "Radar1", Device.DeviceType.RADAR, new Coordinates(38.045, 38.045)),
                new Device(2, "Radar2", Device.DeviceType.RADAR, new Coordinates(37.73, 39.045)),
                new Device(3, "Radar3", Device.DeviceType.RADAR, new Coordinates(37.045, 37.01))
        );

        Set<DayOfWeek> fullWeek = Set.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        WorkTime workTime = new WorkTime(fullWeek, LocalTime.of(8, 0), LocalTime.of(16, 0));

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        List<Employee> employees = List.of(
                new Employee("Boris", "Bilyy", "38123-456-7890", Role.DISPATCHER, "Supervisor", null, workTime, false, passwordEncoder.encode("password1")),
                new Employee("Kirylo", "Sarov", "38123-456-7891", Role.USER, "Technician", null, workTime, false, passwordEncoder.encode("password2")),
                new Employee("Mykyta", "Tomashko", "38123-456-7892", Role.USER, "Assistant", null, workTime, false, passwordEncoder.encode("password3")),
                new Employee("Ilia", "Zhurenko", "38123-456-7893", Role.USER, "Operator", null, workTime, false, passwordEncoder.encode("password4")),
                new Employee("Alexandr", "Kushchin", "38123-456-7894", Role.USER, "Manager", null, workTime, false, passwordEncoder.encode("password5")),
                new Employee("Erik", "Cantorovich", "38123-456-7895", Role.DISPATCHER, "Supervisor", null, workTime, false, passwordEncoder.encode("password6")),
                new Employee("Taras", "Levin", "38123-456-7896", Role.USER, "Technician", null, workTime, false, passwordEncoder.encode("password7")),
                new Employee("Vladyslav", "Vovchenko", "38123-456-7897", Role.USER, "Assistant", null, workTime, false, passwordEncoder.encode("password8")),
                new Employee("Savely", "Hryhorenko", "38123-456-7898", Role.USER, "Mechanic", null, workTime, false, passwordEncoder.encode("password9")),
                new Employee("Ivan", "Synenko", "38123-456-7899", Role.ADMINISTRATOR, "Director", null, workTime, false, passwordEncoder.encode("password0"))
        );

        List<MessageScenario> scenarios = List.of(
                new MessageScenario("Air Alert",
                        "Увага! Оголошена повітряна тривога! Негайно прослідуйте до укриття",
                        "Повітряна тривога завершена"),
                new MessageScenario("Chemical Alert",
                        "Увага! Хімічна тривога! Зберігайте спокій і негайно прослідуйте до укриття",
                        "Відбій хімічної тривоги")
        );

        // Збереження в базу

        System.out.println("Запуск сидера");

        devices.forEach(device -> {
            if (deviceRepo.findDeviceByName(device.getName()).isEmpty()) {
                deviceRepo.save(device);
                System.out.println(" Inserted device: " + device.getName());
            } else {
                System.out.println("Device already exists: " + device.getName());
            }
        });

        employees.forEach(employee -> {
            if (employeeRepo.findEmployeeByPhoneNumber(employee.getPhoneNumber()).isEmpty()) {
                employeeRepo.save(employee);
                System.out.println(" Inserted employee: " + employee.getFullName());
            } else {
                System.out.println(" Employee already exists: " + employee.getFullName());
            }
        });

        scenarios.forEach(scenario -> {
            if (scenarioRepo.findByScenarioName(scenario.getScenarioName()).isEmpty()) {
                scenarioRepo.save(scenario);
                System.out.println(" Inserted scenario: " + scenario.getScenarioName());
            } else {
                System.out.println(" Scenario already exists: " + scenario.getScenarioName());
            }
        });

        System.out.println(" Базу даних заповнено тестовими даними.");
    }
}
