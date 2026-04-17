//Brady
//Prompt:
//tests for the Room class. check id and name are stored right, that connecting rooms
//sets up exits correctly, that getExit returns null for a direction with no exit, that
//adding and finding items works, that findItem is case insensitive, removing an item
//takes it out, adding and finding monsters works, hasLivingMonsters flips once the
//monster dies, locking and unlocking with the right key, unlocking with the wrong key
//fails, and that setting a trap stores it. parameterized test for exits in all 4 directions

package com.example.haunted.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class RoomTest {

    private Room room;

    @BeforeEach
    void setup() {
        room = new Room("lab", "Lab Room");
    }

    @Test
    void idAndName() {
        assertEquals("lab", room.getId());
        assertEquals("Lab Room", room.getName());
    }

    @Test
    void connectSetsExit() {
        Room next = new Room("next", "Next Room");
        room.connect(Direction.EAST, next);
        assertEquals(next, room.getExit(Direction.EAST));
    }

    @Test
    void noExitReturnsNull() {
        assertNull(room.getExit(Direction.WEST));
    }

    @ParameterizedTest(name = "can connect a room to the {0} exit")
    @EnumSource(Direction.class)
    void connectAllDirections(Direction dir) {
        Room neighbor = new Room("neighbor", "Neighbor");
        room.connect(dir, neighbor);
        assertEquals(neighbor, room.getExit(dir));
    }

    @Test
    void addItem() {
        room.addItem(new Key("Archive Key", "Opens the archive."));
        assertTrue(room.findItem("Archive Key").isPresent());
    }

    @Test
    void findItemCaseInsensitive() {
        room.addItem(new Potion("Coffee Potion", "Warm cup.", 15));
        assertTrue(room.findItem("coffee potion").isPresent());
    }

    @Test
    void findMissing() {
        assertFalse(room.findItem("Invisible Homework").isPresent());
    }

    @Test
    void removeItem() {
        room.addItem(new Key("Vault Key", "Heavy key."));
        Item removed = room.removeItemByName("Vault Key");
        assertNotNull(removed);
        assertFalse(room.findItem("Vault Key").isPresent());
    }

    @Test
    void removeMissingReturnsNull() {
        assertNull(room.removeItemByName("Excalibur"));
    }

    @Test
    void addMonster() {
        room.addMonster(new Monster("Ghost", 20, 5, 2, List.of()));
        assertTrue(room.findMonster("Ghost").isPresent());
    }

    @Test
    void monsterDiesAndRoomKnowsIt() {
        Monster m = new Monster("Ghost", 10, 5, 2, List.of());
        room.addMonster(m);
        assertTrue(room.hasLivingMonsters());
        m.takeDamage(9999);
        assertFalse(room.hasLivingMonsters()); //room should notice once hp hits 0
    }

    @Test
    void unlockedByDefault() {
        assertFalse(room.isLocked());
    }

    @Test
    void setLocked() {
        room.setLocked(true);
        assertTrue(room.isLocked());
    }

    @Test
    void rightKeyUnlocks() {
        room.setLocked(true);
        room.setRequiredKeyName("Archive Key");
        assertTrue(room.unlock("Archive Key"));
        assertFalse(room.isLocked());
    }

    @Test
    void wrongKeyDoesNothing() {
        room.setLocked(true);
        room.setRequiredKeyName("Archive Key");
        assertFalse(room.unlock("Wrong Key"));
        assertTrue(room.isLocked());
    }

    @Test
    void unlockAlreadyOpenRoom() {
        assertTrue(room.unlock("anything")); //already open, should just return true
    }

    @Test
    void trap() {
        Trap t = new Trap("Steam Vent", TrapType.STEAM, 5, true, false);
        room.setTrap(t);
        assertEquals(t, room.getTrap());
    }

    @Test
    void getItemsUnmodifiable() {
        room.addItem(new Key("Some Key", "desc"));
        assertThrows(UnsupportedOperationException.class,
                () -> room.getItems().add(new Key("Sneaky Key", "nope")));
    }
}
