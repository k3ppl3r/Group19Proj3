//Brady
//Prompt:
//Write humanized JUnit 5 tests for InteractionEngine via GameEngine covering: pickup success adds to
//inventory, pickup nonexistent item fails, full inventory blocks pickup and item stays in room, picking
//up Lost Gradebook marks quest gradebookRecovered. Equip weapon boosts attack, equip armor boosts defense,
//equipping a potion fails, equipping something not in inventory fails. Using a potion heals a wounded
//player, using a key fails, using item not in inventory fails. Unlocking exam archive with correct key
//succeeds, unlocking without key fails, unlocking a direction with no room fails, unlocking already-open
//room succeeds quietly. Parameterized inventory boundary test at 0, 1, 7, and 8 items out of 8 capacity.

package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.haunted.config.DungeonFactory;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Weapon;

public class InteractionTest {

    private GameEngine game;

    @BeforeEach
    void startInLectureHall() {
        game = DungeonFactory.createGame();
        game.move(Direction.EAST); //stairwell -> lectureHall, which has the Coffee Potion
    }

    //picking up items

    @Test
    void grabbingTheCoffeePotionPutsItInYourBackpack() {
        InteractionResult result = game.pickUpItem("Coffee Potion");
        assertTrue(result.isSuccess());
        assertTrue(game.getPlayer().getInventory().contains("Coffee Potion"),
                "Coffee Potion should now be in inventory");
    }

    @Test
    void tryingToPickUpSomethingThatDoesNotExistGoesNowhere() {
        InteractionResult result = game.pickUpItem("Invisible Homework");
        assertFalse(result.isSuccess(), "You cannot pick up something that isn't there");
    }

    @Test
    void aFullBackpackMeansYouHaveToLeaveThingsOnTheFloor() {
        //fill up all 8 inventory slots with junk
        Player player = game.getPlayer();
        for (int i = 0; i < 8; i++) {
            player.getInventory().addItem(new Potion("Filler Potion " + i, "Taking up space.", 1));
        }
        assertTrue(player.getInventory().isFull());

        InteractionResult result = game.pickUpItem("Coffee Potion");
        assertFalse(result.isSuccess(), "Full inventory should block pickup");
        assertTrue(game.getCurrentRoom().findItem("Coffee Potion").isPresent(),
                "Coffee Potion should still be on the floor since we couldn't grab it");
    }

    @Test
    void findingTheLostGradebookUpdatesTheQuest() {
        //full route: lectureHall -> labStorage (grab key) -> back -> unlock -> examArchive -> grab it
        game.move(Direction.EAST);        //lectureHall -> labStorage
        game.pickUpItem("Archive Key");
        game.move(Direction.WEST);        //back to lectureHall
        game.unlockRoom(Direction.NORTH); //use the key to open the exam archive
        game.move(Direction.NORTH);       //lectureHall -> examArchive

        InteractionResult result = game.pickUpItem("Lost Gradebook");
        assertTrue(result.isSuccess());
        assertTrue(game.getQuest().isGradebookRecovered(),
                "The quest should know we finally found that gradebook");
    }

    //equipping items

    @Test
    void equippingAWeaponMakesYouHitHarder() {
        Weapon foamSword = new Weapon("Foam Sword", "Surprisingly threatening.", 7);
        game.getPlayer().getInventory().addItem(foamSword);
        int attackBefore = game.getPlayer().getAttackPower();

        game.equipItem("Foam Sword");
        assertEquals(attackBefore + 7, game.getPlayer().getAttackPower(),
                "Foam sword should add 7 to attack power");
    }

    @Test
    void equippingArmorMakesYouHarderToKill() {
        //calculator shield is in labStorage just east of here
        game.move(Direction.EAST);        //lectureHall -> labStorage
        game.pickUpItem("Calculator Shield");
        int defenseBefore = game.getPlayer().getDefensePower();

        game.equipItem("Calculator Shield");
        assertEquals(defenseBefore + 3, game.getPlayer().getDefensePower(),
                "Calculator Shield should add 3 to defense");
    }

    @Test
    void youCannotWearACoffeePotion() {
        game.pickUpItem("Coffee Potion");
        InteractionResult result = game.equipItem("Coffee Potion");
        assertFalse(result.isSuccess(), "Potions are not gear");
    }

    @Test
    void youCannotEquipSomethingYouDoNotEvenHave() {
        InteractionResult result = game.equipItem("Excalibur");
        assertFalse(result.isSuccess(), "Excalibur is not in this dungeon");
    }

    //using items

    @Test
    void drinkingTheCoffeePotionActuallyHealsYou() {
        game.pickUpItem("Coffee Potion");
        game.getPlayer().takeDamage(20); // get roughed up first
        int healthAfterHit = game.getPlayer().getHealth();

        InteractionResult result = game.useItem("Coffee Potion");
        assertTrue(result.isSuccess());
        assertTrue(game.getPlayer().getHealth() > healthAfterHit,
                "Coffee Potion should restore some HP");
    }

    @Test
    void youCannotDrinkAKey() {
        game.move(Direction.EAST);        //labStorage has the Archive Key
        game.pickUpItem("Archive Key");
        InteractionResult result = game.useItem("Archive Key");
        assertFalse(result.isSuccess(), "Keys are not beverages");
    }

    @Test
    void tryingToUseAnItemYouDoNotHaveFails() {
        InteractionResult result = game.useItem("Mystery Smoothie");
        assertFalse(result.isSuccess(), "Can't use what you don't have");
    }

    //unlocking rooms

    @Test
    void havingTheRightKeyUnlocksTheExamArchive() {
        game.move(Direction.EAST);        //lectureHall -> labStorage
        game.pickUpItem("Archive Key");
        game.move(Direction.WEST);        //back to lectureHall

        InteractionResult result = game.unlockRoom(Direction.NORTH);
        assertTrue(result.isSuccess());
        assertFalse(game.getCurrentRoom().getExit(Direction.NORTH).isLocked(),
                "Exam archive should now be open");
    }

    @Test
    void tryingToUnlockADoorWithNoKeyOnYouFails() {
        //standing in lectureHall, archive is north, but we never grabbed the key
        InteractionResult result = game.unlockRoom(Direction.NORTH);
        assertFalse(result.isSuccess(), "No key, no entry");
        assertTrue(game.getCurrentRoom().getExit(Direction.NORTH).isLocked(),
                "Door should still be locked");
    }

    @Test
    void tryingToUnlockADirectionWithNoRoomAtAllFails() {
        game.move(Direction.WEST); //back to stairwell, which has no north exit
        InteractionResult result = game.unlockRoom(Direction.NORTH);
        assertFalse(result.isSuccess(), "There is no door there to unlock");
    }

    @Test
    void unlockingAnAlreadyOpenDoorJustSucceedsQuietly() {
        //brokenElevator is south of lectureHall and has no lock
        InteractionResult result = game.unlockRoom(Direction.SOUTH);
        assertTrue(result.isSuccess(), "Already-open room should not cause an error");
    }

    //inventory boundary checks

    @ParameterizedTest(name = "bag with {0} out of 8 slots filled")
    @ValueSource(ints = {0, 1, 7, 8})
    void inventoryKnowsWhenItIsFull(int itemsToAdd) {
        Inventory bag = new Inventory(8);
        for (int i = 0; i < itemsToAdd; i++) {
            bag.addItem(new Potion("Potion " + i, "Filler.", 1));
        }
        assertEquals(itemsToAdd >= 8, bag.isFull());
    }
}
