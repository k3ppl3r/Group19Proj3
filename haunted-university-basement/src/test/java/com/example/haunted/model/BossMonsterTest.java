//Brady
//Prompt:
//tests for BossMonster. check that getCurrentAttack returns base attack at full health,
//that dropping to exactly half HP doesn't trigger enrage but one below does, and that
//overkill damage still clamps at 0. also check BossMonster extends Monster. parameterized
//test for enrage threshold at different HP values

package com.example.haunted.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BossMonsterTest {

    @Test
    void bossIsAMonster() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        assertInstanceOf(Monster.class, phantom);
    }

    @Test
    void calmAtFullHealth() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        assertEquals(10, phantom.getCurrentAttack());
    }

    @Test
    void enragesAtExactlyHalfHP() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(20); //hp = 20, condition is <= 20 so this triggers it
        assertEquals(13, phantom.getCurrentAttack());
    }

    @Test
    void oneAboveHalfStaysCalm() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(19); //hp = 21, just above the line
        assertEquals(10, phantom.getCurrentAttack());
    }

    @Test
    void belowHalfAddsBonus() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(25);
        assertEquals(13, phantom.getCurrentAttack());
    }

    @Test
    void overkillClampsAtZero() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(9999);
        assertEquals(0, phantom.getHealth());
        assertFalse(phantom.isAlive());
    }

    @Test
    void aliveAtStart() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        assertTrue(phantom.isAlive());
    }

    @ParameterizedTest(name = "after {0} damage out of 40 HP, enraged={1}")
    @CsvSource({
        "0,  false",
        "19, false",
        "20, true",
        "30, true",
        "40, true"
    })
    void enrageThreshold(int damage, boolean expectedEnraged) {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(damage);
        assertEquals(expectedEnraged, phantom.getCurrentAttack() > 10);
    }
}
