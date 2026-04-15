//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonsterTest {

    private Monster makeMonster() {
        return new Monster("Sleep-Deprived TA", 18, 6, 1, List.of());
    }

    @Test
    void nameIsSetCorrectly() {
        Monster m = makeMonster();
        assertEquals("Sleep-Deprived TA", m.getName());
    }

    @Test
    void healthAndMaxHealthStartEqual() {
        Monster m = makeMonster();
        assertEquals(18, m.getHealth());
        assertEquals(18, m.getMaxHealth());
    }

    @Test
    void attackAndDefenseAreCorrect() {
        Monster m = makeMonster();
        assertEquals(6, m.getAttack());
        assertEquals(1, m.getDefense());
    }

    @Test
    void nullNameThrows() {
        assertThrows(NullPointerException.class, () ->
            new Monster(null, 10, 5, 1, List.of())
        );
    }

    @Test
    void nullLootThrows() {
        assertThrows(NullPointerException.class, () ->
            new Monster("Ghost", 10, 5, 1, null)
        );
    }

    //loot

    @Test
    void emptyLootReturnsEmptyList() {
        Monster m = makeMonster();
        assertTrue(m.getLoot().isEmpty());
    }

    @Test
    void lootItemsAreReturnedCorrectly() {
        Potion drop = new Potion("Coffee Potion", "Backup coffee", 15);
        Monster m = new Monster("TA", 18, 6, 1, List.of(drop));
        assertEquals(1, m.getLoot().size());
        assertEquals("Coffee Potion", m.getLoot().get(0).getName());
    }

    @Test
    void lootListIsUnmodifiable() {
        Monster m = new Monster("TA", 18, 6, 1, List.of(new Potion("p", "d", 5)));
        assertThrows(UnsupportedOperationException.class, () ->
            m.getLoot().add(new Potion("extra", "d", 1))
        );
    }

    //taking damage

    @Test
    void damageReducesHealth() {
        Monster m = makeMonster();
        m.takeDamage(8);
        assertEquals(10, m.getHealth());
    }

    @Test
    void fatalDamageSetsHealthToZero() {
        Monster m = makeMonster();
        m.takeDamage(100);
        assertEquals(0, m.getHealth());
    }

    @Test
    void zeroDamageDoesNothing() {
        Monster m = makeMonster();
        m.takeDamage(0);
        assertEquals(18, m.getHealth());
    }

    @Test
    void negativeDamageIsIgnored() {
        Monster m = makeMonster();
        m.takeDamage(-5);
        assertEquals(18, m.getHealth());
    }

    @ParameterizedTest
    @CsvSource({"5, 13", "18, 0", "20, 0", "1, 17"})
    void takeDamageParameterized(int dmg, int expectedHealth) {
        Monster m = makeMonster();
        m.takeDamage(dmg);
        assertEquals(expectedHealth, m.getHealth());
    }

    //alive checks

    @Test
    void monsterIsAliveAtFullHealth() {
        assertTrue(makeMonster().isAlive());
    }

    @Test
    void monsterIsAliveWithPartialHealth() {
        Monster m = makeMonster();
        m.takeDamage(10);
        assertTrue(m.isAlive());
    }

    @Test
    void monsterIsDeadAtZeroHealth() {
        Monster m = makeMonster();
        m.takeDamage(18);
        assertFalse(m.isAlive());
    }
}
