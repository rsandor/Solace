package solace.cmd;

import solace.game.Player;

/**
 * Set of behaviors that define a resource cost for any cooldown action in the
 * game. For instance, spells may have an associated MP resource cost, actions
 * perhaps require SP, etc.
 * @author Ryan Sandor Richards
 */
public interface ResourceCost {
  /**
   * Determines if a player has required amount of the given resource to pay
   * the cost.
   * @param p Player to test.
   * @return True if the player can pay the cost, false otherwise.
   */
  public boolean canWithdraw(Player p);

  /**
   * Withdraws the specified amount of resources from the given player.
   * @param p Player to pay the cost.
   */
  public void withdraw(Player p);

  /**
   * @return A message to indicate that tells the player they do not possess
   *   the resources required to take an action (e.g. "Not enough mp.", etc.).
   */
  public String getInsufficientResourceMessage();
}
