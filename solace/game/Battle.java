package solace.game;

import com.google.common.collect.*;
import java.util.*;
import solace.util.*;

/**
 * Represents a single battle in the game world.
 * @author Ryan Sandor Richards
 */
public class Battle {
  Set<Player> participants;
  Hashtable<Player, Player> targets;
  Multimap<Player, Player> attackers;

  /**
   * Creates a new, empty, battle.
   */
  public Battle() {
    participants = Collections.synchronizedSet(
      new HashSet<Player>()
    );
    targets = new Hashtable<Player, Player>();
    attackers = Multimaps.synchronizedListMultimap(
      ArrayListMultimap.<Player, Player> create()
    );
  }

  /**
   * Executes a round of the battle.
   */
  public void round() {
    Log.trace("Battle: Synchronizing participants.");

    synchronized (participants) {
      Hashtable<Player, StringBuffer> messageBuffers =
        new Hashtable<Player, StringBuffer>();

      Set<Player> dead = new HashSet<Player>();

      Hashtable<Player, Player> lastAttacker = new Hashtable<Player, Player>();

      for (Player p : participants) {
        messageBuffers.put(p, new StringBuffer());
      }

      for (Player attacker : participants) {
        Player target = targets.get(attacker);
        if (target == null) {
          continue;
        }

        int attackRoll = attacker.getAttackRoll();
        int numberOfAttacks = attacker.getNumberOfAttacks();
        int hitMod = attacker.getHitMod();
        int averageDamage = attacker.getAverageDamage();
        int damageMod = attacker.getDamageMod();
        int ac = target.getAC();

        int damage = 0;
        int hits = 0;

        Log.trace(String.format(
          "Battle: %s attacks %s.",
          attacker.getName(),
          target.getName()));

        Log.trace(Color.format(String.format(
          "%s {rAttack Roll:{x %d vs. %d",
          attacker.getName(), attackRoll, ac)));

        for (int i = 0; i < numberOfAttacks; i++) {
          try {
            int roll = Roll.uniform(attackRoll) + hitMod;
            if (roll > ac) {
              hits++;
              damage += Roll.normal(averageDamage) + damageMod;
            }
          }
          catch (Exception e) {
            Log.error("Error calculating attack roll and damage.");
            e.printStackTrace();
          }
        }

        int actualDamage = target.applyDamage(damage);

        if (hits == 0) {
          messageBuffers.get(attacker).append(String.format(
            "Your attack missed %s.\n\r",
            target.getName()));
          messageBuffers.get(target).append(String.format(
            "%s {gmissed{x you completely!\n\r",
            attacker.getName()));
        }
        else if (hits == 1) {
          messageBuffers.get(attacker).append(String.format(
            "[{g%d{x] You hit %s!\n\r",
            actualDamage, target.getName()));
          messageBuffers.get(target).append(String.format(
            "<{r%d{x> %s hit you!\n\r",
            actualDamage, attacker.getName()));
        }
        else {
          messageBuffers.get(attacker).append(String.format(
            "[{g%d{x] You hit %s %d times!\n\r",
            actualDamage, target.getName(), hits));
          messageBuffers.get(target).append(String.format(
            "<{r%d{x> %s hit you %d times!\n\r",
            actualDamage, attacker.getName(), hits));
        }
      }

      for (Player attacker : participants) {
        Player target = targets.get(attacker);
        if (target != null && target.isDead()) {
          dead.add(target);
          lastAttacker.put(target, attacker);
          messageBuffers.get(attacker).append(String.format(
            "You {Rkilled{x %s!\n\r", target.getName()));
        }
      }

      Log.trace("Cleaning up and sending messages.");

      for (Player p : messageBuffers.keySet()) {
        p.sendMessage(messageBuffers.get(p).toString().trim());
      }

      for (Player p : dead) {
        remove(p);
        p.die(lastAttacker.get(p));
      }
    }
  }

  /**
   * Adds a character or mobile to the battle.
   * @param m Character or mobile to add.
   */
  public void add(Player m) {
    synchronized(participants) {
      participants.add(m);
    }
  }

  /**
   * Removes a character or mobile from the battle.
   * @param m Character or mobile to remove.
   */
  public synchronized void remove(Player m) {
    participants.remove(m);
    attackers.removeAll(m);
    targets.remove(m);
  }

  /**
   * Sets who a given participant is attacking.
   * @param a The attacker.
   * @param b The defender.
   */
  public void setAttacking(Player a, Player b) {
    synchronized(attackers) {
      targets.put(a, b);
      attackers.put(b, a);
    }
  }

  /**
   * @return A collection of participants in the battle.
   */
  public Collection<Player> getParticipants() {
    return Collections.unmodifiableCollection(participants);
  }

  /**
   * @return The number of participants in the battle.
   */
  public int size() {
    synchronized (participants) {
      return participants.size();
    }
  }

  /**
   * Determines if a battle has completed.
   * @return `true` if the battle is over, false otherwise.
   */
  public boolean isOver() {
    return size() < 2;
  }

  /**
   * Returns the target of the given player in the battle.
   * @param  p Player for which to find the target.
   * @return   The target of the player.
   */
  public Player getTargetFor(Player p) {
    return targets.get(p);
  }
}
