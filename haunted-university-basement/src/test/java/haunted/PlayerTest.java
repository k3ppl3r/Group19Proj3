//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player makePlayer() {
        return new Player("Brady", 50, 10, 3, new Inventory(5));
    }

    @Test
    void playerNameIsSetCorrectly() {
        Player p = makePlayer();
        assertEquals("Brady", p.getName());
    }

    @Test
    void playerStartsAtMaxHealth() {
        Player p = makePlayer();
        assertEquals(50, p.getHealth());
        assertEquals(50, p.getMaxHealth());
    }

    @Test
    void baseAttackAndDefenseAreCorrect() {
        Player p = makePlayer();
        assertEquals(10, p.getBaseAttack());
        assertEquals(3, p.getBaseDefense());
    }

    @Test
    void inventoryIsSetOnConstruction() {
        Inventory inv = new Inventory(5);
        Player p = new Player("Test", 50, 10, 3, inv);
        assertSame(inv, p.getInventory());
    }

    @Test
    void nullNameThrowsException() {
        assertThrows(NullPointerException.class, () ->
            new Player(null, 50, 10, 3, new Inventory(5))
        );
    }

    @Test
    void nullInventoryThrowsException() {
        assertThrows(NullPointerException.class, () ->
            new Player("Brady", 50, 10, 3, null)
        );
    }

    //taking damage

    @Test
    void takingDamageReducesHealth() {
        Player p = makePlayer();
        p.takeDamage(10);
        assertEquals(40, p.getHealth());
    }

    @Test
    void takingMoreDamageThanHealthClampsToZero() {
        Player p = makePlayer();
        p.takeDamage(999);
        assertEquals(0, p.getHealth());
    }

    @Test
    void takingZeroDamageDoesNothing() {
        Player p = makePlayer();
        p.takeDamage(0);
        assertEquals(50, p.getHealth());
    }

    @Test
    void negativeDamageIsIgnored() {
        Player p = makePlayer();
        p.takeDamage(-5);
        assertEquals(50, p.getHealth());
    }

    @ParameterizedTest
    @CsvSource({"10, 40", "25, 25", "50, 0", "100, 0"})
    void takeDamageParameterized(int dmg, int expected) {
        Player p = makePlayer();
        p.takeDamage(dmg);
        assertEquals(expected, p.getHealth());
    }

    //healing

    @Test
    void healingIncreasesHealth() {
        Player p = makePlayer();
        p.takeDamage(20);
        p.heal(10);
        assertEquals(40, p.getHealth());
    }

    @Test
    void healingCannotExceedMaxHealth() {
        Player p = makePlayer();
        p.takeDamage(10);
        p.heal(999);
        assertEquals(50, p.getHealth());
    }

    @Test
    void healingByZeroDoesNothing() {
        Player p = makePlayer();
        p.takeDamage(20);
        p.heal(0);
        assertEquals(30, p.getHealth());
    }

    @Test
    void negativeHealDoesNothing() {
        Player p = makePlayer();
        p.takeDamage(20);
        p.heal(-10);
        assertEquals(30, p.getHealth());
    }

    //isAlive

    @Test
    void playerIsAliveWhenHealthIsAboveZero() {
        Player p = makePlayer();
        assertTrue(p.isAlive());
    }

    @Test
    void playerIsDeadWhenHealthHitsZero() {
        Player p = makePlayer();
        p.takeDamage(50);
        assertFalse(p.isAlive());
    }

    //attack and defense power

    @Test
    void attackPowerWithNoWeaponIsJustBaseAttack() {
        Player p = makePlayer();
        assertEquals(10, p.getAttackPower());
    }

    @Test
    void attackPowerAddsWeaponBonus() {
        Player p = makePlayer();
        p.equipWeapon(new Weapon("Stapler", "Heavy stapler", 4));
        assertEquals(14, p.getAttackPower());
    }

    @Test
    void defensePowerWithNoArmorIsJustBaseDefense() {
        Player p = makePlayer();
        assertEquals(3, p.getDefensePower());
    }

    @Test
    void defensePowerAddsArmorBonus() {
        Player p = makePlayer();
        p.equipArmor(new Armor("Calculator Shield", "Battered calc", 3));
        assertEquals(6, p.getDefensePower());
    }

    //equipping

    @Test
    void noWeaponEquippedByDefault() {
        Player p = makePlayer();
        assertNull(p.getEquippedWeapon());
    }

    @Test
    void noArmorEquippedByDefault() {
        Player p = makePlayer();
        assertNull(p.getEquippedArmor());
    }

    @Test
    void equipWeaponStoresWeapon() {
        Player p = makePlayer();
        Weapon w = new Weapon("Sword", "Sharp", 5);
        p.equipWeapon(w);
        assertSame(w, p.getEquippedWeapon());
    }

    @Test
    void equipArmorStoresArmor() {
        Player p = makePlayer();
        Armor a = new Armor("Plate", "Heavy", 4);
        p.equipArmor(a);
        assertSame(a, p.getEquippedArmor());
    }

    @Test
    void equipingNewWeaponReplacesOldOne() {
        Player p = makePlayer();
        Weapon first = new Weapon("Dagger", "Quick", 2);
        Weapon second = new Weapon("Sword", "Strong", 6);
        p.equipWeapon(first);
        p.equipWeapon(second);
        assertSame(second, p.getEquippedWeapon());
    }

    @Test
    void equipNullWeaponThrows() {
        assertThrows(NullPointerException.class, () -> makePlayer().equipWeapon(null));
    }

    @Test
    void equipNullArmorThrows() {
        assertThrows(NullPointerException.class, () -> makePlayer().equipArmor(null));
    }

    //current room

    @Test
    void setAndGetCurrentRoom() {
        Player p = makePlayer();
        Room room = new Room("hall", "Main Hall", "A dark hall.");
        p.setCurrentRoom(room);
        assertSame(room, p.getCurrentRoom());
    }

    @Test
    void setNullRoomThrows() {
        assertThrows(NullPointerException.class, () -> makePlayer().setCurrentRoom(null));
    }
}
