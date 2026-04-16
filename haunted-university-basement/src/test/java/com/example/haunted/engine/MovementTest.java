//Brady
//Prompt:
//tests for movement. make sure walking east from stairwell actually moves you, that walking
//into walls in the other 3 directions fails and keeps you in place (parameterized), that a
//locked door blocks entry, and that the broken elevator trap fires and hurts you but only once.
//also test TrapResolver directly for null trap, a disarmed trap, and a trap that doesn't
//disarm after firing. use Alex as the player name

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

    @Test
    void moveEastFromStairwell() {
        MoveResult res = game.move(Direction.EAST);
        assertTrue(res.isSuccess());
        assertEquals("lectureHall", game.getCurrentRoom().getId());
    }

    @Test
    void roomUpdatesOnMove() {
        assertEquals("stairwell", game.getCurrentRoom().getId());
        game.move(Direction.EAST);
        assertEquals("lectureHall", game.getCurrentRoom().getId());
        game.move(Direction.EAST);
        assertEquals("labStorage", game.getCurrentRoom().getId());
    }

    @ParameterizedTest(name = "going {0} from the stairwell just bounces you off the wall")
    @EnumSource(value = Direction.class, names = {"WEST", "NORTH", "SOUTH"})
    void movingIntoWallFails(Direction direction) {
        MoveResult res = game.move(direction);
        assertFalse(res.isSuccess());
        assertEquals("stairwell", game.getCurrentRoom().getId());
    }

    @Test
    void lockedDoorBlocksMovement() {
        game.move(Direction.EAST); //lectureHall
        MoveResult res = game.move(Direction.NORTH); //exam archive is locked
        assertFalse(res.isSuccess());
        assertEquals("lectureHall", game.getCurrentRoom().getId());
    }

    @Test
    void stumblingIntoTheBrokenElevatorHurts() {
        game.move(Direction.EAST);
        int hp = game.getPlayer().getHealth();

        MoveResult res = game.move(Direction.SOUTH); //trap is live in here
        assertTrue(res.isSuccess(), "You can enter, you just regret it");
        assertTrue(res.isTrapTriggered());
        assertTrue(res.getTrapDamage() > 0);
        assertTrue(game.getPlayer().getHealth() < hp);
    }

    @Test
    void theTrapOnlyGetsYouOnce_afterThatYoureFine() {
        game.move(Direction.EAST);
        game.move(Direction.SOUTH); //ouch
        game.move(Direction.NORTH);

        int hp = game.getPlayer().getHealth();
        MoveResult second = game.move(Direction.SOUTH); //should be safe now

        assertTrue(second.isSuccess());
        assertFalse(second.isTrapTriggered(), "Trap should be spent after the first trigger");
        assertEquals(hp, game.getPlayer().getHealth());
    }

    //testing TrapResolver directly to hit the branches the game engine skips over

    @Test
    void nullTrapDoesNothing() {
        TrapResolver resolver = new TrapResolver();
        Player student = new Player("Alex", 100, 10, 5, new Inventory(10));
        Room safeRoom = new Room("safe", "Safe Room", "Nothing here.");
        student.setCurrentRoom(safeRoom);

        InteractionResult res = resolver.resolveTrap(student, null);
        assertFalse(res.isSuccess());
        assertEquals(100, student.getHealth());
    }

    @Test
    void disarmedTrapDoesNothing() {
        TrapResolver resolver = new TrapResolver();
        Player student = new Player("Alex", 100, 10, 5, new Inventory(10));
        Room room = new Room("r", "Room", "Desc.");
        student.setCurrentRoom(room);

        Trap dud = new Trap("Dud Trap", TrapType.STEAM, 10, false, true);
        assertFalse(resolver.resolveTrap(student, dud).isSuccess());
        assertEquals(100, student.getHealth());
    }

    @Test
    void aSteamVentKeepsGoingOffEveryTimeYouWalkThrough() {
        TrapResolver resolver = new TrapResolver();
        Player student = new Player("Alex", 100, 10, 5, new Inventory(10));
        Room room = new Room("r", "Room", "Desc.");
        student.setCurrentRoom(room);

        //oneTimeTrigger = false means it never disarms
        Trap steamVent = new Trap("Steam Vent", TrapType.STEAM, 5, true, false);
        resolver.resolveTrap(student, steamVent);

        assertTrue(steamVent.isArmed(), "Steam vent should still be armed for the next poor soul");
        assertEquals(95, student.getHealth());
    }
}
