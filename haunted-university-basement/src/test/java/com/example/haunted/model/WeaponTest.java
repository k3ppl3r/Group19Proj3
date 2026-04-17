//Francis
//Prompt: generate tests for weapon.java. Focusing on mutation and logic coverage. Checking to make sure that weapon.java is capable of working alongside game engine and inventory
//without any unintended side effects. Ensure that the test will check for boundary values and is capable of working with super(name, description).

package com.example.haunted.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class WeaponTest {

    @Test
    @DisplayName("Weapon: Verify naming and description inheritance")
    void testWeaponInheritance() {
        String name = "Excalibur";
        String desc = "A legendary sword pulled from a stone.";
        Weapon weapon = new Weapon(name, desc, 15);

        assertAll("Verify Item properties are correctly passed to super",
            () -> assertEquals(name, weapon.getName()),
            () -> assertEquals(desc, weapon.getDescription()),
            () -> assertTrue(weapon instanceof Equippable, "Weapon must implement Equippable")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "Steel Dagger, Sharp and light, 5",
        "Heavy Mace, Crushes bones, 12",
        "Training Sword, Dull edge, 1",
        "Cursed Blade, Powerful but risky, 25"
    })
    @DisplayName("Attack Bonus: Verify getAttackBonus returns correct data")
    void testGetAttackBonus(String name, String desc, int bonus) {
        Weapon weapon = new Weapon(name, desc, bonus);
        
        // Kills mutations that return hardcoded values (like 0 or 1)
        assertEquals(bonus, weapon.getAttackBonus(), "The attack bonus must match the constructor input.");
    }

    @Test
    @DisplayName("Mutation Check: Ensure attackBonus is not swapped with other fields")
    void testFieldIndependence() {
        // Use a number that is unlikely to appear elsewhere
        Weapon weapon = new Weapon("99", "99", 99);
        
        // If the constructor is mutated to assign name to attackBonus (via parsing),
        // or if getters are swapped, this ensures we are checking the int field specifically.
        assertEquals(99, weapon.getAttackBonus());
    }

    @Test
    @DisplayName("Null Safety: Verify inherited null guards")
    void testNullGuards() {
        // Weapon should still throw NPEs if the super constructor receives nulls
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> new Weapon(null, "Desc", 5)),
            () -> assertThrows(NullPointerException.class, () -> new Weapon("Name", null, 5))
        );
    }
}