package solace.cmd.cost;

import solace.game.Player;

/**
 * Resource cost for stamina points.
 * @author Ryan Sandor Richards
 */
public class SpCost extends AbstractResourceCost {
  /**
   * Creates a new sp cost for the given percentage amount.
   * @param a Percentage of max mp for the cost.
   */
  public SpCost(int a) { super(a); }

  /**
   * Creates a new sp cost of the given type and amount.
   * @param t Type for the cost.
   * @param a Amount for the cost.
   * @see CostType
   */
  public SpCost(AbstractResourceCost.CostType t, int a) { super(t, a); }

  /**
   * @see AbstractResourceCost
   */
  protected int getPlayerResource(Player p) { return p.getSp(); }

  /**
   * @see AbstractResourceCost
   */
  protected int getPlayerResourceMax(Player p) { return p.getMaxSp(); }

  /**
   * @see ResourceCost
   */
  public void withdraw(Player p) {
    if (!canWithdraw(p)) return;
    p.setSp(getPlayerResource(p) - getCost(p));
  }

  /**
   * @see ResourceCost
   */
  public String getInsufficentResourceMessage() {
    return "Not enough {y}sp{x}.";
  }
}
