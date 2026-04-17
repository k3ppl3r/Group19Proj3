//Brady
//Prompt:
//tests for QuestTracker. check that picking up the Lost Gradebook marks it recovered,
//that a different item doesn't affect the quest, that null item does nothing,
//that killing the Final Exam Phantom marks it defeated, that a different dead monster
//doesn't count, and that a living phantom doesn't trigger it. also check that getting
//both objectives moves the quest to COMPLETED

package com.example.haunted.rules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.example.haunted.model.BossMonster;
import com.example.haunted.model.Key;
import com.example.haunted.model.Monster;
import com.example.haunted.model.Quest;
import com.example.haunted.model.QuestItem;
import com.example.haunted.model.QuestStatus;

public class QuestTrackerTest {

    private final QuestTracker tracker = new QuestTracker();

    private Quest newQuest() {
        return new Quest("Escape", "Recover the gradebook and defeat the phantom.");
    }

    @Test
    void gradebookMarksRecovered() {
        Quest q = newQuest();
        tracker.updateQuestForItem(q, new QuestItem("Lost Gradebook", "The missing book."));
        assertTrue(q.isGradebookRecovered());
    }

    @Test
    void wrongItemDoesNothing() {
        Quest q = newQuest();
        tracker.updateQuestForItem(q, new Key("Archive Key", "Opens stuff."));
        assertFalse(q.isGradebookRecovered());
    }

    @Test
    void nullItemDoesNothing() {
        Quest q = newQuest();
        tracker.updateQuestForItem(q, null);
        assertFalse(q.isGradebookRecovered());
    }

    @Test
    void phantomDefeatedMarksQuest() {
        Quest q = newQuest();
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(9999);
        tracker.updateQuestForMonster(q, phantom);
        assertTrue(q.isPhantomDefeated());
    }

    @Test
    void livingPhantomDoesNotCount() {
        Quest q = newQuest();
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        tracker.updateQuestForMonster(q, phantom); //still alive
        assertFalse(q.isPhantomDefeated());
    }

    @Test
    void wrongMonsterDoesNothing() {
        Quest q = newQuest();
        Monster ta = new Monster("Sleep-Deprived TA", 18, 6, 1, List.of());
        ta.takeDamage(9999);
        tracker.updateQuestForMonster(q, ta);
        assertFalse(q.isPhantomDefeated());
    }

    @Test
    void nullMonsterDoesNothing() {
        Quest q = newQuest();
        tracker.updateQuestForMonster(q, null);
        assertFalse(q.isPhantomDefeated());
    }

    @Test
    void bothObjectivesCompletesQuest() {
        Quest q = newQuest();
        tracker.updateQuestForItem(q, new QuestItem("Lost Gradebook", "The missing book."));
        BossMonster phantom = new BossMonster("Final Exam Phantom", 40, 10, 4, List.of(), 3);
        phantom.takeDamage(9999);
        tracker.updateQuestForMonster(q, phantom);
        assertEquals(QuestStatus.COMPLETED, q.getStatus());
    }

    @Test
    void oneObjectiveSetsInProgress() {
        Quest q = newQuest();
        tracker.updateQuestForItem(q, new QuestItem("Lost Gradebook", "The missing book."));
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
    }
}
