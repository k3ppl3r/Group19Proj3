//Brady
//Prompt:
//tests for the Usable interface. since Potion is the concrete implementation, use it
//to verify that calling use() on a damaged player heals them, that it doesn't overshoot
//max hp, that a potion with 0 healing does nothing, and that Potion is recognized as
//Usable. parameterized test for a few different healing amounts

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UsableTest {

    private Player damagedPlayer(int dmg) {
        Player p = new Player("Alex", 100, 10, 5, new Inventory(5));
        p.takeDamage(dmg);
        return p;
    }

    @Test
    void potionIsUsable() {
        Potion p = new Potion("Coffee Potion", "Wakes you up.", 20);
        assertTrue(p instanceof Usable);
    }

    @Test
    void useHealsPlayer() {
        Player p = damagedPlayer(30);
        new Potion("Coffee Potion", "Wakes you up.", 20).use(p);
        assertEquals(90, p.getHealth()); //70 + 20 = 90
    }

    @Test
    void cantHealPastMax() {
        Player p = damagedPlayer(10);
        new Potion("Mega Potion", "Too good.", 999).use(p);
        assertEquals(100, p.getHealth()); //caps at max
    }

    @Test
    void zeroPotionDoesNothing() {
        Player p = damagedPlayer(20);
        new Potion("Dud Potion", "Tastes like nothing.", 0).use(p);
        assertEquals(80, p.getHealth());
    }

    @ParameterizedTest(name = "potion healing {0} on player at 50 HP leaves {1} HP")
    @CsvSource({
        "10, 60",
        "50, 100",
        "99, 100", //overshoot caps at 100
        "1,  51"
    })
    void healingAmounts(int heal, int expected) {
        Player p = damagedPlayer(50);
        new Potion("Test Potion", "desc", heal).use(p);
        assertEquals(expected, p.getHealth());
    }
}
