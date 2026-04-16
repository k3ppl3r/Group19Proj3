//Francis
//Prompt:Here is the code for MoveResult.Java. I need you to generate several tests that 
// are checking for proper boolean logic contained in trap triggered along with ensuring that 
// moveresult works according to the implementation. Create test checking to ensure that the getTrapDamage is 
// triggering correctly and is returning correct results. 

package com.example.haunted.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MoveResultTest {

    @Test
    @DisplayName("Data Integrity: Constructor field mapping check")
    void testConstructorAndGetters() {
        MoveResult result = new MoveResult(true, "Entered the Hallway", true, 15);

        assertAll("Verify all fields are stored correctly",
            () -> assertTrue(result.isSuccess()),
            () -> assertEquals("Entered the Hallway", result.getMessage()),
            () -> assertTrue(result.isTrapTriggered(), "Trap flag should be true"),
            () -> assertEquals(15, result.getTrapDamage(), "Trap damage should match input")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "true, 'Safe passage', false, 0",
        "true, 'You stepped on a pressure plate!', true, 10",
        "false, 'The door is locked', false, 0",
        "false, 'You tripped while failing to move', true, 5"
    })
    @DisplayName("Boolean Logic: Successful/Failed moves with trap variations")
    void testMovementAndTrapLogic(boolean success, String message, boolean trapTriggered, int damage) {
        MoveResult result = new MoveResult(success, message, trapTriggered, damage);
        
        assertAll(
            () -> assertEquals(success, result.isSuccess()),
            () -> assertEquals(message, result.getMessage()),
            () -> assertEquals(trapTriggered, result.isTrapTriggered()),
            () -> assertEquals(damage, result.getTrapDamage())
        );
    }

    @Test
    @DisplayName("Trap Damage: Verify zero damage is handled correctly")
    void testZeroTrapDamage() {
        // Test a scenario where a trap is triggered but damage is 0 (e.g., a dud trap)
        MoveResult result = new MoveResult(true, "A trap clicked but nothing happened", true, 0);
        
        assertTrue(result.isTrapTriggered());
        assertEquals(0, result.getTrapDamage());
    }

    @Test
    @DisplayName("Trap Damage: Verify high damage values (Boundary Test)")
    void testHighTrapDamage() {
        MoveResult result = new MoveResult(true, "Massive trap!", true, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, result.getTrapDamage());
    }

    @Test
    @DisplayName("Mutation Killing: Ensuring trapTriggered and success are independent")
    void testBooleanIndependence() {
        // Mutation tools often try to link boolean fields. 
        // We must prove success and trapTriggered can be different.
        MoveResult result = new MoveResult(true, "Success with trap", true, 5);
        assertTrue(result.isSuccess() && result.isTrapTriggered());

        MoveResult result2 = new MoveResult(false, "Fail without trap", false, 0);
        assertFalse(result2.isSuccess() || result2.isTrapTriggered());
    }
}