//Brady
//Prompt:
//Write humanized JUnit 5 tests for CombatEngine via GameEngine covering: attacking TA deals damage,
//TA counterattacks when not killed in one hit, killing TA drops loot, defeated monster stays dead,
//attacking a nonexistent monster fails, attacking an already-dead monster fails, dead player cannot attack.
//Also test BossMonster directly for enrage at half HP. Test a weak player getting counterattacked by the
//boss using a custom arena setup. Test full quest completion by building a custom arena where hero kills
//the Final Exam Phantom after picking up the Lost Gradebook. Parameterized test for DamageCalculator
//never going below 1 with boundary values. Use "Chad" for the underpowered student.

package com.example.haunted.engine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.events.CombatResult;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.Room;
import com.example.haunted.rules.DamageCalculator;
import com.example.haunted.rules.QuestTracker;
import com.example.haunted.rules.TrapResolver;

public class CombatTest {

    private GameEngine game;

    @BeforeEach
    void walkIntoTheLectureHallAndFaceTheTA() {
        game = DungeonFactory.createGame();
        game.move(Direction.EAST); //stairwell -> lectureHall, where the TA is waiting
    }

    //basic combat

    @Test
    void hittingTheTAActuallyDoesHurtIt() {
        int taHealthBefore = game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().getHealth();
        CombatResult result = game.attack("Sleep-Deprived TA");
        assertTrue(result.isSuccess());
        assertTrue(result.getDamageToMonster() > 0, "Attack should deal some damage");
        assertTrue(game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().getHealth() < taHealthBefore);
    }

    @Test
    void theTADoesNotJustStandThereAndTakeIt() {
        //TA has 18 HP — one hit does 6 damage, so it survives and swings back
        CombatResult result = game.attack("Sleep-Deprived TA");
        assertTrue(result.isSuccess());
        assertFalse(result.isMonsterDefeated(), "TA should still be standing after one hit");
        assertTrue(result.getDamageToPlayer() > 0, "TA should hit back");
        assertTrue(game.getPlayer().getHealth() < game.getPlayer().getMaxHealth(),
                "Player should have some bruises");
    }

    //defeat and loot

    @Test
    void whenYouFinallyKillTheTAItDropsItsCoffee() {
        //player deals max(1, 7-1) = 6 damage per hit; TA has 18 HP so 3 hits to finish it
        while (game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive()) {
            game.attack("Sleep-Deprived TA");
        }
        assertTrue(game.getCurrentRoom().findItem("Coffee Potion").isPresent(),
                "TA drops a Coffee Potion as loot — small consolation");
    }

    @Test
    void aDefeatedMonsterIsActuallyDead() {
        while (game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive()) {
            game.attack("Sleep-Deprived TA");
        }
        assertFalse(game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive());
    }

    //edge cases

    @Test
    void attackingAMonsterThatIsNotInTheRoomJustFails() {
        CombatResult result = game.attack("The Easter Bunny");
        assertFalse(result.isSuccess(), "Can't fight something that isn't there");
    }

    @Test
    void youCannotKickAMonsterThatIsAlreadyDead() {
        while (game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive()) {
            game.attack("Sleep-Deprived TA");
        }
        CombatResult result = game.attack("Sleep-Deprived TA");
        assertFalse(result.isSuccess(), "Beating a dead TA is not allowed");
    }

    @Test
    void aDeadPlayerCannotAttackAnything() {
        game.getPlayer().takeDamage(9999); //instant death
        CombatResult result = game.attack("Sleep-Deprived TA");
        assertFalse(result.isSuccess(), "Dead students do not fight back");
        assertTrue(game.isGameOver());
    }

    //boss monster

    @Test
    void theBossIsChillUntilYouGetItBelowHalfHealthThenItLosesIt() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        assertEquals(10, phantom.getCurrentAttack(), "Boss is calm at full HP");
        phantom.takeDamage(21); //push it below the halfway mark (40/2 = 20)
        assertEquals(13, phantom.getCurrentAttack(), "Boss gains +3 rage bonus when enraged");
    }

    @Test
    void anUnderpoweredStudentGetsCounterattackedByTheBoss() {
        //using a weak student so the boss survives long enough to hit back
        Player underpoweredStudent = new Player("Chad", 100, 5, 0, new Inventory(10));
        Room finalRoom = new Room("final", "Final Chamber", "You probably should have studied.");
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        finalRoom.addMonster(phantom);
        underpoweredStudent.setCurrentRoom(finalRoom);

        Quest quest = new Quest("Escape the Basement", "Good luck.");
        CombatEngine combatEngine = new CombatEngine(new DamageCalculator(), new QuestTracker());

        int healthBefore = underpoweredStudent.getHealth();
        CombatResult result = combatEngine.attack(underpoweredStudent, quest, phantom);

        assertTrue(result.isSuccess());
        assertFalse(result.isMonsterDefeated(), "Boss survives Chad's weak hit");
        assertTrue(underpoweredStudent.getHealth() < healthBefore, "Boss absolutely hits back");
    }

    //quest completion

    @Test
    void retrievingTheGradebookAndSlayingThePhantomActuallyWinsTheGame() {
        //skip the dungeon crawl — build a direct boss arena for this test
        Player hero = new Player("Hero", 999, 999, 10, new Inventory(10));
        Room finalRoom = new Room("final", "Final Chamber", "The last stand.");
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        finalRoom.addMonster(phantom);
        finalRoom.addItem(new QuestItem("Lost Gradebook", "The legendary missing gradebook."));
        hero.setCurrentRoom(finalRoom);

        Quest quest = new Quest("Escape the Basement",
                "Recover the gradebook and defeat the Phantom.");
        QuestTracker tracker = new QuestTracker();
        CombatEngine combatEngine = new CombatEngine(new DamageCalculator(), tracker);
        InteractionEngine interactionEngine = new InteractionEngine(tracker);
        GameEngine bossArena = new GameEngine(
                hero, quest,
                new MovementEngine(new TrapResolver()),
                combatEngine, interactionEngine);

        bossArena.pickUpItem("Lost Gradebook");
        while (phantom.isAlive()) {
            bossArena.attack("Final Exam Phantom");
        }

        assertTrue(quest.isGradebookRecovered(), "Gradebook should be marked recovered");
        assertTrue(quest.isPhantomDefeated(), "Phantom should be marked defeated");
        assertTrue(bossArena.isGameWon(), "Both objectives done — game should be won");
    }

    //damage floor

    @ParameterizedTest(name = "attack {0} vs defense {1} should deal {2} damage (floor is always 1)")
    @CsvSource({
        "10, 5, 5",  //normal hit
        "5,  5, 1",  //attack ties defense — still does 1
        "1, 10, 1",  //hopelessly outclassed — still does 1
        "7,  1, 6"   //actual player vs TA numbers from the game
    })
    void damageNeverDropsBelowOneNoMatterHowToughTheMonsterIs(int atk, int def, int expected) {
        DamageCalculator calc = new DamageCalculator();
        Player attacker = new Player("Attacker", 100, atk, 0, new Inventory(5));
        Monster defender = new Monster("Tank", 100, 5, def, List.of());
        assertEquals(expected, calc.calculatePlayerDamage(attacker, defender));
    }
}
