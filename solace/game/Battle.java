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
  Hashtable<Player, StringBuffer> messageBuffers;

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
    messageBuffers = new Hashtable<Player, StringBuffer>();
  }

  /**
   * Adds a player to the battle.
   * @param p Player to add.
   */
  public synchronized void add(Player p) {
    participants.add(p);
    messageBuffers.put(p, new StringBuffer());
  }

  /**
   * Removes a character or mobile from the battle.
   * @param p Player to remove.
   */
  public synchronized void remove(Player p) {
    messageBuffers.remove(p);
    participants.remove(p);
    attackers.removeAll(p);
    targets.remove(p);
    p.setStanding(); // Removes the "fighting" state from the player
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

  /**
   * Adds a battle message for the specified player.
   * @param p Player to recieve message.
   * @param msg Message to send.
   */
  public synchronized void message(Player p, String msg) {
    messageBuffers.get(p).append(msg);
  }

  /**
   * Determines if a defending player successfully parries an attack.
   * @param defender The player defending an attack.
   * @return True if the attack is parried, false otherwise.
   */
  public static boolean parry(Player defender) {
    int skillLevel = defender.getPassiveLevel("parry");
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
   * Applies global passive modifiers to an attack roll.
   * @param roll The attack roll to scale.
   * @return The resulting roll after passive have been applied.
   */
  private static double applyAttackerPassivesToRoll(Player attacker, double roll) {
    if (attacker.hasPassive("battle trance")) {
      roll = roll * 1.1;
    }
    if (attacker.hasPassive("generalist")) {
      roll = roll * 1.05;
    }
    return roll;
  }

  /**
   * Scales the attack roll by the given potency.
   * @param roll The attack roll to scale.
   * @param potency Potency by which to scale the roll.
   * @return The scaled attack roll.
   */
  private static double scaleAttackRollByPotency(int potency, double roll) {
    // TODO This is too poweful in some cases (coup de grace give 10x)
    return roll * (double)potency / 100.0;
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

    // Apply attacker passives
    roll = applyAttackerPassivesToRoll(attacker, roll);

    // Add attacker's hit modifier
    roll += hitMod;

    // Scale by the potency
    roll = scaleAttackRollByPotency(potency, roll);

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
   * Performs a roll to see if an attacker successfully casts a spell against
   * a defender.
   * @param attacker The attacking player.
   * @param defender The defending player.
   * @param potency Potency of the attack (scales the attacker roll).
   * @param savingThrow Name of the saving throw to use for the defender.
   */
  public static AttackResult rollToCast(
    Player attacker,
    Player defender,
    int potency,
    String savingThrow
  ) {
    int save = defender.getSavingThrow(savingThrow);
    int magicRoll = attacker.getMagicRoll(savingThrow);

    // Make the roll
    double roll = magicRoll * Roll.uniform() + 1.0;

    // Determine if the attack is critical
    boolean critical = roll > magicRoll - (magicRoll * CRITICAL_CHANCE);

    // Apply attacker passives
    roll = applyAttackerPassivesToRoll(attacker, roll);

    // Apply defender passives
    // TODO Currently none, but counter magic would be cool as hell

    // Calculate the result
    if (critical) return AttackResult.CRITICAL;
    if (roll > save) return AttackResult.HIT;
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
    if (defender.isImmortal()) {
      return 0;
    }

    int averageDamage = attacker.getAverageDamage();
    int damageMod = attacker.getDamageMod();
    double damage = (double)Roll.normal(averageDamage) + damageMod;

    // Apply attacker passives
    if (attacker.hasPassive("battle trance")) {
      damage = damage * 1.1;
    }

    if (attacker.hasBuff("concentrate")) {
      potency *= 2.0;
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
  public synchronized void round() {
    Log.trace("Battle: Synchronizing participants.");
    for (Player attacker : participants) {
      Player target = targets.get(attacker);
      if (target == null) continue;

      int numberOfAttacks = attacker.hasBuff("stun") ?
        0 : attacker.getNumberOfAttacks();
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

    Set<Player> dead = new HashSet<Player>();
    for (Player p : participants) {
      if (p.isDead()) dead.add(p);
    }

    Log.trace("Cleaning up and sending messages.");

    for (Player p : messageBuffers.keySet()) {
      StringBuffer messageBuffer = messageBuffers.get(p);
      p.sendMessage(messageBuffers.get(p).toString().trim());
      messageBuffer.setLength(0);
    }

    for (Player p : dead) {
      remove(p);
      p.die(null);
    }
  }
}
