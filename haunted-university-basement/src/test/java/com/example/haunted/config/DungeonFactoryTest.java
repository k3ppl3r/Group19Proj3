//Brady
//Prompt:
//need tests that call DungeonFactory.createGame() to make sure the world gets built right.
//check the player starts in stairwell, stairwell only has east exit, lecture hall has coffee
//potion and the TA, exam archive and final chamber are locked, broken elevator has a trap,
//archive key is in lab storage, vault key in dean vault, quest is NOT_STARTED, player alive.
//use @BeforeEach

package com.example.haunted.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.haunted.engine.GameEngine;
import com.example.haunted.model.Direction;
import com.example.haunted.model.QuestStatus;

public class DungeonFactoryTest {

    private GameEngine game;

    @BeforeEach
    void buildTheDungeon() {
        game = DungeonFactory.createGame();
    }

    @Test
    void dungeonLoads() {
        assertNotNull(game);
    }

    @Test
    void youStartInTheStairwellLikeEveryHorrorMovieEver() {
        assertEquals("stairwell", game.getCurrentRoom().getId());
    }

    @Test
    void stairwellOnlyHasEastExit() {
        assertNotNull(game.getCurrentRoom().getExit(Direction.EAST));
        assertNull(game.getCurrentRoom().getExit(Direction.WEST));
        assertNull(game.getCurrentRoom().getExit(Direction.NORTH));
        assertNull(game.getCurrentRoom().getExit(Direction.SOUTH));
    }

    @Test
    void someoneLedACoffeePotionInTheLectureHallForUs() {
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().findItem("Coffee Potion").isPresent());
    }

    @Test
    void lectureHallHasTA() {
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().findMonster("Sleep-Deprived TA").isPresent());
    }

    @Test
    void questNotStartedAtBeginning() {
        assertEquals(QuestStatus.NOT_STARTED, game.getQuest().getStatus());
    }

    @Test
    void theExamArchiveIsPadlockedAndWeNeedAKey() {
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().getExit(Direction.NORTH).isLocked());
    }

    @Test
    void theFinalChamberIsAlsoLockedBecauseOfCourseItIs() {
        //had to look up the path for this one
        game.move(Direction.EAST);
        game.move(Direction.EAST);
        game.move(Direction.NORTH);
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().getExit(Direction.NORTH).isLocked());
    }

    @Test
    void theElevatorIsADeathtrapAndWeHaveBeenWarned() {
        game.move(Direction.EAST);
        var trap = game.getCurrentRoom().getExit(Direction.SOUTH).getTrap();
        assertNotNull(trap);
        assertTrue(trap.isArmed());
    }

    @Test
    void archiveKeyInLabStorage() {
        game.move(Direction.EAST);
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().findItem("Archive Key").isPresent());
    }

    @Test
    void theVaultKeyIsDeepInTheDeanVault() {
        game.move(Direction.EAST);
        game.move(Direction.EAST);
        game.move(Direction.NORTH);
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().findItem("Vault Key").isPresent());
    }

    @Test
    void playerAliveAtStart() {
        assertTrue(game.getPlayer().isAlive());
        assertFalse(game.isGameOver());
    }

    @Test
    void nobodyHasWonAnythingYet() {
        assertFalse(game.isGameWon());
    }
}
