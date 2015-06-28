package solace.game;

/**
 * Represents an item in the game world.
 * @author Ryan Sandor Richards
 */
public class Item extends Template {
  public Item() {
    super();
  }

  public Item(String id, String names, Area area) {
    super(id, names, area);
  }

  /**
   * Determines if an item can be equipped. An item is considered equipment if
   * it has a "type" property of "equipment" and a valid equipment "slot"
   * property.
   * @return `true` if the item can be equipped, `false` otherwise.
   */
  public boolean isEquipment() {
    String type = get("type");
    String slot = get("slot");
    if (type == null || !type.equals("equipment") || slot == null) {
      return false;
    }
    return solace.game.Character.isValidEquipmentSlot(slot);
  }
}
