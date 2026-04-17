//Hannah Huang

package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TrapTest {
	//I'm not 100% sure if this is needed but eh why not
	@Test
	@DisplayName("Trap: proper naming and information assignments")
	void testNamingAndDescription() {
		String name = "Wires"; //only doing this as a seperate variable because frankly I don't want to mess around with making a TrapType variable
		Trap trap = new Trap(name, TrapType.ELECTRIC, 5, true, true);
		
		assertAll("Verify properties are assigned correctly",
			() -> assertEquals(name, trap.getName(), "Name should match constructor input"),
			() -> assertEquals(true, trap.isArmed(), "Whether the trap is armed should match constructor input"),
			() -> assertEquals(true, trap.isOneTimeTrigger(), "Whether the trap is a one time trigger should match constructor input")
		);
	}
	
	@ParameterizedTest
	@CsvSource({
		"Wires 2, 10",
		"Stupid Big Bug Zapper, 100",
		"Carpet Static, 0",
		"Rule of God, 999"
	})
	@DisplayName("Damage: verify getDamage returns correct and valid data")
	void testGetDamage(String name, int amount) {
		Trap trap = new Trap(name, TrapType.ELECTRIC, amount, true, true);
		
		assertEquals(amount, trap.getDamage(), "Damage should equal constructor input");
	}
	
	@Test
	@DisplayName("Type: verify getType returns correct and valid information")
	void testGetType() {
		Trap electric = new Trap("Electric", TrapType.ELECTRIC, 5, true, true);
		Trap steam = new Trap("Steam", TrapType.STEAM, 5, true, true);

		assertEquals(TrapType.ELECTRIC, electric.getType(), "Type should match constructor input");
		assertEquals(TrapType.STEAM, steam.getType(), "Type should match constructor input");
	}
	
	@Test
	@DisplayName("Armed: verify isArmed returns correct and valid information")
	void testIsArmed() {
		Trap noArm = new Trap("not armed", TrapType.ELECTRIC, 5, false, true);
		Trap yesArm = new Trap("armed", TrapType.ELECTRIC, 5, true, true);
		
		assertAll(
			() -> assertEquals(false, noArm.isArmed(), "Whether the trap is armed should match the constructor input (armed = false"),
			() -> assertEquals(true, yesArm.isArmed(), "Whether the trap is armed should match constructor input (armed = true)")
		);
	}
	
	@Test
	@DisplayName("One Time Trigger: verify isOneTimeTrigger returns correct and valid information")
	void testIsOneTimeTrigger() {
		Trap noOTT = new Trap("multiple times", TrapType.ELECTRIC, 5, true, false);
		Trap yesOTT = new Trap("one time", TrapType.ELECTRIC, 5, true, true);
		
		assertAll(
				() -> assertEquals(false, noOTT.isOneTimeTrigger(), "Whether the trap is one time trigger should match the constructor input (OTT = false"),
				() -> assertEquals(true, yesOTT.isOneTimeTrigger(), "Whether the trap is one time trigger should match constructor input (OTT = true)")
		);
	}
	
	@Test
	@DisplayName("Null Checks: verify that null name or type throws NullPointerException")
	void testNullGuards() {
		assertAll(
			() -> assertThrows(NullPointerException.class, () -> new Trap("name", null, 5, true, true)),
			() -> assertThrows(NullPointerException.class, () -> new Trap(null, TrapType.ELECTRIC, 5, true, true))
		);	
	}
	
	@Test
	@DisplayName("Mutation (damage): check damage is independent and not hardcoded")
	void testDamageIndependence() {
		Trap small = new Trap("small", TrapType.ELECTRIC, 5, true, true);
		Trap big = new Trap("big", TrapType.ELECTRIC, 25, true, true);
		
		assertNotEquals(small.getDamage(), big.getDamage(), "Different traps must return their specific damage amount");
	}
	
	@Test
	@DisplayName("Mutation (type): check trap type is independent and not hardcoded")
	void testTypeIndependence() {
		Trap ele = new Trap("electric", TrapType.ELECTRIC, 5, true, true);
		Trap steam = new Trap("steam", TrapType.STEAM, 5, true, true);
		
		assertNotEquals(ele.getType(), steam.getType(), "Different traps must return their specific types");
	}
	
	@Test
	@DisplayName("Disarm: verify disarm function works as intended")
	void testDisarm() {
		Trap trap = new Trap("big", TrapType.ELECTRIC, 25, true, true);
		trap.disarm();
		
		assertEquals(false, trap.isArmed(), "The trap should be disarmed after the disarm function is called");
	}
}
