package com.bastionserver;

import com.bastionserver.employees.model.Coordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CoordinatesTest {

    @Test
    void test() {
        Coordinates coordinates1 = new Coordinates(0, 0);
        Coordinates coordinates2 = new Coordinates(1, 1);
        assertEquals(Math.sqrt(2), coordinates1.distanceTo(coordinates2));
    }
}
