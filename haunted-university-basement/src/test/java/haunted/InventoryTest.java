//Brady

package haunted;

import com.example.haunted.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class InventoryTest {

    private Inventory makeInventory() {
        return new Inventory(3);
    }

    @Test
    void capacityIsSetCorrectly() {
        Inventory inv = new Inventory(5);
        assertEquals(5, inv.getCapacity());
    }

    @Test
    void newInventoryIsNotFull() {
        assertFalse(makeInventory().isFull());
    }

    @Test
    void inventoryIsFullAtCapacity() {
        Inventory inv = makeInventory();
        inv.addItem(new Potion("p1", "d", 1));
        inv.addItem(new Potion("p2", "d", 1));
        inv.addItem(new Potion("p3", "d", 1));
        assertTrue(inv.isFull());
    }

    @Test
    void addingItemWhenNotFullReturnsTrue() {
        Inventory inv = makeInventory();
        assertTrue(inv.addItem(new Potion("Coffee", "Hot", 10)));
    }

    @Test
    void addingItemWhenFullReturnsFalse() {
        Inventory inv = makeInventory();
        inv.addItem(new Potion("p1", "d", 1));
        inv.addItem(new Potion("p2", "d", 1));
        inv.addItem(new Potion("p3", "d", 1));
        assertFalse(inv.addItem(new Potion("p4", "d", 1)));
    }

    @Test
    void itemAddedToFullInventoryDoesNotAppearInList() {
        Inventory inv = makeInventory();
        inv.addItem(new Potion("p1", "d", 1));
        inv.addItem(new Potion("p2", "d", 1));
        inv.addItem(new Potion("p3", "d", 1));
        inv.addItem(new Potion("overflow", "d", 1));
        assertEquals(3, inv.getItems().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 10})
    void inventoryBecomesFullAfterAddingExactCapacity(int capacity) {
        Inventory inv = new Inventory(capacity);
        for (int i = 0; i < capacity; i++) {
            inv.addItem(new Potion("p" + i, "d", 1));
        }
        assertTrue(inv.isFull());
    }

    //finding items

    @Test
    void canFindItemThatWasAdded() {
        Inventory inv = makeInventory();
        inv.addItem(new Key("Archive Key", "Opens archive"));
        assertTrue(inv.findItem("Archive Key").isPresent());
    }

    @Test
    void findItemIsCaseInsensitive() {
        Inventory inv = makeInventory();
        inv.addItem(new Key("Archive Key", "Opens archive"));
        assertTrue(inv.findItem("archive key").isPresent());
        assertTrue(inv.findItem("ARCHIVE KEY").isPresent());
    }

    @Test
    void findItemReturnsEmptyWhenNotPresent() {
        assertTrue(makeInventory().findItem("Nonexistent Item").isEmpty());
    }

    @Test
    void findItemReturnsCorrectObject() {
        Inventory inv = makeInventory();
        Weapon sword = new Weapon("Sword", "Sharp", 5);
        inv.addItem(sword);
        assertSame(sword, inv.findItem("Sword").get());
    }

    //contains

    @Test
    void containsReturnsTrueForAddedItem() {
        Inventory inv = makeInventory();
        inv.addItem(new Key("Vault Key", "Opens vault"));
        assertTrue(inv.contains("Vault Key"));
    }

    @Test
    void containsReturnsFalseForMissingItem() {
        assertFalse(makeInventory().contains("Missing Item"));
    }

    //removing items

    @Test
    void removingItemReturnsIt() {
        Inventory inv = makeInventory();
        Potion p = new Potion("Elixir", "Strong", 20);
        inv.addItem(p);
        assertSame(p, inv.removeItem("Elixir"));
    }

    @Test
    void removingItemTakesItOutOfInventory() {
        Inventory inv = makeInventory();
        inv.addItem(new Potion("Elixir", "Strong", 20));
        inv.removeItem("Elixir");
        assertFalse(inv.contains("Elixir"));
    }

    @Test
    void removingItemThatDoesntExistReturnsNull() {
        assertNull(makeInventory().removeItem("Ghost Item"));
    }

    @Test
    void removingItemFreesUpSpace() {
        Inventory inv = makeInventory();
        inv.addItem(new Potion("p1", "d", 1));
        inv.addItem(new Potion("p2", "d", 1));
        inv.addItem(new Potion("p3", "d", 1));
        inv.removeItem("p1");
        assertTrue(inv.addItem(new Potion("p4", "d", 1)));
    }

    //getItems

    @Test
    void getItemsReflectsWhatWasAdded() {
        Inventory inv = makeInventory();
        Potion p = new Potion("Coffee", "Hot", 10);
        inv.addItem(p);
        assertEquals(1, inv.getItems().size());
        assertSame(p, inv.getItems().get(0));
    }

    @Test
    void getItemsListIsUnmodifiable() {
        Inventory inv = makeInventory();
        assertThrows(UnsupportedOperationException.class, () ->
            inv.getItems().add(new Potion("x", "x", 1))
        );
    }
}
