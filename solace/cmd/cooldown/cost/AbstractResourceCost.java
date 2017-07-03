package solace.cmd.cooldown;

import solace.game.Player;

/**
 * Abstract cost type that allows for fixed and percentage based resource costs.
 * @author Ryan Sandor Richards
 */
public abstract class AbstractResourceCost implements ResourceCost {
  /**
   * Enumerates basic types of resource costs.
   */
  public enum CostType { PERCENTAGE, FIXED };

  CostType type = CostType.PERCENTAGE;
  int amount = 0;

  /**
   * Creates a new percentage based cost for the given amount.
   * @param a Amount for the resource cost.
   */
  public AbstractResourceCost(int a) {
    amount = a;
  }

  /**
   * Creates a new abstract resource cost of the given type and amount.
   * @param t Type for the resource cost.
   * @param a Amount for the resource cost.
   */
  public AbstractResourceCost(CostType t, int a) {
    type = t;
    amount = a;
  }

  /**
   * Determines the current amount of the resource currently possessed by the
   * given player.
   * @param p Player for which to fetch the resource.
   * @return The amount of the resource the player currently possesses.
   */
  protected abstract int getPlayerResource(Player p);

  /**
   * Determines the current amount of the resource currently possessed by the
   * given player.
   * @param p Player for which to fetch the resource.
   * @return The amount of the resource the player currently possesses.
   */
  protected abstract int getPlayerResourceMax(Player p);

  /**
   * Determines a fixed total cost for the given player.
   * @param p Player for which to determine the total cost.
   * @return The fixed total cost to that player.
   */
  protected int getCost(Player p) {
    return (type == CostType.PERCENTAGE) ?
      (int)((amount / 100.0) * getPlayerResourceMax(p)) :
      amount;
  }

  /**
   * @see solace.cmd.cooldown.ResourceCost
   */
  public boolean canWithdraw(Player p) {
    return getPlayerResource(p) >= getCost(p);
  }

  /**
   * @see solace.cmd.cooldown.ResourceCost
   */
  public abstract void withdraw(Player p);

  /**
   * @see solace.cmd.cooldown.ResourceCost
   */
  public abstract String getInsufficentResourceMessage();
}
