package solace.game;

/**
 * Represents a type of damage in the game world.
 * @author Ryan Sandor Richards
 */
public class DamageType {
  private static String CATEGORY_PHYSICAL = "physical";
  private static String CATEGORY_MAGICAL = "magical";

  private String name;
  private String category;

  /**
   * Creates a new damage type.
   * @param n Name of the type.
   * @param c Category for the type.
   */
  public DamageType(String n, String c) {
    name = n;
    category = c;
  }

  /**
   * @return The name of the damage type.
   */
  public String getName() { return name; }

  /**
   * @return The category for the damage type.
   */
  public String getCategory() { return category; }

  /**
   * @return `true` if this is a physical damage type.
   */
  public boolean isPhysical() { return category.equals(CATEGORY_PHYSICAL); }

  /**
   * @return `true` if this is a physical damage type.
   */
  public boolean isMagical() { return category.equals(CATEGORY_MAGICAL); }
}
