//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    private Room makeRoom() {
        return new Room("r1", "Main Hall", "A dusty hall.");
    }

    //constructor

    @Test
    void idIsSetCorrectly() {
        assertEquals("r1", makeRoom().getId());
    }

    @Test
    void nameIsSetCorrectly() {
        assertEquals("Main Hall", makeRoom().getName());
    }

    @Test
    void descriptionIsSetCorrectly() {
        assertEquals("A dusty hall.", makeRoom().getDescription());
    }

    @Test
    void nullIdThrows() {
        assertThrows(NullPointerException.class, () ->
            new Room(null, "Name", "Desc")
        );
    }

    @Test
    void nullNameThrows() {
        assertThrows(NullPointerException.class, () ->
            new Room("id", null, "Desc")
        );
    }

    @Test
    void nullDescriptionThrows() {
        assertThrows(NullPointerException.class, () ->
            new Room("id", "Name", null)
        );
    }

    //exits

    @Test
    void connectingRoomsAllowsMovement() {
        Room room = makeRoom();
        Room north = new Room("north", "North Room", "Cold.");
        room.connect(Direction.NORTH, north);
        assertSame(north, room.getExit(Direction.NORTH));
    }

    @Test
    void unconnectedDirectionReturnsNull() {
        assertNull(makeRoom().getExit(Direction.SOUTH));
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void canConnectInEveryDirection(Direction dir) {
        Room room = makeRoom();
        Room target = new Room("t", "Target", "T");
        room.connect(dir, target);
        assertSame(target, room.getExit(dir));
    }

    @Test
    void getExitsReflectsAllConnections() {
        Room room = makeRoom();
        room.connect(Direction.EAST, new Room("e", "East", "E"));
        room.connect(Direction.SOUTH, new Room("s", "South", "S"));
        assertEquals(2, room.getExits().size());
    }

    @Test
    void exitsMapIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () ->
            makeRoom().getExits().put(Direction.NORTH, new Room("x", "X", "X"))
        );
    }

    //items

    @Test
    void addedItemShowsUpInRoom() {
        Room room = makeRoom();
        Potion p = new Potion("Coffee Potion", "Warm", 15);
        room.addItem(p);
        assertEquals(1, room.getItems().size());
        assertSame(p, room.getItems().get(0));
    }

    @Test
    void itemsListIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () ->
            makeRoom().getItems().add(new Potion("x", "x", 1))
        );
    }

    @Test
    void removingItemByNameReturnsIt() {
        Room room = makeRoom();
        Potion p = new Potion("Coffee Potion", "Warm", 15);
        room.addItem(p);
        assertSame(p, room.removeItemByName("Coffee Potion"));
    }

    @Test
    void removingItemByNameActuallyRemovesIt() {
        Room room = makeRoom();
        room.addItem(new Potion("Coffee Potion", "Warm", 15));
        room.removeItemByName("Coffee Potion");
        assertEquals(0, room.getItems().size());
    }

    @Test
    void removeItemByNameIsCaseInsensitive() {
        Room room = makeRoom();
        room.addItem(new Potion("Coffee Potion", "Warm", 15));
        assertNotNull(room.removeItemByName("coffee potion"));
    }

    @Test
    void removingItemThatIsntThereReturnsNull() {
        assertNull(makeRoom().removeItemByName("Missing"));
    }

    @Test
    void findItemReturnsPresentForExistingItem() {
        Room room = makeRoom();
        room.addItem(new Key("Archive Key", "Opens archive"));
        assertTrue(room.findItem("Archive Key").isPresent());
    }

    @Test
    void findItemReturnsEmptyForMissingItem() {
        assertTrue(makeRoom().findItem("Nothing").isEmpty());
    }

    //monsters

    @Test
    void addedMonsterShowsUpInRoom() {
        Room room = makeRoom();
        Monster m = new Monster("Goblin", 10, 3, 1, List.of());
        room.addMonster(m);
        assertEquals(1, room.getMonsters().size());
        assertSame(m, room.getMonsters().get(0));
    }

    @Test
    void monstersListIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class, () ->
            makeRoom().getMonsters().add(new Monster("x", 5, 1, 0, List.of()))
        );
    }

    @Test
    void findMonsterReturnsPresentForExistingMonster() {
        Room room = makeRoom();
        room.addMonster(new Monster("Troll", 20, 5, 1, List.of()));
        assertTrue(room.findMonster("Troll").isPresent());
    }

    @Test
    void findMonsterIsCaseInsensitive() {
        Room room = makeRoom();
        room.addMonster(new Monster("Troll", 20, 5, 1, List.of()));
        assertTrue(room.findMonster("troll").isPresent());
    }

    @Test
    void findMonsterReturnsEmptyWhenNotFound() {
        assertTrue(makeRoom().findMonster("Dragon").isEmpty());
    }

    @Test
    void roomWithNoMonstersHasNoLivingMonsters() {
        assertFalse(makeRoom().hasLivingMonsters());
    }

    @Test
    void roomWithAliveMonsterHasLivingMonsters() {
        Room room = makeRoom();
        room.addMonster(new Monster("Zombie", 15, 4, 1, List.of()));
        assertTrue(room.hasLivingMonsters());
    }

    @Test
    void roomWithOnlyDeadMonstersHasNoLivingMonsters() {
        Room room = makeRoom();
        Monster m = new Monster("Zombie", 15, 4, 1, List.of());
        m.takeDamage(15);
        room.addMonster(m);
        assertFalse(room.hasLivingMonsters());
    }

    @Test
    void roomWithMixedMonstersStillHasLivingMonsters() {
        Room room = makeRoom();
        Monster dead = new Monster("Ghost", 5, 2, 0, List.of());
        dead.takeDamage(5);
        room.addMonster(dead);
        room.addMonster(new Monster("Zombie", 10, 3, 0, List.of()));
        assertTrue(room.hasLivingMonsters());
    }

    //locking and unlocking

    @Test
    void roomIsNotLockedByDefault() {
        assertFalse(makeRoom().isLocked());
    }

    @Test
    void settingLockedToTrueLocksRoom() {
        Room room = makeRoom();
        room.setLocked(true, "Master Key");
        assertTrue(room.isLocked());
    }

    @Test
    void requiredKeyNameIsStoredCorrectly() {
        Room room = makeRoom();
        room.setLocked(true, "Master Key");
        assertEquals("Master Key", room.getRequiredKeyName());
    }

    @Test
    void unlockingAlreadyUnlockedRoomReturnsTrue() {
        assertTrue(makeRoom().unlock("Anything"));
    }

    @Test
    void correctKeyUnlocksRoom() {
        Room room = makeRoom();
        room.setLocked(true, "Golden Key");
        assertTrue(room.unlock("Golden Key"));
        assertFalse(room.isLocked());
    }

    @Test
    void wrongKeyDoesNotUnlockRoom() {
        Room room = makeRoom();
        room.setLocked(true, "Golden Key");
        assertFalse(room.unlock("Iron Key"));
        assertTrue(room.isLocked());
    }

    @Test
    void unlockKeyCheckIsCaseInsensitive() {
        Room room = makeRoom();
        room.setLocked(true, "Archive Key");
        assertTrue(room.unlock("archive key"));
    }

    @Test
    void unlockWithNullRequiredKeyReturnsFalse() {
        Room room = makeRoom();
        room.setLocked(true, null);
        assertFalse(room.unlock("Any Key"));
    }

    //trap

    @Test
    void noTrapByDefault() {
        assertNull(makeRoom().getTrap());
    }

    @Test
    void settingTrapStoresIt() {
        Room room = makeRoom();
        Trap trap = new Trap("Loose Wires", TrapType.ELECTRIC, 8, true, true);
        room.setTrap(trap);
        assertSame(trap, room.getTrap());
    }
}
