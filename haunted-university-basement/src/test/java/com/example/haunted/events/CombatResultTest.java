//Francis
//Prompt: (input CombatResult.java) generate test cases following the same style, structure, 
//and concerns of GameEngineTest.java. Focus on generating tests that validate and test that check for 
//mutations.

package com.example.haunted.events;

import com.example.haunted.model.Item;
import com.example.haunted.model.Key;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatResultTest {

    @Test
    @DisplayName("Data Integrity: Constructor should correctly assign all fields")
    void testConstructorAndGetters() {
        List<Item> loot = Arrays.asList(new Key("Old Key", "Opens a chest"));
        CombatResult result = new CombatResult(true, "You hit the ghost!", 10, 5, false, loot);

        assertAll("Verify all fields are stored correctly",
            () -> assertTrue(result.isSuccess()),
            () -> assertEquals("You hit the ghost!", result.getMessage()),
            () -> assertEquals(10, result.getDamageToMonster()),
            () -> assertEquals(5, result.getDamageToPlayer()),
            () -> assertFalse(result.isMonsterDefeated()),
            () -> assertEquals(1, result.getDroppedItems().size()),
            () -> assertEquals("Old Key", result.getDroppedItems().get(0).getName())
        );
    }

    @ParameterizedTest
    @CsvSource({
        "true, Monster defeated!, 50, 0, true",
        "true, Critical hit!, 20, 10, false",
        "false, You missed!, 0, 5, false"
    })
    @DisplayName("Logic Combinations: Test various combat outcome states")
    void testCombatOutcomeCombinations(boolean success, String msg, int dmgM, int dmgP, boolean defeated) {
        CombatResult result = new CombatResult(success, msg, dmgM, dmgP, defeated, new ArrayList<>());
        
        assertEquals(success, result.isSuccess());
        assertEquals(defeated, result.isMonsterDefeated());
        assertEquals(msg, result.getMessage());
    }

    @Test
    @DisplayName("Immutability: Ensure the internal list is a defensive copy")
    void testDefensiveCopy() {
        List<Item> originalLoot = new ArrayList<>();
        originalLoot.add(new Key("Key A", "Desc"));
        
        CombatResult result = new CombatResult(true, "Hit", 5, 0, false, originalLoot);
        
        // Modify the original list passed to the constructor
        originalLoot.add(new Key("Key B", "Desc"));
        
        // The result's list should NOT have changed (Mutation Kill: detects if copy was made)
        assertEquals(1, result.getDroppedItems().size(), 
            "Modifying the input list should not affect the CombatResult internal state.");
    }

    @Test
    @DisplayName("Immutability: Ensure returned list cannot be modified")
    void testUnmodifiableList() {
        CombatResult result = new CombatResult(true, "Hit", 5, 0, false, new ArrayList<>());
        List<Item> returnedLoot = result.getDroppedItems();

        // Attempting to modify the returned list should throw an exception
        // (Mutation Kill: detects if Collections.unmodifiableList was used)
        assertThrows(UnsupportedOperationException.class, () -> {
            returnedLoot.add(new Key("Illegal Key", "Should fail"));
        }, "The list returned by getDroppedItems should be unmodifiable.");
    }

    @Test
    @DisplayName("Edge Cases: Damage values should handle zero and high values")
    void testDamageBoundaries() {
        CombatResult result = new CombatResult(true, "Overkill", Integer.MAX_VALUE, 0, true, new ArrayList<>());
        
        assertEquals(Integer.MAX_VALUE, result.getDamageToMonster());
        assertEquals(0, result.getDamageToPlayer());
    }
}