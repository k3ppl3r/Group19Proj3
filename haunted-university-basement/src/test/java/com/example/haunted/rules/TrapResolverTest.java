//Brady
//Prompt:
//tests for TrapResolver. null trap returns failure and does no damage, a disarmed trap
//also does nothing, an armed one-time trap fires and then disarms itself, and a repeating
//trap fires but stays armed for next time. parameterized test for different damage values
//on a repeating trap

package com.example.haunted.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;

public class TrapResolverTest {

    private final TrapResolver resolver = new TrapResolver();

    private Player freshPlayer() {
        Player p = new Player("Alex", 100, 10, 5, new Inventory(5));
        p.setCurrentRoom(new Room("r", "Room"));
        return p;
    }

    @Test
    void nullTrap() {
        Player p = freshPlayer();
        InteractionResult res = resolver.resolveTrap(p, null);
        assertFalse(res.isSuccess());
        assertEquals(100, p.getHealth());
    }

    @Test
    void disarmedTrapDoesNothing() {
        Player p = freshPlayer();
        Trap dud = new Trap("Dud", TrapType.STEAM, 10, false, true);
        assertFalse(resolver.resolveTrap(p, dud).isSuccess());
        assertEquals(100, p.getHealth());
    }

    @Test
    void oneTimeTrapFiresAndDisarms() {
        Player p = freshPlayer();
        Trap t = new Trap("Loose Wires", TrapType.ELECTRIC, 15, true, true);
        InteractionResult res = resolver.resolveTrap(p, t);
        assertTrue(res.isSuccess());
        assertEquals(85, p.getHealth());
        assertFalse(t.isArmed()); //should be spent now
    }

    @Test
    void repeatingTrapStaysArmed() {
        Player p = freshPlayer();
        Trap vent = new Trap("Steam Vent", TrapType.STEAM, 5, true, false);
        resolver.resolveTrap(p, vent);
        assertTrue(vent.isArmed()); //still dangerous for next time
        assertEquals(95, p.getHealth());
    }

    @ParameterizedTest(name = "repeating trap dealing {0} damage leaves player at {0} less HP")
    @ValueSource(ints = {5, 10, 20, 50})
    void trapDamageAmounts(int dmg) {
        Player p = freshPlayer();
        Trap t = new Trap("Zapper", TrapType.ELECTRIC, dmg, true, false);
        resolver.resolveTrap(p, t);
        assertEquals(100 - dmg, p.getHealth());
    }
}
