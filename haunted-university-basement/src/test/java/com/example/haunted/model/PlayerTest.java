//Brady

package com.example.haunted.model;

import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void playerShouldLoseHealthWhenHitByAMonster() {
        Player alex = new Player("Alex", 100, 10, 5, new Inventory(10));
        //Alex walks into a room and gets smacked for 30 damage
        alex.takeDamage(30);
        assertEquals(70, alex.getHealth(), "Alex should have 70 HP left after taking 30 damage");
    }
}
