//Brady
//Prompt:
//i need tests for the Player class. check that takeDamage lowers health, that health
//cant go below 0 even with huge damage (use parameterized tests), that heal works but
//doesn't overshoot the max hp, player is dead when hp hits 0, and that weapons and armor
//actually update the attack and defense numbers. call the player Alex

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class PlayerTest {

    @Test
    void testTakeDamage() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.takeDamage(30);
        assertEquals(70, alex.getHealth()); // 100 - 30 = 70
    }

    //prof wants us to use parameterized tests so here we go
    @ParameterizedTest(name = "taking {0} damage from {1} HP leaves {2} HP")
    @CsvSource({
        "10, 100, 90",
        "50, 100, 50",
        "100, 100, 0",
        "999, 100, 0"   //overkill should floor at zero not go negative
    })
    void healthCantGoNegative(int damage, int maxHp, int expectedHealth) {
        Player alex = new Player("Alex", maxHp, 10, 5, new Inventory(10));
        alex.takeDamage(damage);
        assertEquals(expectedHealth, alex.getHealth());
    }

    @Test
    void healingRestoresHP() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.takeDamage(40);
        alex.heal(20);
        assertEquals(80, alex.getHealth());
    }

    @Test
    void cantHealPastMax() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.heal(999); //should still cap at 100
        assertEquals(100, alex.getHealth());
    }

    @Test
    void playerDiesAt0HP() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.takeDamage(100);
        assertFalse(alex.isAlive());
    }

    @Test
    void alexIsAliveWhenAtFullHealth() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        assertTrue(alex.isAlive());
    }

    @Test
    void weaponBoostsAttack() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        int baseAtk = alex.getAttackPower();
        alex.equipWeapon(new Weapon("Rusty Stapler", "Better than nothing.", 5));
        assertEquals(baseAtk + 5, alex.getAttackPower()); // +5 bonus stacks on top of base
    }

    @Test
    void armorBoostsDefense() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        int baseDef = alex.getDefensePower();
        alex.equipArmor(new Armor("Trash Lid Shield", "A bin lid repurposed for war.", 4));
        assertEquals(baseDef + 4, alex.getDefensePower());
    }

    @Test
    void noWeaponMeansJustBaseAttack() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        assertNull(alex.getEquippedWeapon());
        assertEquals(10, alex.getAttackPower());
    }

    @Test
    void noArmorMeansJustBaseDefense() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        assertNull(alex.getEquippedArmor());
        assertEquals(5, alex.getDefensePower());
    }
}
