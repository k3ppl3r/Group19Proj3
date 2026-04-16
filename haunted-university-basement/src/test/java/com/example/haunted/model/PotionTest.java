//Hannah Huang
//prompt:
//tests for the Potion class. check for proper naming, descriptions, and valid healingAmount for potion.java
//make sure getHealingAmount returns the correct and valid data
//search for possible mutation bugs that could be discovered by tests

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PotionTest {
	
	@Test
	@DisplayName("Potion: Proper naming and description assignment")
	void testPotionNamingAndDescription() {
		//setup
		String name = "Medium Potion";
		String desc = "Heals a medium amount. Feels good!";
		Potion potion = new Potion(name, desc, 15);
		
		//testing
		assertAll("Verify Item properties are inherited and assigned correctly",
			() -> assertEquals(name, potion.getName(), "Name should match the constructor input"),
			() -> assertEquals(desc, potion.getDescription(), "Description should match the constructor input"),
			() -> assertEquals(name, potion.toString(), "toString should return the item name")
		);
	} //end naming and desc test
	
	@ParameterizedTest
	@CsvSource({
		"Small Potion, Heals a bit of HP, 5",
		"Large Potion, Heals a lot, 25",
		"Literally Water, just water, 0",
		"Bad Potion, You should not drink this, -5"
	})
	@DisplayName("Healing Amount: verify getHealingAmount returns correct and valid data")
	void testGetHealingAmount(String name, String desc, int amount) {
		Potion potion = new Potion(name, desc, amount);
		
		//kill mutations that might result in hardcoded values
		assertEquals(amount, potion.getHealingAmount(), "The healing amount returned must match the constructor input");
	}
	
	@Test
	@DisplayName("Mutation Check: check healing amount is independent and not hardcoded")
	void testHealingAmountIndependence() { 
		Potion small = new Potion("Small Potion", "Desc", 5);
		Potion large = new Potion("Large Potion", "Desc", 25);
		
		//Kills mutants where the field might be shared or the return value is static
		assertNotEquals(small.getHealingAmount(), large.getHealingAmount(), "Different potions must return their specific healing amounts");
	}
	
	@Test
	@DisplayName("Null Checks: ensure null names or descriptions throw NulLPointerException")
	void testNullGuards() {
		//test Potion inherits the null guards from the Item class
		assertAll(
			() -> assertThrows(NullPointerException.class, () -> new Potion(null, "desc", 10)),
			() -> assertThrows(NullPointerException.class, () -> new Potion("name", null, 10)),
		);
	}
	
	@Test
	@DisplayName("Hierarchy: verify Potion implements Usable")
	void testPotionUsable() {
		Potion potion = new Potion("Med Pot", "Someone got lazy with naming", 15);
		assertTrue(potion instanceof Usable, "Potion must implement Usable (idk if it's an interface I haven't checked yet)");
		assertTrue(potion instanceof Item, "Potion must be a subclass of Item");
	}
}
