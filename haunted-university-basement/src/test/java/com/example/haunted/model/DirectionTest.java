//Brady
//Prompt:
//tests for the Direction enum. check all four values exist, that valueOf works for each,
//that the name strings match what we expect, and that there are exactly 4 directions.
//use a parameterized test for the valueOf checks

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DirectionTest {

    @Test
    void allFourDirectionsExist() {
        assertEquals(4, Direction.values().length);
    }

    @ParameterizedTest(name = "Direction.valueOf(\"{0}\") should not be null")
    @CsvSource({"NORTH", "SOUTH", "EAST", "WEST"})
    void valueOfWorksForEachDirection(String name) {
        assertNotNull(Direction.valueOf(name));
    }

    @Test
    void directionNamesMatchExpected() {
        assertEquals("NORTH", Direction.NORTH.name());
        assertEquals("SOUTH", Direction.SOUTH.name());
        assertEquals("EAST", Direction.EAST.name());
        assertEquals("WEST", Direction.WEST.name());
    }

    @Test
    void directionsAreDistinct() {
        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; i++) {
            for (int j = i + 1; j < dirs.length; j++) {
                assertNotEquals(dirs[i], dirs[j]);
            }
        }
    }
}
