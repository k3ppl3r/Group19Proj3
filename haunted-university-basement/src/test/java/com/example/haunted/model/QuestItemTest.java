//Hannah Huang
//based largely on my PotionTest code

package com.example.haunted.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;

class QuestItemTest {
	@Test
	@DisplayName("Quest Item: proper naming and description assignment")
	void testNamingAndDescription() {
		String name = "Important thing";
		String desc = "trust me bro";
		QuestItem testItem = new QuestItem(name, desc);
		
		assertAll("Verify Item properties are inherited and assigned correctly",
			() -> assertEquals(name, testItem.getName(), "Name should match the constructor input"),
			() -> assertEquals(desc, testItem.getDescription(), "Description should match constructor input"),
			() -> assertEquals(name, testItem.toString(), "toString should return the item name")
		);
	}
	
	@Test
	@DisplayName("Null Checks: ensure null names or descriptions throw NullPointerException")
	void testNullGuards() {
		assertAll(
			() -> assertThrows(NullPointerException.class, () -> new QuestItem(null, "desc")),
			() -> assertThrows(NullPointerException.class, () -> new QuestItem("name", null))
		);
	}
	
	@Test
	@DisplayName("Hierarchy: verify QuestItem is a subclass of Item")
	void testHierarchy() {
		QuestItem item = new QuestItem("big thing", "very important");
		
		assertTrue(item instanceof Item, "QuestItem must be a subclass of Item");
	}
}
