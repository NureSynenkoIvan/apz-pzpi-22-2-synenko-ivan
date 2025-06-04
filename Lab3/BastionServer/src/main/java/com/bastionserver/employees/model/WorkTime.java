package com.bastionserver.employees.model;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Set;

public class WorkTime {
    private final Set<DayOfWeek> workDays;
    private final LocalTime shiftStart;
    private final LocalTime shiftFinish;

    public WorkTime(Set<DayOfWeek> workDays,
                    LocalTime shiftStart,
                    LocalTime shiftFinish) {
        this.workDays = EnumSet.copyOf(workDays);
        this.shiftStart = shiftStart;
        this.shiftFinish = shiftFinish;
    }

    public Set<DayOfWeek> getWorkDays() {
        return workDays;
    }

    public LocalTime getShiftStart() {
        return shiftStart;
    }

    public LocalTime getShiftFinish() {
        return shiftFinish;
    }
}