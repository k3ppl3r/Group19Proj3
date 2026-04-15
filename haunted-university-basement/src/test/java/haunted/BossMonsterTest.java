//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BossMonsterTest {

    //Final Exam Phantom stats from DungeonFactory: hp=40, atk=10, def=4, enragedBonus=3
    //enrage kicks in at health <= 40/2 = 20

    private BossMonster makePhantom() {
        return new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
    }

    @Test
    void bossIsAMonster() {
        assertInstanceOf(Monster.class, makePhantom());
    }

    @Test
    void fullHealthReturnsBaseAttack() {
        BossMonster boss = makePhantom();
        assertEquals(10, boss.getCurrentAttack());
    }

    @Test
    void healthJustAboveHalfStillBaseAttack() {
        BossMonster boss = makePhantom();
        boss.takeDamage(19); //health = 21
        assertEquals(10, boss.getCurrentAttack());
    }

    @Test
    void healthExactlyHalfTriggersEnrage() {
        BossMonster boss = makePhantom();
        boss.takeDamage(20); //health = 20, should be enraged
        assertEquals(13, boss.getCurrentAttack());
    }

    @Test
    void healthBelowHalfIsEnraged() {
        BossMonster boss = makePhantom();
        boss.takeDamage(30); //health = 10
        assertEquals(13, boss.getCurrentAttack());
    }

    @Test
    void nearlyDeadBossIsStillEnraged() {
        BossMonster boss = makePhantom();
        boss.takeDamage(39); //health = 1
        assertEquals(13, boss.getCurrentAttack());
    }

    @ParameterizedTest
    @CsvSource({
        "0,  10",
        "19, 10",
        "20, 13",
        "21, 13",
        "39, 13"
    })
    void enrageThresholdParameterized(int damageTaken, int expectedAttack) {
        BossMonster boss = makePhantom();
        boss.takeDamage(damageTaken);
        assertEquals(expectedAttack, boss.getCurrentAttack());
    }

    @Test
    void differentEnragedBonusIsAddedCorrectly() {
        //make sure it's the bonus being added, not some hardcoded value
        BossMonster boss = new BossMonster("Custom Boss", 10, 5, 0, List.of(), 7);
        boss.takeDamage(5); //health = 5 = maxHealth/2, enraged
        assertEquals(12, boss.getCurrentAttack());
    }
}
