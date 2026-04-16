//Brady
//Prompt:
//tests for item interaction. picking up an item, picking up something that doesn't exist,
//and picking up when inventory is full. picking up the lost grade book should update the quest.
//equipping a weapon and armor and checking that stats change. equipping a potion should fail,
//equipping something you don't have should fail. using a potion to heal, trying to use a key,
//using something not in inventory. unlocking with the right key, without a key, toward a
//direction with no room, and on a room already open. parameterized test for inventory capacity
//at boundary values 0, 1, 7, and 8

package com.example.haunted.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.haunted.events.InteractionResult;
import com.example.haunted.model.Direction;
import com.example.haunted.model.Inventory;
import com.example.haunted.model.Player;
import com.example.haunted.model.Potion;
import com.example.haunted.model.Weapon;

public class InteractionTest {

    private GameEngine game;

    @Test
    void pickupAddsToInventory() {
        InteractionResult res = game.pickUpItem("Coffee Potion");
        assertTrue(res.isSuccess());
        assertTrue(game.getPlayer().getInventory().contains("Coffee Potion"));
    }

    @Test
    void pickupNonexistentItemFails() {
        InteractionResult res = game.pickUpItem("Invisible Homework");
        assertFalse(res.isSuccess());
    }

    @Test
    void aFullBackpackMeansYouHaveToLeaveThingsOnTheFloor() {
        //fill up all 8 slots with junk first
        Player player = game.getPlayer();
        for (int i = 0; i < 8; i++) {
            player.getInventory().addItem(new Potion("Filler Potion " + i, "Taking up space.", 1));
        }
        assertTrue(player.getInventory().isFull());

        InteractionResult res = game.pickUpItem("Coffee Potion");
        assertFalse(res.isSuccess(), "Full inventory should block pickup");
        assertTrue(game.getCurrentRoom().findItem("Coffee Potion").isPresent(),
                "Coffee Potion should still be on the floor since we couldn't grab it");
    }

    @Test
    void findingTheLostGradebookUpdatesTheQuest() {
        //had to figure out the path for this one: east to lab, grab key, back, unlock, north
        game.move(Direction.EAST);
        game.pickUpItem("Archive Key");
        game.move(Direction.WEST);
        game.unlockRoom(Direction.NORTH);
        game.move(Direction.NORTH);

        InteractionResult res = game.pickUpItem("Lost Gradebook");
        assertTrue(res.isSuccess());
        assertTrue(game.getQuest().isGradebookRecovered(),
                "The quest should know we finally found that grade book");
    }

    @Test
    void equippingAWeaponMakesYouHitHarder() {
        Weapon foamSword = new Weapon("Foam Sword", "Surprisingly threatening.", 7);
        game.getPlayer().getInventory().addItem(foamSword);
        int atk = game.getPlayer().getAttackPower();

        game.equipItem("Foam Sword");
        assertEquals(atk + 7, game.getPlayer().getAttackPower());
    }

    @Test
    void equippingArmorMakesYouHarderToKill() {
        //Calculator Shield is in labStorage just east of here
        game.move(Direction.EAST);
        game.pickUpItem("Calculator Shield");
        int def = game.getPlayer().getDefensePower();

        game.equipItem("Calculator Shield");
        assertEquals(def + 3, game.getPlayer().getDefensePower());
    }

    @Test
    void youCannotWearACoffeePotion() {
        game.pickUpItem("Coffee Potion");
        InteractionResult res = game.equipItem("Coffee Potion");
        assertFalse(res.isSuccess(), "Potions are not gear");
    }

    @Test
    void equipItemNotInInventory() {
        assertFalse(game.equipItem("Excalibur").isSuccess());
    }

    @Test
    void drinkingTheCoffeePotionActuallyHealsYou() {
        game.pickUpItem("Coffee Potion");
        game.getPlayer().takeDamage(20); //get roughed up first
        int hp = game.getPlayer().getHealth();

        InteractionResult res = game.useItem("Coffee Potion");
        assertTrue(res.isSuccess());
        assertTrue(game.getPlayer().getHealth() > hp);
    }

    @Test
    void youCannotDrinkAKey() {
        game.move(Direction.EAST); //labStorage has the Archive Key
        game.pickUpItem("Archive Key");
        assertFalse(game.useItem("Archive Key").isSuccess(), "Keys are not beverages");
    }

    @Test
    void useItemNotInInventory() {
        assertFalse(game.useItem("Mystery Smoothie").isSuccess());
    }

    @Test
    void havingTheRightKeyUnlocksTheExamArchive() {
        game.move(Direction.EAST);
        game.pickUpItem("Archive Key");
        game.move(Direction.WEST);

        InteractionResult res = game.unlockRoom(Direction.NORTH);
        assertTrue(res.isSuccess());
        assertFalse(game.getCurrentRoom().getExit(Direction.NORTH).isLocked());
    }

    @Test
    void tryingToUnlockADoorWithNoKeyOnYouFails() {
        //standing in lectureHall, archive is north, never grabbed the key
        InteractionResult res = game.unlockRoom(Direction.NORTH);
        assertFalse(res.isSuccess(), "No key, no entry");
        assertTrue(game.getCurrentRoom().getExit(Direction.NORTH).isLocked());
    }

    @Test
    void unlockNoRoomFails() {
        game.move(Direction.WEST); //back to stairwell, no north exit
        assertFalse(game.unlockRoom(Direction.NORTH).isSuccess());
    }

    @Test
    void unlockingAnAlreadyOpenDoorJustSucceedsQuietly() {
        //brokenElevator south of lectureHall has no lock
        assertTrue(game.unlockRoom(Direction.SOUTH).isSuccess());
    }

    //prof said to use parameterized tests, using them for inventory boundary values
    @ParameterizedTest(name = "bag with {0} out of 8 slots filled")
    @ValueSource(ints = {0, 1, 7, 8})
    void inventoryFullTest(int itemsToAdd) {
        Inventory bag = new Inventory(8);
        for (int i = 0; i < itemsToAdd; i++) {
            bag.addItem(new Potion("Potion " + i, "Filler.", 1));
        }
        assertEquals(itemsToAdd >= 8, bag.isFull());
    }
}
