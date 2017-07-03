package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;
import solace.cmd.AbstractCommand;
import solace.cmd.InvalidTargetException;

/**
 * Base class for all cool down commands. Cool down commands are ones which
 * become unavailable for a certain duration after their use.
 * @author Ryan Sandor Richards
 */
public abstract class CooldownCommand extends AbstractCommand {
  /**
   * Exception thrown before scheduling or executing the cooldown action in the
   * case where the action cannot be scheduled or executed.
   * @author Ryan Sandor Richards
   */
  protected class CooldownException extends Exception {
    public CooldownException(String message) {
      super(message);
    }
  }

  /**
   * Indicates that the command uses the global cool down.
   */
  public static final int GLOBAL_COOLDOWN = -1;

  Player player;
  int cooldownDuration;
  boolean initiatesCombat;
  boolean onCooldown;
  int castTime;
  List<ResourceCost> resourceCosts = new LinkedList<ResourceCost>();

  /**
   * Creates a new cool down command with the given name and duration for the
   * specified player.
   * @param name Name of the command.
   * @param p Player for the command.
   */
  public CooldownCommand(String name, Player p) {
    super(name);
    player = p;
    onCooldown = false;
    castTime = 0;
  }

  /**
   * @return The player associated with the command.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * @return Amount of time for the cooldown.
   */
  public int getCooldownDuration() {
    return cooldownDuration;
  }

  /**
   * Sets the coodown duration for the command.
   * @param d The duration to set.
   */
  public void setCooldownDuration(int d) {
    cooldownDuration = d;
  }

  /**
   * @return True if the command initiates combat, false otherwise.
   */
  public boolean getInitiatesCombat() {
    return initiatesCombat;
  }

  /**
   * Sets whether or not the cooldown initiates combat.
   * @param combat [description]
   */
  public void setInitiatesCombat(boolean combat) {
    initiatesCombat = combat;
  }

  /**
   * @return The cast time for the cooldown.
   */
  public int getCastTime() { return castTime; }

  /**
   * Sets the cast time for the cooldown.
   * @param ct Casting time in seconds.
   */
  public void setCastTime(int ct) { castTime = ct; }

  /**
   * @return The message to send when the player begins casting for this action.
   */
  protected String getCastMessage() { return "You begin casting..."; }

  /**
   * Adds a resource cost to this cooldown action.
   * @param c The cost to add.
   */
  public void addResourceCost(ResourceCost c) {
    resourceCosts.add(c);
  }

  /**
   * Determines if the player can pay resource costs.
   * @throws CooldownException If the player cannot pay the resource costs.
   */
  protected void checkResourceCosts() throws CooldownException {
    for (ResourceCost cost : resourceCosts) {
      if (!cost.canWithdraw(player)) {
        throw new CooldownException(cost.getInsufficentResourceMessage());
      }
    }
  }

  /**
   * Handles behaviors required before scheduling the cooldown action.
   * @param params Parameters sent along with the command.
   * @return The target for the action, if applicable, otherwise null.
   * @throws CooldownException
   */
  protected Player beforeSchedule(String []params)
    throws CooldownException
  {
    // Determine if the player even has the skill
    if (!player.hasCooldown(getName())) {
      throw new CooldownException(String.format(
        "You do not possess the %s action.", getName()));
    }

    // Players must always be at a ready state to use cooldowns
    if (!player.isStanding() && !player.isFighting()) {
      throw new CooldownException(String.format(
        "You must be standing and alert to use %s.", getName()));
    }

    // Check to see if the skill is on cooldown
    boolean onGCD = cooldownDuration == GLOBAL_COOLDOWN && player.isOnGCD();
    if (onGCD || onCooldown) {
      throw new CooldownException(String.format(
        "%s is not ready yet.", getName()));
    }

    // If applicable, find and assign a target.
    Player target = null;
    if (params.length > 1) {
      target = player.getRoom().findPlayerIfVisible(params[1], player);
    }

    // Determine if the target is fighting in another battle
    Battle playerBattle = null;
    if (player.isFighting()) {
      playerBattle = BattleManager.getBattleFor(player);
    }

    Battle targetBattle = null;
    if (target != null && target.isFighting()) {
      targetBattle = BattleManager.getBattleFor(target);
    }

    if (target != null && target.isFighting() && playerBattle != targetBattle) {
      // TODO Rework this when player groups come along
      throw new CooldownException(String.format(
        "%s is already engaged in combat!", target.getName()));
    }

    // Schedule global cooldowns
    if (cooldownDuration == GLOBAL_COOLDOWN) {
      player.setOnGCD();
    }

    // Check to ensure all the resource costs can be met
    checkResourceCosts();

    return target;
  }

  /**
   * Performs tasks immediately after scheduling the action.
   * @param params Params to the action.
   * @param target Target of the action.
   */
  protected void afterSchedule(String []params, Player target) {
  }

  /**
   * Performs tasks before executing the action.
   * @param params Params to the action.
   * @param target Target of the action.
   * @throws CooldownException If the action should not be executed.
   */
  protected void beforeExecute(String []params, Player target)
    throws CooldownException
  {
    checkResourceCosts();
    for (ResourceCost cost : resourceCosts) {
      cost.withdraw(player);
    }
  }

  /**
   * Performs tasks immediately after executing the action.
   * @param params Params to the action.
   * @param target Target of the action.
   * @param result The result of the action.
   */
  protected void afterExecute(
    Connection c,
    String []params,
    Player target,
    boolean result
  ) {
    // Schedule off global cooldowns
    if (cooldownDuration != GLOBAL_COOLDOWN && result) {
      onCooldown = true;
      Clock.getInstance().schedule(
        String.format("%s cooldown for %s", getName(), player.getName()),
        cooldownDuration,
        new Runnable() {
          public void run() {
            onCooldown = false;
          }
        });
      player.cooldownAt(getName(), cooldownDuration);
    }

    // Handle GCD combos
    if (cooldownDuration == GLOBAL_COOLDOWN && result) {
      player.setComboAction(getName());
    }

    // Initiate combat if applicable
    if (!initiatesCombat || !result) {
      return;
    }

    if (target != null) {
      if (!player.isFighting() && !target.isFighting()) {
        BattleManager.initiate(player, target);
        if (c != null) {
          c.skipNextPrompt();
        }
      } else if (player.isFighting() && !target.isFighting()) {
        Battle playerBattle = BattleManager.getBattleFor(player);
        playerBattle.add(target);
        playerBattle.setAttacking(target, player);
      }
    }
  }

  /**
   * @see solace.cmd.AbstractCommand
   */
  public boolean run(Connection c, String[] params) {
    try {
      Player target = beforeSchedule(params);
      if (castTime < 1) {
        afterSchedule(params, target);
        beforeExecute(params, target);
        boolean result = execute(player.getCooldownLevel(getName()), target);
        afterExecute(c, params, target, result);
      } else {
        player.setCasting(true);
        player.sendln(getCastMessage());
        Clock.getInstance().schedule(
          String.format("%s casting %s", player.getName(), getName()),
          castTime,
          new Runnable() {
            public void run() {
              try {
                player.setCasting(false);
                beforeExecute(params, target);
                boolean result = execute(
                  player.getCooldownLevel(getName()),
                  target);
                afterExecute(c, params, target, result);
              } catch (CooldownException cnse) {
                player.sendln(cnse.getMessage());
              }
            }
          });
        afterSchedule(params, target);
      }
    } catch (CooldownException cde) {
      player.sendln(cde.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Checks to determine if the target for the cooldown is valid.
   * @param target Target to check.
   * @throws InvalidTargetException With a descriptive message if the target is
   *   not valid.
   */
  protected void checkValidTarget(Player target) throws InvalidTargetException {
  }

  /**
   * Internal helper method for executing cooldown commands as attacks. Handles
   * targeting attack rolls, damage rolls, damage application, and battle
   * messages.
   * @param target The target of the attack.
   * @param basePotency The base potency of the attack.
   * @param comboWith Name of a cooldown that causes this to be a combo strike.
   * @param comboPotency Potency for the attack if it was performed as a combo.
   * @param saveName Name of the saving throw if this is a magical attack.
   * @return `true` if the attack succeeded, `false` otherwise.
   */
  private boolean executeAttack(
    Player target,
    int basePotency,
    String comboWith,
    int comboPotency,
    String saveName
  ) {
    try {
      if (!player.isFighting() && target == null) {
        player.sendln(String.format(
          "Who would you like to attack with {m%s{x?",
          getDisplayName()));
        return false;
      }

      if (player.isFighting() && target == null) {
        target = BattleManager.getBattleFor(player).getTargetFor(player);
      }

      checkValidTarget(target);

      boolean isCombo = player.getComboAction().equals(comboWith);
      int potency = isCombo ? comboPotency : basePotency;

      AttackResult result;
      if (saveName == null) {
        result = Battle.rollToHit(player, target, potency);
      } else {
        result = Battle.rollToCast(player, target, potency, saveName);
      }

      if (result == AttackResult.MISS) {
        player.sendMessage(String.format(
          "Your {m%s{x misses!",
          getDisplayName()));
        return false;
      }

      boolean critical = result == AttackResult.CRITICAL;
      int damage = Battle.rollDamage(player, target, critical, potency);
      target.applyDamage(damage);

      if (isCombo) {
        player.sendln(String.format(
          "[{g%d{x] {y<{Ycombo{y>{x Your {m%s{x hits %s!",
          damage, getDisplayName(), target.getName()));
      } else {
        player.sendln(String.format(
          "[{g%d{x] Your {m%s{x hits %s!",
          damage, getDisplayName(), target.getName()));
      }

      target.sendln(String.format(
        "<{r%d{x> %s hits you with an {m%s{x!",
        damage, player.getName(), getDisplayName()));

      return true;
    } catch (InvalidTargetException ite) {
      player.sendln(ite.getMessage());
      return false;
    }
  }

  /**
   * Helper for executing physical attacks that handles rolls and messaging.
   * @param target The target of the attack.
   * @param potency The potency of the attack.
   * @return `true` if the attack succeeds, `false` otherwise.
   */
  protected boolean executePhysicalAttack(
    Player target,
    int potency
  ) {
    return executePhysicalAttack(target, potency, null, 0);
  }

  /**
   * Helper for executing physical attacks that handles rolls, messaging, and
   * combo attacks.
   * @param target The target of the attack.
   * @param basePotency The base potency of the attack.
   * @param comboWith Name of a cooldown that causes this to be a combo strike.
   * @param comboPotency Potency for the attack if it was performed as a combo.
   * @return `true` if the attack succeeds, `false` otherwise.
   */
  protected boolean executePhysicalAttack(
    Player target,
    int basePotency,
    String comboWith,
    int comboPotency
  ) {
    return executeAttack(target, basePotency, comboWith, comboPotency, null);
  }

  /**
   * Helper for executing magical attacks that handles rolls and messaging.
   * @param target The target of the attack.
   * @param potency The potency of the attack.
   * @param save Name of the opposing saving throw.
   * @return `true` if the attack succeeds, `false` otherwise.
   */
  protected boolean executeMagicAttack(
    Player target,
    int potency,
    String save
  ) {
    return executeMagicAttack(target, potency, null, 0, save);
  }

  /**
   * Helper for executing magical attacks that handles rolls, messaging, and
   * combo attacks.
   * @param target The target of the attack.
   * @param potency The potency of the attack.
   * @param comboWith Name of a cooldown that causes this to be a combo strike.
   * @param comboPotency Potency for the attack if it was performed as a combo.
   * @param save Name of the opposing saving throw.
   * @return `true` if the attack succeeds, `false` otherwise.
   */
  protected boolean executeMagicAttack(
    Player target,
    int basePotency,
    String comboWith,
    int comboPotency,
    String save
  ) {
    return executeAttack(target, basePotency, comboWith, comboPotency, save);
  }

  /**
   * Executes the cooldown action.
   * @param level Level of the skill for the action.
   * @param target A target, if any, for the action.
   * @return True if the action successfully executed, false otheriwse.
   */
  public abstract boolean execute(int level, Player target);
}
