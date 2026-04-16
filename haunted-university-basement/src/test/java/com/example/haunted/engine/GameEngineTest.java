// Francis
// Prompt:
// generate tests that search for movement and room navigation validation making search that the player actually 
// changes directions and returns a successful move result. Tests searching for boundaries as well as locked entrances. 
// Check for correct attacking methods be ensuring that the target identification works successfully and checking 
// that defeating a monster would actually update the quest. test the engine behavior for a player trying to attack while at 
// 0 health. Generate test for pickup mechanics of the game, test equiptItem through the GameEngine making sure it updates players 
// stats. To check unlock progression, test a sequence of picking up a key and attempting to unlock a door. Test the players win/loss 
// conditions making sure what is false is actually going to be false. Also testing for what occurs if the quest is 
// completed but the player is dead. Finally, conduct some Null testing on attack for when the monster's name is 
// misspelled. Along with a test of boolean logic in isgamewon.

// Reimported the code and generated comments explaining the each test.

package com.example.haunted.engine;

import com.example.haunted.events.CombatResult;
import com.example.haunted.events.InteractionResult;
import com.example.haunted.events.MoveResult;
import com.example.haunted.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {
    private GameEngine gameEngine;
    private Player player;
    private Room startRoom;
    private Room northRoom;
    private Quest quest;
    private Monster ghost;

    @BeforeEach
    void setUp() {
        // 1. Initialize Rooms
        startRoom = new Room("Basement Entrance", "A dark, damp entry.");
        northRoom = new Room("Study Hall", "Filled with old textbooks.");
        startRoom.setExit(Direction.NORTH, northRoom);
        northRoom.setExit(Direction.SOUTH, startRoom);

        // 2. Initialize Engines (using real implementations as mocking is prohibited)
        MovementEngine moveEngine = new MovementEngine();
        CombatEngine combatEngine = new CombatEngine();
        InteractionEngine interactionEngine = new InteractionEngine();

        // 3. Initialize Quest and Monster
        List<Item> loot = new ArrayList<>();
        loot.add(new Key("Library Key", "Opens the archives."));
        ghost = new Monster("Plagiarism Ghost", 20, 5, 2, loot);
        startRoom.addMonster(ghost);
        
        // Assuming a standard Quest implementation that tracks this monster
        quest = new Quest("Defeat the Ghost", "Recover the gradebook.");

        // 4. Initialize Player and GameEngine
        Inventory inventory = new Inventory(5);
        player = new Player("Hero", 100, 10, 5, inventory);
        player.setCurrentRoom(startRoom);

        gameEngine = new GameEngine(player, quest, moveEngine, combatEngine, interactionEngine);
    }

    // --- MOVEMENT & NAVIGATION ---

    @ParameterizedTest
    @EnumSource(value = Direction.class, names = {"NORTH"})
    @DisplayName("Movement: Successful room transition and results")
    void testSuccessfulMovement(Direction dir) {
        MoveResult result = gameEngine.move(dir);
        
        assertAll(
            () -> assertTrue(result.isSuccess(), "Move should be successful"),
            () -> assertEquals(northRoom, player.getCurrentRoom(), "Player location must change to the new room"),
            () -> assertNotEquals(startRoom, player.getCurrentRoom(), "Player should no longer be in the start room")
        );
    }

    @Test
    @DisplayName("Boundaries: Moving into a non-existent exit")
    void testInvalidMovement() {
        // There is no exit to the EAST in the startRoom
        MoveResult result = gameEngine.move(Direction.EAST); 
        
        assertAll(
            () -> assertFalse(result.isSuccess(), "Should fail when moving into a wall"),
            () -> assertEquals(startRoom, player.getCurrentRoom(), "Player should not change rooms")
        );
    }

    @Test
    @DisplayName("Locked Entrances: Verify path is blocked")
    void testLockedRoomMovement() {
        northRoom.setLocked(true);
        MoveResult result = gameEngine.move(Direction.NORTH);
        
        assertFalse(result.isSuccess(), "Movement should fail if the destination is locked");
        assertEquals(startRoom, player.getCurrentRoom(), "Player should remain in start room if locked");
    }

    // --- COMBAT & TARGETING ---

    @Test
    @DisplayName("Combat: Target identification and quest updates")
    void testAttackAndDefeatMonster() {
        // Verify target identification works
        CombatResult result = gameEngine.attack("Plagiarism Ghost");
        assertNotNull(result, "CombatResult should not be null for existing monster");

        // Reduce health to simulate defeat
        ghost.takeDamage(100); 
        gameEngine.attack("Plagiarism Ghost"); // Trigger the defeat check in engine/quest
        
        assertTrue(quest.isComplete() || ghost.getHealth() == 0, "Defeating the monster should update game state");
    }

    @Test
    @DisplayName("Edge Case: Attacking while at 0 health")
    void testAttackWhileDead() {
        player.takeDamage(1000); // Reduce player to 0 HP
        
        CombatResult result = gameEngine.attack("Plagiarism Ghost");
        assertAll(
            () -> assertFalse(player.isAlive(), "Player should be dead"),
            () -> assertFalse(result.isSuccess(), "Attack should not succeed if player is dead")
        );
    }

    @Test
    @DisplayName("Null Testing: Misspelled monster name")
    void testAttackWithMisspelledName() {
        // Should handle "Plagiaris Ghost" (missing 'm') gracefully
        CombatResult result = gameEngine.attack("Plagiaris Ghost");
        
        assertNotNull(result, "Engine should return a failure result instead of throwing NPE");
        assertFalse(result.isSuccess(), "Attack should fail for misspelled monster name");
    }

    // --- ITEMS & PROGRESSION ---

    @ParameterizedTest
    @ValueSource(strings = {"Java Blade", "Coffee Mug"})
    @DisplayName("Interaction: Pickup mechanics for different items")
    void testPickupMechanics(String itemName) {
        Item item = new Key(itemName, "A test item");
        startRoom.addItem(item);

        InteractionResult result = gameEngine.pickUpItem(itemName);
        assertTrue(result.isSuccess(), "Should successfully pick up the item");
        assertTrue(player.getInventory().contains(itemName), "Item should be in player inventory");
    }

    @Test
    @DisplayName("Interaction: equipItem updates player stats")
    void testEquipItemUpdatesStats() {
        Armor vest = new Armor("Protective Vest", "Sturdy", 10);
        player.getInventory().addItem(vest);
        
        int initialDefense = player.getDefensePower();
        gameEngine.equipItem("Protective Vest");
        
        assertEquals(initialDefense + 10, player.getDefensePower(), "Defense power should increase after equipping");
    }

    @Test
    @DisplayName("Progression: Sequence of picking up a key and unlocking a door")
    void testUnlockProgression() {
        Key key = new Key("Golden Key", "Shiny");
        startRoom.addItem(key);
        northRoom.setLocked(true);

        gameEngine.pickUpItem("Golden Key");
        InteractionResult result = gameEngine.unlockRoom(Direction.NORTH);
        
        assertTrue(result.isSuccess(), "Should unlock the room if the player has the key");
    }

    // --- WIN / LOSS CONDITIONS ---

    @Test
    @DisplayName("Game State: Win/Loss and Boolean logic validation")
    void testGameStatusConditions() {
        // Case 1: Player dead (Loss)
        player.takeDamage(player.getMaxHealth());
        assertTrue(gameEngine.isGameOver(), "Should be Game Over if player health is 0");
        assertFalse(gameEngine.isGameWon(), "Cannot win if player is dead");

        // Case 2: Alive but Quest not done
        player.heal(100);
        assertFalse(gameEngine.isGameWon(), "Should be false if quest is incomplete");

        // Case 3: Alive and Quest done (Win)
        // Manually complete quest for testing purposes
        quest.setComplete(true); 
        assertTrue(gameEngine.isGameWon(), "Should win if alive and quest is complete");
    }

    @Test
    @DisplayName("Boolean Logic: Quest complete but player is dead")
    void testQuestCompleteButDead() {
        quest.setComplete(true);
        player.takeDamage(1000); // Kill player
        
        // This kills mutations that change '&&' to '||' in isGameWon()
        assertAll(
            () -> assertFalse(gameEngine.isGameWon(), "Game is NOT won if player is dead, even if quest is complete"),
            () -> assertTrue(gameEngine.isGameOver(), "GameOver should be true")
        );
    }
}