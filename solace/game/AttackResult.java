package solace.game;

/**
 * Enumerates all possibe results of a roll to hit during an attack.
 * @author Ryan Sandor Richards
 */
public enum AttackResult {
  MISS, HIT, CRITICAL;

  public boolean isCritical() {
    return this == CRITICAL;
  }

  public boolean isHit() {
    return this == HIT || this == CRITICAL;
  }

  public boolean isMiss() {
    return this == MISS;
  }
};
