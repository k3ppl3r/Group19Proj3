//Brady
//Prompt:
//tests for the Equippable interface. since Weapon and Armor both implement it, test that
//instances of each are recognized as Equippable, that their bonuses come back right,
//and that Potion is NOT Equippable. use a parameterized test for weapon attack bonuses
//at a few different values

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EquippableTest {

    @Test
    void weaponIsEquippable() {
        Weapon w = new Weapon("Stapler", "Office weapon.", 5);
        assertTrue(w instanceof Equippable);
    }

    @Test
    void armorIsEquippable() {
        Armor a = new Armor("Trash Lid", "Repurposed bin lid.", 3);
        assertTrue(a instanceof Equippable);
    }

    @Test
    void potionIsNotEquippable() {
        Potion p = new Potion("Coffee Potion", "Restores energy.", 20);
        assertFalse(p instanceof Equippable);
    }

    @ParameterizedTest(name = "weapon with attackBonus={0} should return {0}")
    @CsvSource({"1", "5", "10", "99"})
    void weaponReturnsCorrectAttackBonus(int bonus) {
        Weapon w = new Weapon("Test Weapon", "desc", bonus);
        assertEquals(bonus, w.getAttackBonus());
    }

    @Test
    void armorReturnsCorrectDefenseBonus() {
        Armor a = new Armor("Foam Breastplate", "Not great.", 7);
        assertEquals(7, a.getDefenseBonus());
    }

    @Test
    void weaponIsAlsoAnItem() {
        Weapon w = new Weapon("Ruler", "A 12-inch ruler.", 2);
        assertInstanceOf(Item.class, w);
        assertEquals("Ruler", w.getName());
    }

    @Test
    void armorIsAlsoAnItem() {
        Armor a = new Armor("Notebook Shield", "Spiral bound.", 4);
        assertInstanceOf(Item.class, a);
        assertEquals("Notebook Shield", a.getName());
    }
}
