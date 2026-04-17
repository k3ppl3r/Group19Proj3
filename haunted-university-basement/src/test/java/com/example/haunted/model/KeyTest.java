//Brady
//Prompt:
//tests for the Key class. check that name and description come back right, that Key
//extends Item, that null name and null description both throw NullPointerException,
//and that toString returns the key name. parameterized test for a few different key names

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class KeyTest {

    @Test
    void keyStoresNameCorrectly() {
        Key k = new Key("Archive Key", "Opens the exam archive.");
        assertEquals("Archive Key", k.getName());
    }

    @Test
    void keyStoresDescriptionCorrectly() {
        Key k = new Key("Vault Key", "A heavy iron key.");
        assertEquals("A heavy iron key.", k.getDescription());
    }

    @Test
    void keyIsAnItem() {
        Key k = new Key("Dorm Key", "Opens your room.");
        assertInstanceOf(Item.class, k);
    }

    @Test
    void toStringReturnsKeyName() {
        Key k = new Key("Master Key", "Opens everything apparently.");
        assertEquals("Master Key", k.toString());
    }

    @Test
    void nullNameThrows() {
        assertThrows(NullPointerException.class, () -> new Key(null, "some desc"));
    }

    @Test
    void nullDescriptionThrows() {
        assertThrows(NullPointerException.class, () -> new Key("Some Key", null));
    }

    @ParameterizedTest(name = "key named \"{0}\" should store name \"{0}\"")
    @CsvSource({
        "Archive Key, Opens the archive.",
        "Vault Key, Deep in the dean's office.",
        "Dorm Key, Gets you back to your room."
    })
    void variousKeyNamesAndDescriptions(String name, String desc) {
        Key k = new Key(name, desc);
        assertEquals(name, k.getName());
        assertEquals(desc, k.getDescription());
    }
}
