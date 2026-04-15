//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class TrapTest {

    private Trap makeElectricTrap() {
        return new Trap("Loose Wires Trap", TrapType.ELECTRIC, 8, true, true);
    }

    @Test
    void nameIsSetCorrectly() {
        assertEquals("Loose Wires Trap", makeElectricTrap().getName());
    }

    @Test
    void typeIsSetCorrectly() {
        assertEquals(TrapType.ELECTRIC, makeElectricTrap().getType());
    }

    @Test
    void damageIsSetCorrectly() {
        assertEquals(8, makeElectricTrap().getDamage());
    }

    @Test
    void armedTrapReportsArmed() {
        assertTrue(makeElectricTrap().isArmed());
    }

    @Test
    void disarmedTrapReportsNotArmed() {
        Trap t = new Trap("Steam Vent", TrapType.STEAM, 5, false, false);
        assertFalse(t.isArmed());
    }

    @Test
    void oneTimeTriggerFlagIsTrue() {
        assertTrue(makeElectricTrap().isOneTimeTrigger());
    }

    @Test
    void repeatingTrapHasFalseOneTimeTrigger() {
        Trap t = new Trap("Steam Vent", TrapType.STEAM, 5, true, false);
        assertFalse(t.isOneTimeTrigger());
    }

    //disarming

    @Test
    void disarmingAnArmedTrapDisablesIt() {
        Trap trap = makeElectricTrap();
        trap.disarm();
        assertFalse(trap.isArmed());
    }

    @Test
    void disarmingAlreadyDisarmedTrapKeepsItDisarmed() {
        Trap trap = new Trap("Disarmed", TrapType.STEAM, 5, false, false);
        trap.disarm();
        assertFalse(trap.isArmed());
    }

    //steam trap type

    @Test
    void steamTrapTypeIsCorrect() {
        Trap t = new Trap("Steam Vent", TrapType.STEAM, 10, true, false);
        assertEquals(TrapType.STEAM, t.getType());
    }

    //null checks

    @Test
    void nullNameThrows() {
        assertThrows(NullPointerException.class, () ->
            new Trap(null, TrapType.ELECTRIC, 5, true, false)
        );
    }

    @Test
    void nullTypeThrows() {
        assertThrows(NullPointerException.class, () ->
            new Trap("Trap", null, 5, true, false)
        );
    }

    //both trap types covered

    @ParameterizedTest
    @EnumSource(TrapType.class)
    void canCreateTrapWithAnyType(TrapType type) {
        Trap t = new Trap("T", type, 5, true, false);
        assertEquals(type, t.getType());
    }
}
