//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestTest {

    private Quest makeQuest() {
        return new Quest("Escape the Basement",
                "Recover the Lost Gradebook and defeat the Final Exam Phantom.");
    }

    @Test
    void nameIsSetCorrectly() {
        assertEquals("Escape the Basement", makeQuest().getName());
    }

    @Test
    void descriptionIsSetCorrectly() {
        Quest q = makeQuest();
        assertEquals("Recover the Lost Gradebook and defeat the Final Exam Phantom.", q.getDescription());
    }

    @Test
    void nullNameThrows() {
        assertThrows(NullPointerException.class, () ->
            new Quest(null, "description")
        );
    }

    @Test
    void nullDescriptionThrows() {
        assertThrows(NullPointerException.class, () ->
            new Quest("name", null)
        );
    }

    //initial state

    @Test
    void questStartsAsNotStarted() {
        assertEquals(QuestStatus.NOT_STARTED, makeQuest().getStatus());
    }

    @Test
    void gradebookNotRecoveredAtStart() {
        assertFalse(makeQuest().isGradebookRecovered());
    }

    @Test
    void phantomNotDefeatedAtStart() {
        assertFalse(makeQuest().isPhantomDefeated());
    }

    @Test
    void questIsNotCompleteAtStart() {
        assertFalse(makeQuest().isComplete());
    }

    //recovering the gradebook

    @Test
    void recoveringGradebookSetsItToTrue() {
        Quest q = makeQuest();
        q.markGradebookRecovered();
        assertTrue(q.isGradebookRecovered());
    }

    @Test
    void recoveringGradebookAloneSetsInProgress() {
        Quest q = makeQuest();
        q.markGradebookRecovered();
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
    }

    @Test
    void recoveringGradebookAloneDoesNotCompleteQuest() {
        Quest q = makeQuest();
        q.markGradebookRecovered();
        assertFalse(q.isComplete());
    }

    //defeating the phantom

    @Test
    void defeatingPhantomSetsItToTrue() {
        Quest q = makeQuest();
        q.markPhantomDefeated();
        assertTrue(q.isPhantomDefeated());
    }

    @Test
    void defeatingPhantomAloneSetsInProgress() {
        Quest q = makeQuest();
        q.markPhantomDefeated();
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
    }

    @Test
    void defeatingPhantomAloneDoesNotCompleteQuest() {
        Quest q = makeQuest();
        q.markPhantomDefeated();
        assertFalse(q.isComplete());
    }

    //completing the quest

    @Test
    void doingBothObjectivesCompletesQuest() {
        Quest q = makeQuest();
        q.markGradebookRecovered();
        q.markPhantomDefeated();
        assertEquals(QuestStatus.COMPLETED, q.getStatus());
        assertTrue(q.isComplete());
    }

    @Test
    void orderDoesNotMatterForCompletion() {
        Quest q = makeQuest();
        q.markPhantomDefeated();
        q.markGradebookRecovered();
        assertTrue(q.isComplete());
    }

    //updateStatus

    @Test
    void updateStatusWithNothingDoneDoesNotChangeStatus() {
        Quest q = makeQuest();
        q.updateStatus();
        assertEquals(QuestStatus.NOT_STARTED, q.getStatus());
    }

    @Test
    void updateStatusCalledExplicitlyStillWorks() {
        Quest q = makeQuest();
        q.markGradebookRecovered();
        q.updateStatus();
        assertEquals(QuestStatus.IN_PROGRESS, q.getStatus());
    }
}
