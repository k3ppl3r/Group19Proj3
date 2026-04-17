//Brady
//Prompt:
//tests for Inventory. check that adding an item works, that contains returns true after
//adding and false for something not there, that removing an item takes it out, that
//removing something not in inventory returns null, that a full inventory rejects new items,
//that getItems is unmodifiable, and that capacity is stored correctly. parameterized test
//for isFull at boundaries

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class InventoryTest {

    @Test
    void addItem() {
        Inventory bag = new Inventory(5);
        bag.addItem(new Key("Dorm Key", "Opens your room."));
        assertTrue(bag.contains("Dorm Key"));
    }

    @Test
    void containsIsCaseInsensitive() {
        Inventory bag = new Inventory(5);
        bag.addItem(new Potion("Coffee Potion", "Wakes you up.", 20));
        assertTrue(bag.contains("coffee potion"));
    }

    @Test
    void containsMissing() {
        Inventory bag = new Inventory(5);
        assertFalse(bag.contains("Invisible Homework"));
    }

    @Test
    void removeItem() {
        Inventory bag = new Inventory(5);
        bag.addItem(new Key("Archive Key", "Opens the archive."));
        Item removed = bag.removeItem("Archive Key");
        assertNotNull(removed);
        assertFalse(bag.contains("Archive Key"));
    }

    @Test
    void removeMissingReturnsNull() {
        Inventory bag = new Inventory(5);
        assertNull(bag.removeItem("Excalibur"));
    }

    @Test
    void fullBagRejectsNewItems() {
        Inventory bag = new Inventory(2);
        bag.addItem(new Key("Key 1", "First key."));
        bag.addItem(new Key("Key 2", "Second key."));
        boolean added = bag.addItem(new Key("Key 3", "One too many."));
        assertFalse(added);
        assertEquals(2, bag.getItems().size());
    }

    @Test
    void getItemsIsUnmodifiable() {
        Inventory bag = new Inventory(5);
        bag.addItem(new Key("Some Key", "desc"));
        assertThrows(UnsupportedOperationException.class,
                () -> bag.getItems().add(new Key("Sneaky Key", "shouldn't work")));
    }

    @Test
    void capacityStoredCorrectly() {
        Inventory bag = new Inventory(8);
        assertEquals(8, bag.getCapacity());
    }

    @ParameterizedTest(name = "{0} items in a bag of size {1} — full={2}")
    @CsvSource({
        "0, 3, false",
        "2, 3, false",
        "3, 3, true",
        "1, 1, true"
    })
    void isFullAtBoundaries(int itemsToAdd, int capacity, boolean expectedFull) {
        Inventory bag = new Inventory(capacity);
        for (int i = 0; i < itemsToAdd; i++) {
            bag.addItem(new Key("Key " + i, "filler"));
        }
        assertEquals(expectedFull, bag.isFull());
    }
}
