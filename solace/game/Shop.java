package solace.game;

import solace.util.*;
import java.util.*;
import java.text.DecimalFormat;

/**
 * Represents a shop in the game world.
 * @author Ryan Sandor Richards
 */
public class Shop {
  Room room;
  List<ShopItem> items = new ArrayList<ShopItem>();
  String id = null;
  String name = "";
  Mobile owner = null;
  double buyMultiplier = 0.5;
  double sellMultiplier = 1.5;

  /**
   * Creates a new shop.
   * @param i The id of the shop.
   * @param n The name of the shop.
   * @param r Room the shop inhabits.
   */
  public Shop(String i, String n, Room r) {
    id = i;
    name = n;
    room = r;
  }

  /**
   * Initializes the shop, sets restocking intervals, and sets the owner mobile.
   */
  public void initialize() {
    // Do not initialize twice
    if (owner != null) {
      return;
    }

    // 1. Find the owner mobile in the room
    for (Mobile mob : room.getMobiles()) {
      String shopOwner = mob.get("shop-owner");
      if (shopOwner != null && shopOwner.equals(id)) {
        owner = mob;
        break;
      }
    }

    // 2. Initialize the shop items in stock
    for (ShopItem item : items) {
      item.initialize();
    }
  }

  /**
   * Generates a list of the items in this shop.
   * @return [description]
   */
  public String list() {
    StringBuffer b = new StringBuffer();

    // Shop title
    b.append(Strings.shopBanner(name));
    b.append(String.format(
      "| %-9s %11s   %-43s %-8s |\n\r",
      "Inv#", "Price", "Name", "Qty"
    ));
    b.append(Strings.RULE);

    DecimalFormat dec = new DecimalFormat("#,###,###");

    int index = 0;
    for (ShopItem item : items) {
      try {
        Item sample = item.getSampleItem();

        // This could happen if a reference to a game item was not found by the
        // TemplateManager. This item is defunkt, and we should just skip it.
        if (sample == null) {
          continue;
        }

        int quantity = item.getQuantity();

        b.append(String.format(
          "| {c%-9s{x {y%11s{x   %-43s {m%-8s{x |\n\r",
          "[" + (++index) + "]",
          sellPrice(item) + "g",
          sample.get("description.inventory"),
          (quantity > 0) ? "" + quantity : "--"
        ));
      }
      catch (TemplateNotFoundException e) {
        // This shouldn't happen because of the check above but here for safety.
        Log.error("Shop.list: Item could not be found");
        e.printStackTrace();
      }
    }

    b.append(Strings.RULE);

    return b.toString();
  }

  /**
   * Determines how much an item sells for.
   * @param item Item to be sold.
   * @return The price for the item in gold.
   * @throws TemplateNotFoundException If the itemId for this store item is
   *   invalid.
   */
  public long sellPrice(ShopItem item)
    throws TemplateNotFoundException
  {
    Item sample = item.getSampleItem();

    // This could happen if the item was not correctly initia
    if (sample == null) {
      return -1;
    }

    String value = sample.get("value");
    if (value == null) {
      value = "1";
    }
    return (long)(sellMultiplier * Long.parseLong(value));
  }

  /**
   * @return The multiplier applied to buying items from players.
   */
  public double getBuyMultiplier() {
    return buyMultiplier;
  }

  /**
   * Sets the buy multiplier for the shop.
   * @param d The multiplier to be applied when buying items from players.
   */
  public void setBuyMultiplier(double d) {
    buyMultiplier = d;
  }

  /**
   * @return The multiplier applied to sellng items to players.
   */
  public double getSellMultiplier() {
    return sellMultiplier;
  }

  /**
   * Sets the sell multiplier for the shop.
   * @param d The multiplier to be applied when selling items to players.
   */
  public void setSellMultiplier(double d) {
    sellMultiplier = d;
  }

  /**
   * Sets the room for the shop.
   * @param r Room to set.
   */
  public void setRoom(Room r) {
    room = r;
  }

  /**
   * Get's the room the shop inhabits.
   * @return The room for the shop.
   */
  public Room getRoom() {
    return room;
  }

  /**
   * Gets a shop item with the associated index.
   * @param index ShopItem index for the item.
   * @return The store item at the given index, or null if the index is out of
   *   bounds.
   */
  public ShopItem getItemByIndex(int index) {
    index--;
    if (index < 0 || index >= items.size()) {
      return null;
    }
    return items.get(index);
  }

  /**
   * Finds an item store item with the given name.
   * @param  name Name of the item
   * @return      The item with the given name, or null if none was found.
   */
  public ShopItem getItemByName(String name) {
    for (ShopItem item : items) {
      Item sample = item.getSampleItem();
      if (sample == null) {
        continue;
      }
      if (sample.hasName(name)) {
        return item;
      }
    }
    return null;
  }

  /**
   * Adds a item and puts it up for sale in the shop.
   * @param i Item to add.
   */
  public void addItem(ShopItem i) {
    items.add(i);
  }

  /**
   * Removes an item from sale at the shop.
   * @param i Item to remove.
   */
  public void removeItem(ShopItem i) {
    items.remove(i);
  }

  /**
   * @return The name of the shop.
   */
  public String getName() {
    return name;
  }
}
