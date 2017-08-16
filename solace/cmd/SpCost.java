package solace.cmd;

import solace.game.Player;
import solace.game.effect.PlayerEffect;

/**
 * Resource cost for SP.
 * @author Ryan Sandor Richards
 */
public class SpCost extends AbstractResourceCost {
  /**
   * Creates a new sp cost for the given percentage amount.
   * @param a Percentage of max sp for the cost.
   */
  public SpCost(int a) { super(a); }

  /**
   * Creates a new sp cost of the given type and amount.
   * @param t Type for the cost.
   * @param a Amount for the cost.
   * @see CostType
   */
  public SpCost(AbstractResourceCost.CostType t, int a) { super(t, a); }

  @Override
  protected int getPlayerResource(Player p) { return p.getSp(); }

  @Override
  protected int getPlayerResourceMax(Player p) { return p.getMaxSp(); }

  @Override
  public String getInsufficientResourceMessage() {
    return "Not enough {m}sp{x}.";
  }

  @Override
  protected int getCost(Player p) {
    double cost = super.getCost(p);
    for (PlayerEffect effect : p.getEffects()) {
      cost = effect.getModSpCost().modify(p, cost);
    }
    return (int)Math.round(cost);
  }

  @Override
  public void withdraw(Player p) {
    if (!canWithdraw(p)) return;
    p.setSp(getPlayerResource(p) - getCost(p));
  }
}

