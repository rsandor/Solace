package solace.game;

import solace.game.*;
import solace.util.*;

/**
 * Represents a "damage over time" (DoT) buff in the game world.
 * @author Ryan Sandor Richards.
 */
public class DotBuff extends Buff {
  private int averageDamage;
  private Player target;
  private Clock.Event tickInterval;
  private String damageMessage;
  private int frequency;

  /**
   * Creates a DoT buff.
   * @param name Name of the buff.
   * @param t Player affected by the shock debuff.
   * @param avgDam Average damage the debuff deals per tick.
   * @param duration Number of seconds the buff lasts.
   * @param freq How many ticks between damage applications.
   * @param msg Parameterized damage message.
   */
  public DotBuff(
    String name,
    Player t,
    int avgDam,
    int duration,
    int freq,
    String msg
  ) {
    super(name, duration);
    target = t;
    averageDamage = avgDam;
    frequency = Math.max(1, freq);
    damageMessage = msg;
  }

  /**
   * Applies damage to the target each tick.
   */
  private void applyShockDamage() {
    int damage = Roll.normal(averageDamage);
    target.applyDamage(damage);
    target.sendMessage(String.format(damageMessage, damage));
    if (target.isDead()) {
      target.die(null);
    }
  }

  /**
   * @see solace.game.Buff
   */
  public void scheduleTickAction() {
    String intervalName = String.format(
      "DoT (%s) for %s", getName(), target.getName());
    tickInterval = Clock.getInstance().interval(
      intervalName, frequency, new Runnable() {
        public void run() { applyShockDamage(); }
      });
  }

  /**
   * @see solace.game.Buff
   */
  public void cancelTickAction() {
    if (tickInterval != null) {
      tickInterval.cancel();
    }
  }
}
