package solace.cmd;

import solace.game.Player;
import solace.game.effect.PlayerEffect;

/**
 * Resource cost for HP.
 * @author Ryan Sandor Richards
 */
public class HpCost extends AbstractResourceCost {
  /**
   * Creates a new hp cost for the given percentage amount.
   * @param a Percentage of max hp for the cost.
   */
  public HpCost(int a) { super(a); }

  /**
   * Creates a new hp cost of the given type and amount.
   * @param t Type for the cost.
   * @param a Amount for the cost.
   * @see CostType
   */
  public HpCost(AbstractResourceCost.CostType t, int a) { super(t, a); }

  @Override
  protected int getPlayerResource(Player p) { return p.getHp(); }

  @Override
  protected int getPlayerResourceMax(Player p) { return p.getMaxHp(); }

  @Override
  public String getInsufficientResourceMessage() {
    return "Not enough {m}hp{x}.";
  }

  @Override
  protected int getCost(Player p) {
    double cost = super.getCost(p);
    for (PlayerEffect effect : p.getEffects()) {
      cost = effect.getModHpCost().modify(p, cost);
    }
    return (int)Math.round(cost);
  }

  @Override
  public void withdraw(Player p) {
    if (!canWithdraw(p)) return;
    p.setHp(getPlayerResource(p) - getCost(p));
  }
}
