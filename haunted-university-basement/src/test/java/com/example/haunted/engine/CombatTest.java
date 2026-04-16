//Brady
//Prompt:
//tests for combat. check that attacking the TA deals damage and that the TA hits back
//when it survives. test killing the TA drops loot and that a dead monster stays dead.
//edge cases: attacking something that isn't there, attacking a dead monster, attacking
//while player is already dead. test the boss enrage mechanic directly. also test that a
//weak player gets counterattacked by the boss. build a small custom arena to test that
//picking up the lost grade book and killing the phantom wins the game. parameterized test
//for damage floor. use Chad for the weak student

package com.example.haunted.engine;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.haunted.events.CombatResult;
import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Player;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.Room;
import com.example.haunted.rules.DamageCalculator;

public class CombatTest {

    private GameEngine game;

    @Test
    void attackDamagesMonster() {
        int hpBefore = game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().getHealth();
        CombatResult res = game.attack("Sleep-Deprived TA");
        assertTrue(res.isSuccess());
        assertTrue(res.getDamageToMonster() > 0);
        assertTrue(game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().getHealth() < hpBefore);
    }

    @Test
    void theTADoesNotJustStandThereAndTakeIt() {
        //TA has 18 HP, one hit does 6 damage so it survives and swings back
        CombatResult res = game.attack("Sleep-Deprived TA");
        assertTrue(res.isSuccess());
        assertFalse(res.isMonsterDefeated(), "TA should still be standing after one hit");
        assertTrue(res.getDamageToPlayer() > 0);
        assertTrue(game.getPlayer().getHealth() < game.getPlayer().getMaxHealth());
    }

    @Test
    void whenYouFinallyKillTheTAItDropsItsCoffee() {
        //player deals max(1, 7-1) = 6 damage per hit; TA has 18 HP so 3 hits to finish it
        while (game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive()) {
            game.attack("Sleep-Deprived TA");
        }
        assertTrue(game.getCurrentRoom().findItem("Coffee Potion").isPresent(),
                "TA drops a Coffee Potion as loot, small consolation");
    }

    @Test
    void killedMonsterIsDead() {
        while (game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive()) {
            game.attack("Sleep-Deprived TA");
        }
        assertFalse(game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive());
    }

    @Test
    void attackMissingMonster() {
        CombatResult res = game.attack("The Easter Bunny");
        assertFalse(res.isSuccess());
    }

    @Test
    void youCannotKickAMonsterThatIsAlreadyDead() {
        while (game.getCurrentRoom().findMonster("Sleep-Deprived TA").get().isAlive()) {
            game.attack("Sleep-Deprived TA");
        }
        CombatResult res = game.attack("Sleep-Deprived TA");
        assertFalse(res.isSuccess(), "Beating a dead TA is not allowed");
    }

    @Test
    void deadPlayerCantAttack() {
        game.getPlayer().takeDamage(9999); //instant death
        CombatResult res = game.attack("Sleep-Deprived TA");
        assertFalse(res.isSuccess());
        assertTrue(game.isGameOver());
    }

    @Test
    void theBossIsChillUntilYouGetItBelowHalfHealthThenItLosesIt() {
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        assertEquals(10, phantom.getCurrentAttack(), "Boss is calm at full HP");
        phantom.takeDamage(21); //push it below the halfway mark (40/2 = 20)
        assertEquals(13, phantom.getCurrentAttack(), "Boss gains +3 rage bonus when enraged");
    }

    @Test
    void anUnderpoweredStudentGetsCounterattackedByTheBoss() {
        //using Chad so the boss survives long enough to hit back
        Player underpoweredStudent = new Player("Chad", 100, 5, 0, new Inventory(10));
        Room finalRoom = new Room("final", "Final Chamber");
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        finalRoom.addMonster(phantom);
        underpoweredStudent.setCurrentRoom(finalRoom);

        Quest quest = new Quest("Escape the Basement", "Good luck.");
        CombatEngine combatEngine = new CombatEngine();

        int hp = underpoweredStudent.getHealth();
        CombatResult res = combatEngine.attack(underpoweredStudent, quest, phantom);

        assertTrue(res.isSuccess());
        assertFalse(res.isMonsterDefeated(), "Boss survives Chad's weak hit");
        assertTrue(underpoweredStudent.getHealth() < hp, "Boss absolutely hits back");
    }

    @Test
    void retrievingTheGradebookAndSlayingThePhantomActuallyWinsTheGame() {
        //skip the dungeon crawl, build a direct boss arena for this test
        Player hero = new Player("Hero", 999, 999, 10, new Inventory(10));
        Room finalRoom = new Room("final", "Final Chamber");
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        finalRoom.addMonster(phantom);
        finalRoom.addItem(new QuestItem("Lost Gradebook", "The legendary missing grade book."));
        hero.setCurrentRoom(finalRoom);

        Quest quest = new Quest("Escape the Basement", "Recover the grade book and defeat the Phantom.");
        CombatEngine combatEngine = new CombatEngine();
        InteractionEngine interactionEngine = new InteractionEngine();
        GameEngine bossArena = new GameEngine(
                hero, quest,
                new MovementEngine(),
                combatEngine, interactionEngine);

        bossArena.pickUpItem("Lost Gradebook");
        while (phantom.isAlive()) {
            bossArena.attack("Final Exam Phantom");
        }

        assertTrue(quest.isGradebookRecovered());
        assertTrue(quest.isPhantomDefeated());
        assertTrue(bossArena.isGameWon(), "Both objectives done, game should be won");
    }

    //assignment says to use parameterized tests so using them for damage boundary values
    @ParameterizedTest(name = "attack {0} vs defense {1} should deal {2} damage (floor is always 1)")
    @CsvSource({
        "10, 5, 5",  //normal hit
        "5,  5, 1",  //attack ties defense, still does 1
        "1, 10, 1",  //hopelessly outclassed, still does 1
        "7,  1, 6"   //actual player vs TA numbers from the game
    })
    void damageFloorIsOne(int atk, int def, int expected) {
        DamageCalculator calc = new DamageCalculator();
        Player attacker = new Player("Attacker", 100, atk, 0, new Inventory(5));
        Monster defender = new Monster("Tank", 100, 5, def, List.of());
        assertEquals(expected, calc.calculatePlayerDamage(attacker, defender));
    }
}
