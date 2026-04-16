//Brady
//Prompt:
//Write humanized JUnit 5 tests for the Player model class covering: takeDamage reduces health correctly,
//health never drops below zero (parameterized with boundary values including overkill), heal restores health
//but cannot exceed max, player dies at zero HP, isAlive returns correct state, equipWeapon increases
//getAttackPower, equipArmor increases getDefensePower, and base attack/defense are used when nothing is equipped.
//Use "Alex" as the player name and keep test names conversational.

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
    void playerShouldLoseHealthWhenHitByAMonster() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        //Alex walks into a room and gets smacked for 30 damage
        alex.takeDamage(30);
        assertEquals(70, alex.getHealth(), "Alex should have 70 HP left after taking 30 damage");
    }

    @ParameterizedTest(name = "taking {0} damage from {1} HP leaves {2} HP")
    @CsvSource({
        "10, 100, 90",
        "50, 100, 50",
        "100, 100, 0",
        "999, 100, 0"   //overkill should still floor at zero, not go negative
    })
    void playerHealthNeverDropsBelowZero(int damage, int maxHp, int expectedHealth) {
        Player alex = new Player("Alex", maxHp, 10, 5, new Inventory(10));
        alex.takeDamage(damage);
        assertEquals(expectedHealth, alex.getHealth());
    }

    @Test
    void drinkingAPotionRestoresSomeHealth() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.takeDamage(40);   //hurt first
        alex.heal(20);
        assertEquals(80, alex.getHealth());
    }

    @Test
    void healingCantPushHealthAboveMaximum() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.heal(999);  //chug every potion in the known universe
        assertEquals(100, alex.getHealth(), "Cannot heal above max HP");
    }

    @Test
    void alexDiesAfterTakingFatalDamage() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        alex.takeDamage(100);
        assertFalse(alex.isAlive(), "Alex should be dead after taking 100 damage");
    }

    @Test
    void alexIsAliveWhenAtFullHealth() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        assertTrue(alex.isAlive());
    }

    @Test
    void pickingUpAWeaponIncreasesHowHardAlexHits() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        int unarmedAttack = alex.getAttackPower();
        alex.equipWeapon(new Weapon("Rusty Stapler", "Better than nothing.", 5));
        assertEquals(unarmedAttack + 5, alex.getAttackPower(),
                "Weapon bonus should stack on top of base attack");
    }

    @Test
    void wearingArmorMakesAlexHarderToHurt() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        int unarmored = alex.getDefensePower();
        alex.equipArmor(new Armor("Trash Lid Shield", "A bin lid repurposed for war.", 4));
        assertEquals(unarmored + 4, alex.getDefensePower(),
                "Armor bonus should stack on top of base defense");
    }

    @Test
    void alexWithNoWeaponStillUsesBaseAttack() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        assertNull(alex.getEquippedWeapon());
        assertEquals(10, alex.getAttackPower());
    }

    @Test
    void alexWithNoArmorStillUsesBaseDefense() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        assertNull(alex.getEquippedArmor());
        assertEquals(5, alex.getDefensePower());
    }
}
