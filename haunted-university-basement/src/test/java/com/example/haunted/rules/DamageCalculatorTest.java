//Brady
//Prompt:
//tests for DamageCalculator. check that calculatePlayerDamage works normally, that
//damage floors at 1 when attack is less than or equal to defense, and that
//calculateMonsterDamage uses the boss enrage bonus when the boss is below half HP.
//also check the regular monster damage path and the floor of 1. parameterized test
//for player damage at a few attack/defense combos

package com.example.haunted.rules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;

public class DamageCalculatorTest {

    private final DamageCalculator calc = new DamageCalculator();

    @ParameterizedTest(name = "atk={0} def={1} should deal {2}")
    @CsvSource({
        "10, 5, 5",
        "5,  5, 1",  //tied stats still do 1
        "1, 10, 1",  //completely outclassed, still 1
        "7,  1, 6"   //actual player vs TA numbers
    })
    void playerDamageFloor(int atk, int def, int expected) {
        Player p = new Player("Test", 100, atk, 0, new Inventory(5));
        Monster m = new Monster("Tank", 100, 5, def, List.of());
        assertEquals(expected, calc.calculatePlayerDamage(p, m));
    }

    @Test
    void normalMonsterHitsBack() {
        Player p = new Player("Test", 100, 5, 3, new Inventory(5));
        Monster m = new Monster("Ghost", 20, 8, 2, List.of());
        assertEquals(5, calc.calculateMonsterDamage(m, p)); //8 - 3 = 5
    }

    @Test
    void monsterDamageFloor() {
        Player p = new Player("Tank", 100, 5, 20, new Inventory(5));
        Monster m = new Monster("Weakling", 10, 5, 2, List.of());
        assertEquals(1, calc.calculateMonsterDamage(m, p));
    }

    @Test
    void bossUsesEnragedAttackWhenBelowHalfHP() {
        Player p = new Player("Test", 100, 5, 0, new Inventory(5));
        BossMonster boss = new BossMonster("Phantom", 40, 10, 4, List.of(), 5);
        boss.takeDamage(25); //push below half, enrage kicks in — attack becomes 15
        assertEquals(15, calc.calculateMonsterDamage(boss, p));
    }

    @Test
    void bossStaysCalm_atFullHP() {
        Player p = new Player("Test", 100, 5, 0, new Inventory(5));
        BossMonster boss = new BossMonster("Phantom", 40, 10, 4, List.of(), 5);
        assertEquals(10, calc.calculateMonsterDamage(boss, p)); //no bonus yet
    }
}
