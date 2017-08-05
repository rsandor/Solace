package solace.game;

import solace.io.WeaponProficiencies;

import java.util.HashSet;
import java.util.Set;

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

  /**
   * Gets all damage types for this item if it is a weapon.
   * @return The damage types for the item if it is a weapon with an associated
   *   proficiency, an empty set if it is not.
   */
  public Set<DamageType> getDamageTypes() {
    Set<DamageType> types = new HashSet<>();
    String slot = get("slot");
    if (slot == null || !slot.equals("weapon")) {
      return types;
    }
    String profName = get("proficiency");
    if (profName == null || !WeaponProficiencies.getInstance().has(profName)) {
      return types;
    }
    WeaponProficiency prof = WeaponProficiencies.getInstance().get(profName);
    types.addAll(prof.getDamageTypes());
    return types;
  }

  /**
   * Returns the XML describing the item (primarily used to save character
   * inventory and equipment).
   * @return The XML describing the item.
   */
  public String getXML() {
    StringBuffer b = new StringBuffer();

    StringBuffer names = new StringBuffer();
    for (String name : getNames()) {
      names.append(name + " ");
    }

    b.append(String.format(
      "<item uuid=\"%s\" id=\"%s\" names=\"%s\" area=\"%s\">",
      getUUID(),
      getId(),
      names.toString().trim(),
      getArea().getId()
    ));

    for (String key : getProperties().keySet()) {
      b.append(String.format(
        "<property key=\"%s\">%s</property>", key, get(key)
      ));
    }

    b.append("</item>");

    return b.toString();
  }
}
