package solace.game;

import java.util.*;
import solace.util.*;
import solace.game.*;

/**
 * Represents a single item for sale in a shop.
 * @author Ryan Sandor Richards.
 */
public class ShopItem {
  // Instance variables
  Shop shop = null;
  String itemId = "";
  int maxQuantity = -1;
  int quantity = -1;
  int restockInterval = -1;
  int restockFreq = -1;
  int restockAmount = -1;
  Clock.Event restockEvent = null;
  Item sample = null;

  /**
   * Creares a new shop item.
   * @param s Shop that owns the item.
   * @param id Id of the template for the item.
   */
  public ShopItem(Shop s, String id) {
    shop = s;
    itemId = id;
  }

  /**
   * Prepares the item to be sold. Sets a sample item from the template factory
   * and schedules the restock interval if applicable. This method should only
   * be called once all of the game areas have been loaded.
   *
   * Note: this method can only be called once, every subsequent call will be
   * ignored.
   */
  public void initialize() {
    // Don't initialize twice
    if (sample != null) {
      return;
    }

    try {
      sample = getInstance();
      registerRestockEvent();
    }
    catch (TemplateNotFoundException e) {
      Log.error("ShopItem: Unable to find template for item: " + itemId);
    }
  }

  /**
   * @return a new instance of the item represented by this shop item.
   * @throws TemplateNotFoundException If no item with the given id exists.
   */
  public Item getInstance()
    throws TemplateNotFoundException
  {
    return TemplateFactory.getInstance().getItem(itemId);
  }

  /**
   * Returns a sample item
   * @return [description]
   */
  public Item getSampleItem() {
    return sample;
  }

  /**
   * Restocks the current quantity of the item.
   */
  protected void restock() {
    // No need to restock if the quantity is at its maximum
    if (quantity >= maxQuantity) {
      return;
    }

    // Handle restock frequency
    if (
      restockFreq > 0 &&
      (new Random().nextInt(100) >= restockFreq)
    ) {
      return;
    }

    Log.trace("Restocking item " + itemId + " for shop " + shop);

    if (restockAmount < 1) {
      quantity = maxQuantity;
    }
    else {
      quantity = Math.min(quantity + restockAmount, maxQuantity);
    }
  }

  /**
   * Registers a restock event for the item with game clock.
   */
  public void registerRestockEvent() {
    // Don't double register the restock event.
    if (restockEvent != null) {
      return;
    }

    if (restockInterval < 1) {
      return;
    }

    // Sets a minimum restock interval. This is a double check for the setter.
    if (restockInterval < 600) {
      restockInterval = 600;
    }

    // Register the event with the game clock
    restockEvent = Clock.getInstance().interval(
      "shop.restock",
      restockInterval,
      new Runnable() {
        public void run() {
          restock();
        }
      }
    );
  }

  /**
   * Unregisters the item's restocking event from the master game clock. This
   * method should be called anytime a shop is reloaded. Failure to do so will
   * result in dangling references to shop items that can no longer impact the
   * game in any way (a memory leak leading to reduce performance).
   */
  public void unregisterRestockEvent() {
    if (restockEvent != null) {
      restockEvent.cancel();
    }
  }

  /**
   * Sets the item id for the shop item.
   * @param id Id to set.
   */
  public void setItemId(String id) {
    itemId = id;
  }

  /**
   * @return The item id for the shop item.
   */
  public String getItemId() {
    return itemId;
  }

  /**
   * Sets the maximum quantity for the item in the shop.
   * @param m The maximum quantity to set.
   */
  public void setMaxQuantity(int m) {
    maxQuantity = m;
  }

  /**
   * @return The maximum quantity for the item.
   */
  public int getMaxQuantity() {
    return maxQuantity;
  }

  /**
   * Sets the current quantity for the item.
   * @param q Quantity to set.
   */
  public void setQuantity(int q) {
    quantity = q;
  }

  /**
   * @return the current quantity for the item.
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Determines if there is at least the given number of items for sale.
   * @param n Number of items to check.
   * @return `true` if there are at least that number remaining, `false`
   *   otherwise.
   */
  public boolean hasQuantity(int n) {
    return quantity == -1 || quantity >= n;
  }

  /**
   * Sets the restock interval for the item.
   * @param i Interval period to set.
   */
  public void setRestockInterval(int i) {
    // TODO Set minimum restock time as an option in world.txt
    restockInterval = (i < 1 || i >= 600) ? i : 600;
  }

  /**
   * @return The restock interval in game ticks.
   */
  public int getRestockInterval() {
    return restockInterval;
  }

  /**
   * Sets the restock frequency for the item.
   * @param i Interval period to set.
   */
  public void setRestockFrequency(int f) {
    restockFreq = f;
  }

  /**
   * @return The restock frequency in game ticks.
   */
  public int getRestockFrequency() {
    return restockFreq;
  }

  /**
   * Sets the restock amount for the item.
   * @param i Interval period to set.
   */
  public void setRestockAmount(int a) {
    restockAmount = a;
  }

  /**
   * @return The restock amount in game ticks.
   */
  public int getRestockAmount() {
    return restockAmount;
  }
}
