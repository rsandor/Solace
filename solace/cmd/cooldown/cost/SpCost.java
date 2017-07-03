package solace.cmd.cooldown;

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
   * @see solace.cmd.cooldown.AbstractResourceCost.CostType
   */
  public SpCost(AbstractResourceCost.CostType t, int a) { super(t, a); }

  /**
   * @see solace.cmd.cooldown.AbstractResourceCost
   */
  protected int getPlayerResource(Player p) { return p.getSp(); }

  /**
   * @see solace.cmd.cooldown.AbstractResourceCost
   */
  protected int getPlayerResourceMax(Player p) { return p.getMaxSp(); }

  /**
   * @see solace.cmd.cooldown.ResourceCost
   */
  public void withdraw(Player p) {
    if (!canWithdraw(p)) return;
    p.setSp(getPlayerResource(p) - getCost(p));
  }

  /**
   * @see solace.cmd.cooldown.ResourceCost
   */
  public String getInsufficentResourceMessage() {
    return "Not enough {ysp{x.";
  }
}
