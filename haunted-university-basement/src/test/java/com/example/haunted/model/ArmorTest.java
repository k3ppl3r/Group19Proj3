// Francis
// Prompt: Create a test that covers checks for proper naming, descriptions, and valid defensebonus for armor.java. 
// Search for any possible mutation bugs that could be discovered by tests. Finally create a test that 
// checks to make sure getdefensebonus returns the correct and valid data.

package com.example.haunted.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ArmorTest {

    @Test
    @DisplayName("Armor: Proper naming and description assignment")
    void testArmorNamingAndDescription() {
        String name = "Rusty Chainmail";
        String desc = "Old but still offers some protection.";
        Armor armor = new Armor(name, desc, 5);

        assertAll("Verify Item properties are inherited and assigned correctly",
            () -> assertEquals(name, armor.getName(), "Name should match the constructor input"),
            () -> assertEquals(desc, armor.getDescription(), "Description should match the constructor input"),
            () -> assertEquals(name, armor.toString(), "toString should return the item name")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "Plate Armor, Heavy protection, 20",
        "Leather Vest, Light and flexible, 5",
        "Magical Robe, Arcane defense, 0",
        "Cursed Bracers, Heavy but draining, -5"
    })
    @DisplayName("Defense Bonus: Validate getDefenseBonus returns correct and valid data")
    void testGetDefenseBonus(String name, String desc, int bonus) {
        Armor armor = new Armor(name, desc, bonus);
        
        // This kills mutations that might return a hardcoded value like '0' or '1'
        assertEquals(bonus, armor.getDefenseBonus(), "The defense bonus returned must match the constructor input.");
    }

    @Test
    @DisplayName("Mutation Check: Ensure Defense Bonus is independent and not hardcoded")
    void testDefenseBonusIndependence() {
        Armor light = new Armor("Light", "Desc", 5);
        Armor heavy = new Armor("Heavy", "Desc", 50);

        // Kills mutants where the field might be shared or the return value is static
        assertNotEquals(light.getDefenseBonus(), heavy.getDefenseBonus(), 
            "Different armor instances must return their specific defense bonuses.");
    }

    @Test
    @DisplayName("Robustness: Ensure null names or descriptions throw NullPointerException")
    void testNullGuards() {
        // Armor inherits the Objects.requireNonNull guards from the Item class
        assertAll(
            () -> assertThrows(NullPointerException.class, () -> new Armor(null, "Desc", 10)),
            () -> assertThrows(NullPointerException.class, () -> new Armor("Name", null, 10))
        );
    }

    @Test
    @DisplayName("Hierarchy: Verify Armor implements Equippable")
    void testEquippableImplementation() {
        Armor armor = new Armor("Gambeson", "Padded cloth", 2);
        assertTrue(armor instanceof Equippable, "Armor must implement the Equippable interface.");
        assertTrue(armor instanceof Item, "Armor must be a subclass of Item.");
    }
}