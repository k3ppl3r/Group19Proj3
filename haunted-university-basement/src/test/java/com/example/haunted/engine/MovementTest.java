//Brady
//Prompt:
//Write humanized JUnit 5 tests for MovementEngine via GameEngine covering: successful east move updates
//current room, parameterized test for all blocked directions from stairwell, locked door blocks movement,
//trap triggers and deals damage when entering broken elevator, trap disarms after one trigger so second
//entry is safe. Also test TrapResolver directly for: null trap does nothing, disarmed trap does nothing,
//and a non-one-time trap stays armed after firing. Use "Alex" as the player name and keep test names casual.

package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Room;
import com.example.haunted.model.Trap;
import com.example.haunted.model.TrapType;
import com.example.haunted.rules.TrapResolver;

public class MovementTest {

    private GameEngine game;

    @BeforeEach
    void newGame() {
        game = DungeonFactory.createGame();
    }

    //walking

    @Test
    void headingEastFromTheStairwellTakesYouToTheLectureHall() {
        MoveResult result = game.move(Direction.EAST);
        assertTrue(result.isSuccess());
        assertEquals("lectureHall", game.getCurrentRoom().getId());
    }

    @Test
    void yourRoomActuallyChangesAfterEachStep() {
        assertEquals("stairwell", game.getCurrentRoom().getId());
        game.move(Direction.EAST);
        assertEquals("lectureHall", game.getCurrentRoom().getId());
        game.move(Direction.EAST);
        assertEquals("labStorage", game.getCurrentRoom().getId());
    }

    //walking into wall

    @ParameterizedTest(name = "going {0} from the stairwell just bounces you off the wall")
    @EnumSource(value = Direction.class, names = {"WEST", "NORTH", "SOUTH"})
    void walkingIntoAWallKeepsYouExactlyWhereYouAre(Direction direction) {
        MoveResult result = game.move(direction);
        assertFalse(result.isSuccess());
        assertEquals("stairwell", game.getCurrentRoom().getId(),
                "Should still be stuck in the stairwell");
    }

    @Test
    void tryingToMarchThroughALockedDoorGoesNowhere() {
        game.move(Direction.EAST); // lectureHall
        MoveResult result = game.move(Direction.NORTH); // exam archive is locked
        assertFalse(result.isSuccess());
        assertEquals("lectureHall", game.getCurrentRoom().getId(),
                "Locked door should not let you through, surprisingly");
    }

    //traps

    @Test
    void stumblingIntoTheBrokenElevatorHurts() {
        game.move(Direction.EAST); // lectureHall
        int healthBefore = game.getPlayer().getHealth();

        MoveResult result = game.move(Direction.SOUTH); // broken elevator, trap is live
        assertTrue(result.isSuccess(), "You can enter, you just regret it");
        assertTrue(result.isTrapTriggered(), "Trap should go off");
        assertTrue(result.getTrapDamage() > 0, "And it should actually hurt");
        assertTrue(game.getPlayer().getHealth() < healthBefore,
                "Player should be worse off than before");
    }

    @Test
    void theTrapOnlyGetsYouOnce_afterThatYoureFine() {
        game.move(Direction.EAST);  // lectureHall
        game.move(Direction.SOUTH); // brokenElevator — ouch
        game.move(Direction.NORTH); // back to lectureHall

        int healthAfterTrap = game.getPlayer().getHealth();
        MoveResult secondVisit = game.move(Direction.SOUTH); // should be safe now

        assertTrue(secondVisit.isSuccess());
        assertFalse(secondVisit.isTrapTriggered(), "Trap should be spent after the first trigger");
        assertEquals(healthAfterTrap, game.getPlayer().getHealth(),
                "No damage the second time around");
    }

    //trap solver edge cases

    @Test
    void nothingBadHappensIfThereWasNoTrapToBeginWith() {
        TrapResolver resolver = new TrapResolver();
        Player student = new Player("Alex", 100, 10, 5, new Inventory(10));
        Room safeRoom = new Room("safe", "Safe Room", "Nothing here.");
        student.setCurrentRoom(safeRoom);

        InteractionResult result = resolver.resolveTrap(student, null);
        assertFalse(result.isSuccess());
        assertEquals(100, student.getHealth(), "Alex should be perfectly fine");
    }

    @Test
    void walkingPastAnAlreadyDisarmedTrapIsCompletelyFine() {
        TrapResolver resolver = new TrapResolver();
        Player student = new Player("Alex", 100, 10, 5, new Inventory(10));
        Room room = new Room("r", "Room", "Desc.");
        student.setCurrentRoom(room);

        Trap disarmedTrap = new Trap("Dud Trap", TrapType.STEAM, 10, false, true);
        InteractionResult result = resolver.resolveTrap(student, disarmedTrap);
        assertFalse(result.isSuccess(), "Disarmed trap should do nothing");
        assertEquals(100, student.getHealth(), "Alex should be unscathed");
    }

    @Test
    void aSteamVentKeepsGoingOffEveryTimeYouWalkThrough() {
        TrapResolver resolver = new TrapResolver();
        Player student = new Player("Alex", 100, 10, 5, new Inventory(10));
        Room room = new Room("r", "Room", "Desc.");
        student.setCurrentRoom(room);

        // neTimeTrigger = false means this trap never disarms — you hurt every time
        Trap steamVent = new Trap("Steam Vent", TrapType.STEAM, 5, true, false);
        resolver.resolveTrap(student, steamVent);

        assertTrue(steamVent.isArmed(), "Steam vent should still be armed for the next poor soul");
        assertEquals(95, student.getHealth(), "Alex took 5 damage from the steam vent");
    }
}
