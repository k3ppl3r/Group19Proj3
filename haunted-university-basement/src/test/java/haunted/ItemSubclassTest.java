//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemSubclassTest {

    //weapon

    @Test
    void weaponNameAndDescriptionAreSet() {
        Weapon w = new Weapon("Stapler of Justice", "A heavy-duty stapler.", 4);
        assertEquals("Stapler of Justice", w.getName());
        assertEquals("A heavy-duty stapler.", w.getDescription());
    }

    @Test
    void weaponAttackBonusIsCorrect() {
        Weapon w = new Weapon("Stapler of Justice", "A heavy-duty stapler.", 4);
        assertEquals(4, w.getAttackBonus());
    }

    @Test
    void weaponWithZeroBonusReturnsZero() {
        Weapon w = new Weapon("Bare Hands", "Just your fists.", 0);
        assertEquals(0, w.getAttackBonus());
    }

    @Test
    void weaponIsAnItem() {
        assertInstanceOf(Item.class, new Weapon("Sword", "Sharp", 3));
    }

    @Test
    void weaponIsEquippable() {
        assertInstanceOf(Equippable.class, new Weapon("Sword", "Sharp", 3));
    }

    @Test
    void weaponToStringReturnsName() {
        Weapon w = new Weapon("Dry Erase Sword", "Marker blade.", 6);
        assertEquals("Dry Erase Sword", w.toString());
    }

    //armor

    @Test
    void armorNameAndDescriptionAreSet() {
        Armor a = new Armor("Calculator Shield", "Battered calculator.", 3);
        assertEquals("Calculator Shield", a.getName());
        assertEquals("Battered calculator.", a.getDescription());
    }

    @Test
    void armorDefenseBonusIsCorrect() {
        Armor a = new Armor("Calculator Shield", "Battered calculator.", 3);
        assertEquals(3, a.getDefenseBonus());
    }

    @Test
    void armorWithZeroBonusReturnsZero() {
        Armor a = new Armor("Paper Vest", "Thin paper armor.", 0);
        assertEquals(0, a.getDefenseBonus());
    }

    @Test
    void armorIsAnItem() {
        assertInstanceOf(Item.class, new Armor("Shield", "Big shield", 2));
    }

    @Test
    void armorIsEquippable() {
        assertInstanceOf(Equippable.class, new Armor("Shield", "Big shield", 2));
    }

    //potion

    @Test
    void potionNameAndDescriptionAreSet() {
        Potion p = new Potion("Coffee Potion", "A suspiciously warm cup.", 15);
        assertEquals("Coffee Potion", p.getName());
        assertEquals("A suspiciously warm cup.", p.getDescription());
    }

    @Test
    void potionHealingAmountIsCorrect() {
        Potion p = new Potion("Coffee Potion", "A suspiciously warm cup.", 15);
        assertEquals(15, p.getHealingAmount());
    }

    @Test
    void potionUseHealsThePlayer() {
        Player player = new Player("Hero", 50, 5, 2, new Inventory(5));
        player.takeDamage(20); // health = 30
        Potion p = new Potion("Coffee Potion", "Warm.", 15);
        p.use(player);
        assertEquals(45, player.getHealth());
    }

    @Test
    void potionUseDoesNotExceedMaxHealth() {
        Player player = new Player("Hero", 50, 5, 2, new Inventory(5));
        player.takeDamage(5); // health = 45
        Potion p = new Potion("Big Potion", "Overpowered.", 100);
        p.use(player);
        assertEquals(50, player.getHealth());
    }

    @Test
    void potionIsUsable() {
        assertInstanceOf(Usable.class, new Potion("P", "D", 10));
    }

    @Test
    void potionIsAnItem() {
        assertInstanceOf(Item.class, new Potion("P", "D", 10));
    }

    //key

    @Test
    void keyNameAndDescriptionAreSet() {
        Key k = new Key("Archive Key", "Opens the Exam Archive.");
        assertEquals("Archive Key", k.getName());
        assertEquals("Opens the Exam Archive.", k.getDescription());
    }

    @Test
    void keyIsAnItem() {
        assertInstanceOf(Item.class, new Key("Key", "A key"));
    }

    //questitem

    @Test
    void questItemNameAndDescriptionAreSet() {
        QuestItem qi = new QuestItem("Lost Gradebook", "The legendary missing gradebook.");
        assertEquals("Lost Gradebook", qi.getName());
        assertEquals("The legendary missing gradebook.", qi.getDescription());
    }

    @Test
    void questItemIsAnItem() {
        assertInstanceOf(Item.class, new QuestItem("LG", "Desc"));
    }
}
