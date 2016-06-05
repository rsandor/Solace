package solace.game;

import com.google.common.collect.*;
import java.util.*;
import solace.util.*;

/**
 * Represents a single battle in the game world.
 * @author Ryan Sandor Richards
 */
public class Battle {
  /**
   * Chance that any given attack roll will be a critical hit.
   */
  public static final double CRITICAL_CHANCE = 0.05;

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
   * Determines if a defending player successfully parries an attack.
   * @param defender The player defending an attack.
   * @return True if the attack is parried, false otherwise.
   */
  public static boolean parry(Player defender) {
    int skillLevel = defender.getMaximumSkillLevelForPassive("parry");
    if (skillLevel < 1) {
      return false;
    }
    double chance = 0.05 + 0.1 * ((double)skillLevel / 100.0);
    return Roll.uniform() < chance;
  }

  /**
   * Roll to hit with normal scale potency.
   * @see Battle.rollToHit(Player, Player, int)
   */
  public static AttackResult rollToHit(Player attacker, Player defender) {
    return rollToHit(attacker, defender, 100);
  }

  /**
   * Performs a roll to see if an attacker hits a defender with a basic attack.
   * @param attacker The attacking player.
   * @param defender The defending player.
   * @param potency Potency of the attack (scales the attacker roll).
   * @return True if the attack succeeds, false otherwise.
   */
  public static AttackResult rollToHit(
    Player attacker,
    Player defender,
    int potency
  ) {
    int ac = defender.getAC();
    double attackRoll = (double)attacker.getAttackRoll();
    int hitMod = attacker.getHitMod();

    // Make the roll
    double roll = attackRoll * Roll.uniform() + 1.0;

    // Determine if the attack is critical
    boolean critical = roll > attackRoll - (attackRoll * CRITICAL_CHANCE);

    // Add attacker's hit modifier
    roll += hitMod;

    // Apply attacker passives
    if (attacker.hasPassive("battle trance")) {
      roll = (int)((double)roll * 1.1);
    }

    // Scale by the potency
    roll *= (double)potency / 100.0;

    // Apply defender passives
    boolean parried = Battle.parry(defender);

    // Calculate the result
    if (!parried) {
      if (critical) return AttackResult.CRITICAL;
      if (roll > ac) return AttackResult.HIT;
    }
    return AttackResult.MISS;
  }

  /**
   * Performs a damage roll with normal potency.
   * @see Battle.rollDamage(Player, Player, boolean, int)
   */
  public static int rollDamage(Player attacker, Player defender, boolean crit) {
    return Battle.rollDamage(attacker, defender, crit, 100);
  }

  /**
   * Performs a roll to determine the amount of damage for a successful hit.
   * @param attacker The attacking player.
   * @param defender The defending player.
   * @param crit Whether or not the attack is a critical hit.
   * @return The amount of damage dealt.
   */
  public static int rollDamage(
    Player attacker,
    Player defender,
    boolean crit,
    int potency
  ) {
    int averageDamage = attacker.getAverageDamage();
    int damageMod = attacker.getDamageMod();
    double damage = (double)Roll.normal(averageDamage) + damageMod;

    // Apply attacker passives
    if (attacker.hasPassive("battle trance")) {
      damage = damage * 1.1;
    }

    // Apply potency
    damage *= (double)potency / 100.0;

    // Apply critical hits
    if (crit) {
      damage *= 2.0;
    }

    // Result
    return (int)damage;
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
        if (target == null) continue;

        int numberOfAttacks = attacker.getNumberOfAttacks();
        int damage = 0;
        int hits = 0;

        Log.trace(String.format(
          "Battle: %s attacks %s.",
          attacker.getName(),
          target.getName()));

        for (int i = 0; i < numberOfAttacks; i++) {
          try {
            AttackResult result = Battle.rollToHit(attacker, target);
            if (result != AttackResult.MISS) {
              hits++;
              damage += Battle.rollDamage(
                attacker, target, result == AttackResult.CRITICAL);
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
        } else if (hits == 1) {
          messageBuffers.get(attacker).append(String.format(
            "[{g%d{x] You hit %s!\n\r",
            actualDamage, target.getName()));
          messageBuffers.get(target).append(String.format(
            "<{r%d{x> %s hit you!\n\r",
            actualDamage, attacker.getName()));
        } else {
          messageBuffers.get(attacker).append(String.format(
            "[{g%d{x] You hit %s {y%d{x times!\n\r",
            actualDamage, target.getName(), hits));
          messageBuffers.get(target).append(String.format(
            "<{r%d{x> %s hit you {y%d{x times!\n\r",
            actualDamage, attacker.getName(), hits));
        }
      }

      for (Player p : participants) {
        if (p.isDead()) dead.add(p);
      }

      Log.trace("Cleaning up and sending messages.");

      for (Player p : messageBuffers.keySet()) {
        p.sendMessage(messageBuffers.get(p).toString().trim());
      }

      for (Player p : dead) {
        remove(p);
        p.die(null);
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
   * Determines if a player is engaged in this battle.
   * @param p Player to find.
   * @return True if they part of this battle, false otherwise.
   */
  public boolean hasParticipant(Player p) {
    synchronized(participants) {
      for (Player q : participants) {
        if (q == p) return true;
      }
    }
    return false;
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
