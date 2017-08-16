package solace.cmd;

import solace.game.Player;
import solace.game.effect.PlayerEffect;
import solace.util.Log;

/**
 * Resource cost for MP.
 * @author Ryan Sandor Richards
 */
public class MpCost extends AbstractResourceCost {
  /**
   * Creates a new mp cost for the given percentage amount.
   * @param a Percentage of max mp for the cost.
   */
  public MpCost(int a) { super(a); }

  /**
   * Creates a new mp cost of the given type and amount.
   * @param t Type for the cost.
   * @param a Amount for the cost.
   * @see CostType
   */
  public MpCost(AbstractResourceCost.CostType t, int a) { super(t, a); }

  @Override
  protected int getPlayerResource(Player p) { return p.getMp(); }

  @Override
  protected int getPlayerResourceMax(Player p) { return p.getMaxMp(); }

  @Override
  public String getInsufficientResourceMessage() {
    return "Not enough {m}mp{x}.";
  }

  @Override
  protected int getCost(Player p) {
    double cost = super.getCost(p);
    Log.info(String.format("BEFORE: %d", (int)cost));
    for (PlayerEffect effect : p.getEffects()) {
      cost = effect.getModMpCost().modify(p, cost);
    }
    Log.info(String.format("AFTER: %d", (int)Math.round(cost)));
    return (int)Math.round(cost);
  }

  @Override
  public void withdraw(Player p) {
    if (!canWithdraw(p)) return;
    p.setMp(getPlayerResource(p) - getCost(p));
  }
}

