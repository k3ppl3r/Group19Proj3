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

//makes sure the dungeon is actually built right before we even start playing.
//if any of these fail, nothing else matters.
public class GameSetupTest {

    private GameEngine game;

    @BeforeEach
    void buildTheDungeon() {
        game = DungeonFactory.createGame();
    }

    @Test
    void theDungeonActuallyBuildsWithoutCrashing() {
        assertNotNull(game, "DungeonFactory should give us something to work with");
    }

    @Test
    void youStartInTheStairwellLikeEveryHorrorMovieEver() {
        assertEquals("stairwell", game.getCurrentRoom().getId());
    }

    @Test
    void theStairwellIsEssentiallyADeadEndWithOneExitEast() {
        assertNotNull(game.getCurrentRoom().getExit(Direction.EAST),   "east exit should exist");
        assertNull(game.getCurrentRoom().getExit(Direction.WEST),  "no west exit");
        assertNull(game.getCurrentRoom().getExit(Direction.NORTH), "no north exit");
        assertNull(game.getCurrentRoom().getExit(Direction.SOUTH), "no south exit");
    }

    @Test
    void someoneLedACoffeePotionInTheLectureHallForUs() {
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().findItem("Coffee Potion").isPresent(),
                "Coffee Potion should be sitting on a desk in the lecture hall");
    }

    @Test
    void theSleepDeprivedTAIsAlreadyLurkingInTheLectureHall() {
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().findMonster("Sleep-Deprived TA").isPresent(),
                "The TA never leaves, apparently");
    }

    @Test
    void theQuestHasNotEvenStartedYet() {
        assertEquals(QuestStatus.NOT_STARTED, game.getQuest().getStatus(),
                "Quest should be dormant until we do something worth noting");
    }

    @Test
    void theExamArchiveIsPadlockedAndWeNeedAKey() {
        game.move(Direction.EAST); // into lectureHall
        assertTrue(game.getCurrentRoom().getExit(Direction.NORTH).isLocked(),
                "Exam Archive door should be locked at game start");
    }

    @Test
    void theFinalChamberIsAlsoLockedBecauseOfCourseItIs() {
        //long walk: stairwell -> lectureHall -> labStorage -> serverCloset -> deanVault
        game.move(Direction.EAST);
        game.move(Direction.EAST);
        game.move(Direction.NORTH);
        game.move(Direction.EAST);
        assertTrue(game.getCurrentRoom().getExit(Direction.NORTH).isLocked(),
                "Final Chamber should definitely be locked");
    }

    @Test
    void theElevatorIsADeathtrapAndWeHaveBeenWarned() {
        game.move(Direction.EAST); //lectureHall
        var trap = game.getCurrentRoom().getExit(Direction.SOUTH).getTrap();
        assertNotNull(trap, "Broken elevator should have a nasty trap waiting");
        assertTrue(trap.isArmed(), "Trap should be live and ready");
    }

    @Test
    void theArchiveKeyIsHiddenInLabStorage() {
        game.move(Direction.EAST); //lectureHall
        game.move(Direction.EAST); //labStorage
        assertTrue(game.getCurrentRoom().findItem("Archive Key").isPresent(),
                "Archive Key should be collecting dust on a lab shelf");
    }

    @Test
    void theVaultKeyIsDeepInTheDeanVault() {
        game.move(Direction.EAST);  //lectureHall
        game.move(Direction.EAST);  //labStorage
        game.move(Direction.NORTH); //serverCloset
        game.move(Direction.EAST);  //deanVault
        assertTrue(game.getCurrentRoom().findItem("Vault Key").isPresent());
    }

    @Test
    void theStudentIsAliveAndStandingAtGameStart() {
        assertTrue(game.getPlayer().isAlive());
        assertFalse(game.isGameOver(), "Game should not be over before it even starts");
    }

    @Test
    void nobodyHasWonAnythingYet() {
        assertFalse(game.isGameWon());
    }
}
