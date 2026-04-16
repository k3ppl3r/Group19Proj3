//Francis
//Prompt: here is interaction result. java, Lets create some tests that are focused on 
// checking the Boolean logic along with ensuring that the 
// strings contained in the methods will be operating correctly.

package com.example.haunted.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for InteractionResult focusing on Boolean logic integrity,
 * String correctness, and mutation killing.
 */
class InteractionResultTest {

    @Test
    @DisplayName("Data Integrity: Verify constructor accurately maps fields")
    void testConstructorAndGetters() {
        String expectedMessage = "You found the Lost Gradebook!";
        InteractionResult result = new InteractionResult(true, expectedMessage);

        assertAll("Verify interaction fields",
            () -> assertTrue(result.isSuccess(), "Success flag should remain true"),
            () -> assertEquals(expectedMessage, result.getMessage(), "Message must match constructor input")
        );
    }

    @ParameterizedTest
    @CsvSource({
        "true, 'Successfully unlocked the door.'",
        "false, 'The door is already locked.'",
        "true, 'Item added to inventory.'",
        "false, 'Inventory is full!'"
    })
    @DisplayName("Boolean Logic: Testing success/failure branches with varied messages")
    void testInteractionOutcomes(boolean success, String message) {
        InteractionResult result = new InteractionResult(success, message);
        
        // These assertions kill mutations that flip boolean logic 
        // or return hardcoded strings/nulls instead of the field.
        assertEquals(success, result.isSuccess(), "The success state was not preserved.");
        assertEquals(message, result.getMessage(), "The interaction message was corrupted or lost.");
    }

    @Test
    @DisplayName("Mutation Killing: Ensuring strings are not hardcoded or swapped")
    void testMessageDistinctness() {
        String msg1 = "Message A";
        String msg2 = "Message B";
        InteractionResult result1 = new InteractionResult(true, msg1);
        InteractionResult result2 = new InteractionResult(true, msg2);

        assertNotEquals(result1.getMessage(), result2.getMessage(), 
            "Different inputs should produce different result messages.");
    }

    @Test
    @DisplayName("Edge Case: Handling of empty or blank messages")
    void testEmptyMessageHandling() {
        InteractionResult result = new InteractionResult(false, "");
        
        assertAll(
            () -> assertFalse(result.isSuccess()),
            () -> assertEquals("", result.getMessage(), "Should correctly store an empty string.")
        );
    }
}