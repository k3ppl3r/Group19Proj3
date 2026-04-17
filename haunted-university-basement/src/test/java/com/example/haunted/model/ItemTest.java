//Brady
//Prompt:
//tests for the Item abstract class. since Item can't be instantiated directly, use
//Key or Potion as a concrete subclass. check getName and getDescription return the
//right values, toString returns the name, and that null name or description throws
//NullPointerException. parameterized test for a few name/description combos

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ItemTest {

    @Test
    void getNameReturnsWhatWasPassedIn() {
        Item item = new Key("Archive Key", "Opens the archive door.");
        assertEquals("Archive Key", item.getName());
    }

    @Test
    void getDescriptionReturnsWhatWasPassedIn() {
        Item item = new Key("Archive Key", "Opens the archive door.");
        assertEquals("Opens the archive door.", item.getDescription());
    }

    @Test
    void toStringReturnsTheName() {
        Item item = new Key("Vault Key", "A heavy iron key.");
        assertEquals("Vault Key", item.toString());
    }

    @Test
    void nullNameThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Key(null, "some desc"));
    }

    @Test
    void nullDescriptionThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Key("Some Key", null));
    }

    @ParameterizedTest(name = "item \"{0}\" should have description \"{1}\"")
    @CsvSource({
        "Coffee Potion, Keeps you awake.",
        "Foam Sword, Surprisingly threatening.",
        "Trash Lid Shield, Repurposed for war."
    })
    void nameAndDescriptionMatchForVariousItems(String name, String desc) {
        Item item = new Key(name, desc);
        assertEquals(name, item.getName());
        assertEquals(desc, item.getDescription());
    }
}
