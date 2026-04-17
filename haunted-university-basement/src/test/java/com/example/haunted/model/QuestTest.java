//Hannah Huang
//prompt:
//tests for Quest.java (things involving QuestStatus are lumped in here because it is just an enum)
//check for proper naming and description assignment
//check that markGradebookRecovered and markPhantomDefeated update those values accordingly
//check that isGradebookRecovered and isPhantomDefeated return correct and valid data
//check that a Quest cannot have a null name or description
//search for possible mutation bugs that could be discovered by tests

//check that Quest Status is updated correctly, and isComplete returns correct and valid data
//search for possible mutation bugs that could be discovered by tests


package com.example.haunted.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class QuestTest {
	
	@Test
	@DisplayName("Quest: proper naming and description assignment")
	void testQuestNamingAndDescription() {
		//setup
		String name = "First Quest";
		String desc = "Get the gradebook";
		Quest quest = new Quest(name, desc);
		
		//testing
		assertAll("Verify Quest properties are inherited and assigned correctly",
			() -> assertEquals(name, quest.getName(), "Name should match the constructor input"),
			() -> assertEquals(desc, quest.getDescription(), "Description should match the constructor input")
		);
	}
	
	//getting info tests
	@Test
	@DisplayName("Get (Phantom): verify isPhantomDefeated returns correct and valid info")
	void testIsPhantomDefeated() {
		Quest quest = new Quest("Phantom Quest", "Defeat the Phantom");
		
		//might need to change this to being not equal to true if this doesn't work
		assertEquals(false, quest.isPhantomDefeated(), "isPhantomDefeated should be false in its initial state");
	}
	@Test
	@DisplayName("Get (Gradebook): verify isGradebookRecovered returns correct and valid info")
	void testIsGradebookRecovered() {
		Quest quest = new Quest("Gradebook Quest", "Get the gradebook");
		
		//same as above
		assertEquals(false, quest.isGradebookRecovered(), "isGradebookRecovered should return false in its initial state");
	}
	@Test
	@DisplayName("Get (Status): verify getStatus returns correct and valid info")
	void testGetStatus() {
		Quest quest = new Quest("name", "desc");
		
		assertEquals(QuestStatus.NOT_STARTED, quest.getStatus(), "Quest status should be Not Started in its initial state");
	}
	
	//marking tests
	@Test
	@DisplayName("Mark Complete (Phantom): verify markPhantomDefeated works correctly and updates the status accordingly")
	void testMarkPhantomDefeated() {
		Quest quest = new Quest("Phantom defeat", "Phantom defeated");
		quest.markPhantomDefeated();
		
		assertAll(
			() -> assertEquals(true, quest.isPhantomDefeated(), "phantomDefeated should be true"),
			() -> assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus(), "Quest Status should be in progress")
		);
	}
	@Test
	@DisplayName("Mark Complete (Gradebook): verify markGradebookRecovered works correctly and updates the status accordingly")
	void testMarkGradebookRecovered() {
		Quest quest = new Quest("Gradebook recover", "Gradebook recovered");
		quest.markGradebookRecovered();
		
		assertAll(
			() -> assertEquals(true, quest.isGradebookRecovered(), "gradebookRecovered should be true"),
			() -> assertEquals(QuestStatus.IN_PROGRESS, quest.getStatus(), "Quest Status should be in progress")
		);
	}
	
	//mutations
	@Test
	@DisplayName("Mutation Check (Phantom): check isPhantomDefeated is not hardcoded")
	void testMutationPhantom() {
		Quest notDefeat = new Quest("Kill Phantom", "Defeat the Phantom");
		Quest yesDefeat = new Quest("Phantom is dead", "the phantom is dead");
		yesDefeat.markPhantomDefeated();
		
		assertNotEquals(notDefeat.isPhantomDefeated(),yesDefeat.isPhantomDefeated(), "Different quests must return their specific Phantom Defeated statuses");
	}
	@Test
	@DisplayName("Mutation Check (Gradebook): check isGradebookRecovered is not hardcoded")
	void testMutationGradebook() {
		Quest notRecovered = new Quest("Get gradebook", "get the gradebook");
		Quest yesRecovered = new Quest("Gradebook got", "gradebook recovered");
		yesRecovered.markGradebookRecovered();
		
		assertNotEquals(notRecovered.isGradebookRecovered(), yesRecovered.isGradebookRecovered(), "Different quests must return their specific Gradebook Recovered statuses");
	}
	
	@Test
	@DisplayName("Null Checks: ensure null names or descriptions throw NullPointerException")
	void testNullChecks() {
		assertAll(
			() -> assertThrows(NullPointerException.class, () -> new Quest(null, "desc")),
			() -> assertThrows(NullPointerException.class, () -> new Quest("name", null))
		);
	}
	
	@ParameterizedTest
	@CsvSource({
		"Not Started, 0, false",
		"Done Phantom, 1, false",
		"Done Gradebook, 2, false",
		"Full Complete, 3, true"
	})
	@DisplayName("Completion: verify isComplete returns correct and valid data")
	void testIsComplete(String name, int whatComplete, boolean expected) {
		Quest testQuest = new Quest(name, "desc");
		
		switch(whatComplete) {
			case 0 -> {
                }
			case 1 -> testQuest.markPhantomDefeated();
			case 2 -> testQuest.markGradebookRecovered();
			case 3 -> {
                            testQuest.markPhantomDefeated();
                            testQuest.markGradebookRecovered();
                }
		}
		
		assertEquals(expected, testQuest.isComplete(), "isComplete did not return the expected value");
	}
	
}