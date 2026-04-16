//Francis
//Prompt: Generate a prompt you would ask an AI to generate tests for monster.java. 
//2nd Prompt: Act as an expert software tester and generate a JUnit 5 suite for Monster.java in the com.example.haunted.model package. 
// You must avoid all mocking frameworks, using real objects instead to verify defensive copying of the loot list and health clamping logic (0 to maxHealth). 
// Ensure high mutation coverage by testing unmodifiable list views and using @ParameterizedTest for boundary values.

package com.example.haunted.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MonsterTest {
    private List<Item> testLoot;
    private Monster monster;
    private final int MAX_HP = 100;

    @BeforeEach
    void setup() {
        testLoot = new java.util.ArrayList<>(List.of(new Key("Old Key", "A rusty key.")));
        monster = new Monster("Test Ghost", MAX_HP, 10, 5, testLoot);
    }

    @Test
    @DisplayName("Mutation Killing: Verify defensive copy of loot in constructor")
    void testConstructorDefensiveCopy() {
        // Modify the original list passed to the constructor
        testLoot.add(new Key("Secret Key", "Should not be in monster"));
        
        // The monster's internal list should still only have 1 item
        assertEquals(1, monster.getLoot().size(), 
            "Monster should hold a defensive copy of the loot list.");
    }

    @Test
    @DisplayName("Mutation Killing: Verify getLoot() returns unmodifiable view")
    void testGetLootUnmodifiable() {
        List<Item> monsterLoot = monster.getLoot();
        assertThrows(UnsupportedOperationException.class, () -> {
            monsterLoot.add(new Key("Cheat Item", "Bypassing logic"));
        }, "The list returned by getLoot() must be unmodifiable.");
    }

    @ParameterizedTest
    @CsvSource({
        "10, 90, false",  // Standard damage
        "100, 0, true",   // Exactly zero (Boundary)
        "150, 0, true"    // Overkill (Clamping check)
    })
    @DisplayName("Logic: Verify takeDamage reduces health and clamps at zero")
    void testTakeDamageClamping(int damage, int expectedHealth, boolean shouldBeDefeated) {
        monster.takeDamage(damage);
        assertAll(
            () -> assertEquals(expectedHealth, monster.getHealth(), "Health should clamp at 0"),
            () -> assertEquals(shouldBeDefeated, monster.isDefeated(), "Defeated status is incorrect")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "20, 70, 90",     // Standard heal
        "50, 70, 100",    // Heal to exactly max (Boundary)
        "100, 70, 100"    // Over-heal (Clamping check)
    })
    @DisplayName("Logic: Verify heal increases health and clamps at maxHealth")
    void testHealClamping(int healAmount, int currentHealth, int expectedHealth) {
        // Set monster to specific health via damage first
        monster.takeDamage(MAX_HP - currentHealth); 
        
        monster.heal(healAmount);
        assertEquals(expectedHealth, monster.getHealth(), "Health should not exceed maxHealth");
    }

    @Test
    @DisplayName("Null Safety: Constructor should reject null name or loot")
    void testNullGuards() {
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> new Monster(null, 10, 5, 5, testLoot)),
            () -> assertThrows(NullPointerException.class, () -> new Monster("Ghost", 10, 5, 5, null))
        );
    }
}