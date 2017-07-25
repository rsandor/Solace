package solace.cmd;

import java.util.LinkedList;
import java.util.List;

import solace.game.AttackResult;
import solace.game.Battle;
import solace.game.BattleManager;
import solace.game.Player;
import solace.net.Connection;
import solace.util.Clock;
import solace.util.Log;

/**
 * Base class for all cooldown commands. Cool down commands are ones which
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
    CooldownException(String message) { super(message); }
  }

  /**
   * Indicates that the command uses the global cool down.
   */
  private static final int GLOBAL_COOLDOWN = -1;
  private int cooldownDuration;
  private List<ResourceCost> resourceCosts = new LinkedList<>();
  private boolean initiatesCombat = false;
  private String combosWith = null;
  private int basePotency = 0;
  private int comboPotency = 0;
  private int castTime = 0;
  private String savingThrow = null;
  private String castMessage = "You begin casting...";

  /**
   * Creates a new cool down command with the given name.
   * @param name Name of the command.
   */
  @SuppressWarnings("unused")
  public CooldownCommand(String name) {
    super(name);
  }

  /**
   * Creates a new cool down command with the given name and display name.
   * @param name Name of the command.
   */
  protected CooldownCommand(String name, String displayName) {
    super(name, displayName);
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
  @SuppressWarnings("unused")
  public int getCastTime() { return castTime; }

  /**
   * Sets the cast time for the cooldown.
   * @param ct Casting time in seconds.
   */
  public void setCastTime(int ct) { castTime = ct; }

  /**
   * @return `true` if the cooldown has a non-zero cast time `false` otherwise.
   */
  @SuppressWarnings("unused")
  public boolean hasCastTime() { return castTime > 0; }

  /**
   * @return The message to send when the player begins casting for this action.
   */
  private String getCastMessage() { return castMessage; }

  /**
   * Sets the casting message for this cooldown.
   * @param msg Message to set.
   */
  public void setCastMessage(String msg) { castMessage = msg; }

  /**
   * Adds a resource cost to this cooldown action.
   * @param c The cost to add.
   */
  public void addResourceCost(ResourceCost c) {
    resourceCosts.add(c);
  }

  /**
   * Determines if the player can pay resource costs.
   * @param player Player for which to check costs.
   * @throws CooldownException If the player cannot pay the resource costs.
   */
  protected void checkResourceCosts(Player player) throws CooldownException {
    for (ResourceCost cost : resourceCosts) {
      if (!cost.canWithdraw(player)) {
        throw new CooldownException(cost.getInsufficentResourceMessage());
      }
    }
  }

  /**
   * @return The name of the cooldown with which this combos.
   */
  public String getCombosWith() { return combosWith; }

  /**
   * Sets the name of the cooldown off which this skill combos.
   * @param c The name of the combo cooldown.
   */
  public void setCombosWith(String c) { combosWith = c; }

  /**
  * @return The base attack potency for the cooldown.
  */
  public int getBasePotency() { return basePotency; }

  /**
   * Sets the nase attack potency for the cooldown.
   * @param i Potency to set.
   */
  public void setBasePotency(int i) { basePotency = i; }

  /**
  * @return The combo potency for the cooldown.
  */
  public int getComboPotency() { return comboPotency; }

  /**
   * Sets the combo potency for the cooldown.
   * @param i Potency to set.
   */
  public void setComboPotency(int i) { comboPotency = i; }

  /**
   * Determines if this cooldown should be executed as a combo.
   * @return `true` if it's a combo, `false` otherwise.
   */
  protected boolean isCombo(Player player) {
    return player.getComboAction().equals(getCombosWith());
  }

  /**
   * Determines the potency for a cooldown attack based on whether or not it is
   * a combo attack.
   * @return The cooldown attack potency.
   */
  public int getPotency(Player player) {
    return isCombo(player) ? getComboPotency() : getBasePotency();
  }

  /**
   * @return The name of the saving throw associated with the cooldown.
   */
  public String getSavingThrow() { return savingThrow; }

  /**
   * Sets the name of the saving throw for the cooldown.
   * @param s Name of the saving throw to set.
   */
  public void setSavingThrow(String s) { savingThrow = s; }

  protected boolean isSpellAttack() {
    return getSavingThrow() != null;
  }

  /**
   * Handles behaviors required before scheduling the cooldown action.
   * @param player Player executing the cooldown.
   * @param params Parameters sent along with the command.
   * @return The target for the action, if applicable, otherwise null.
   * @throws CooldownException
   */
  protected Player beforeSchedule(Player player, String []params)
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
    boolean onGCD = getCooldownDuration() == GLOBAL_COOLDOWN && player.isOnGCD();
    boolean onCooldown = player.isOnCooldown(getName());
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
    if (getCooldownDuration() == GLOBAL_COOLDOWN) {
      player.setOnGCD();
    }

    // Check to ensure all the resource costs can be met
    checkResourceCosts(player);

    return target;
  }

  /**
   * Performs tasks immediately after scheduling the action.
   * @param player Player executing the cooldown.
   * @param target Target for the cooldown.
   * @param params Params to the cooldown.
   */
  protected void afterSchedule(Player player, Player target, String []params) {
  }

  /**
   * Performs tasks before executing the action.
   * @param player Player executing the cooldown.
   * @param target Target for the cooldown.
   * @param params Params to the cooldown.
   * @throws CooldownException If the action should not be executed.
   */
  protected void beforeExecute(Player player, Player target, String []params)
    throws CooldownException
  {
    checkResourceCosts(player);
    for (ResourceCost cost : resourceCosts) {
      cost.withdraw(player);
    }
  }

  /**
   * Performs tasks immediately after executing the action.
   * @param player Player executing the cooldown.
   * @param params Params for the cooldown.
   * @param target Target of the cooldown (if any).
   * @param isHit Whether or not the target (if any) was hit.
   */
  protected void afterExecute(Player player, String []params, Player target, boolean isHit) {
    // Schedule off global cooldowns
    if (getCooldownDuration() != GLOBAL_COOLDOWN && isHit) {
      player.setOnCooldown(getName(), getCooldownDuration());
    }

    // Handle GCD combos
    if (getCooldownDuration() == GLOBAL_COOLDOWN && isHit) {
      player.setComboAction(getName());
    }

    /*
    // Keep this here, this has broken A LOT.
    System.out.println("Target = " + target);
    System.out.println("initatesCombat = " + getInitiatesCombat());
    System.out.println("isHit = " + isHit);
    System.out.println("target is player = " + (getPlayer() == target));
    */

    // Bypass combat if ...
    if (
      target == null ||         // There is no target, OR
      !getInitiatesCombat() ||  // The cooldown does not initiate combat, OR
      !isHit ||                 // The cooldown didn't hit the target, OR
      player == target     // The player is the target
    ) {
      return;
    }

    // Initiate combat
    if (!player.isFighting() && !target.isFighting()) {
      BattleManager.initiate(player, target);
      Connection connection = player.getConnection();
      if (connection != null) {
        connection.skipNextPrompt();
      }
    } else if (player.isFighting() && !target.isFighting()) {
      Battle playerBattle = BattleManager.getBattleFor(player);
      playerBattle.add(target);
      playerBattle.setAttacking(target, player);
    }
  }

  /**
   * @see Command
   */
  public void run(Player player, String[] params) {
    try {
      Player target = beforeSchedule(player, params);
      int level = player.getCooldownLevel(getName());
      if (castTime < 1) {
        afterSchedule(player, target, params);
        beforeExecute(player, target, params);
        boolean result = execute(player, target, level);
        afterExecute(player, params, target, result);
      } else {
        if (player.isCasting()) {
          player.sendln("You are already casting a spell!");
          return;
        }

        String eventName = String.format("%s casting %s", player.getName(), getName());
        player.beginCasting(Clock.getInstance().schedule(eventName, castTime, () -> {
          try {
            player.finishCasting();
            beforeExecute(player, target, params);
            boolean result = execute(player, target, level);
            afterExecute(player, params, target, result);
          } catch (CooldownException cnse) {
            player.sendln(cnse.getMessage());
          }
        }));
        player.sendln(getCastMessage());

        afterSchedule(player, target, params);
      }
    } catch (CooldownException cde) {
      player.sendln(cde.getMessage());
    }
  }

  /**
   * Checks to determine if the target for the cooldown is valid. This method
   * is meant to be overriden by subclasses.
   * @param target Target to check.
   * @throws InvalidTargetException With a descriptive message if the target is
   *   not valid.
   */
  public void checkValidTarget(Player target)
    throws InvalidTargetException
  {
    if (target == null) {
      Log.error("Unable to find target for cooldown command.");
      throw new InvalidTargetException("Could not find the target!");
    }
  }

  /**
   * Resolves a target given to the command's execute method and determines if
   * it is valid. If no target is given this method will attempt to find the
   * executor's current battle target.
   * @param player Player executing the cooldown.
   * @param givenTarget Target as given at the time of cooldown execution.
   * @return The actual target for the cooldown.
   * @throws InvalidTargetException If the final target is invalid.
   */
  public Player resolveTarget(Player player, Player givenTarget)
    throws InvalidTargetException
  {
    Player target = givenTarget;

    if (target == null) {
      if (player.isFighting()) {
        target = BattleManager.getBattleFor(player).getTargetFor(player);
      } else {
        throw new InvalidTargetException(String.format(
          "Who would you like to attack with {m}%s{x}?",
          getDisplayName()));
      }
    }

    checkValidTarget(target);
    return target;
  }

  /**
   * Performs a roll to determine if the cooldown attack hits, criticals or
   * misses. By default this will use the `Battle.rollToHit` unless this
   * cooldown defines is a spell. In the latter case it will use the
   * `Battle.rollToCast` method.
   * @param target The target of the attack.
   * @return The result of the roll.
   */
  public AttackResult rollToHit(Player attacker, Player target) {
    if (isSpellAttack()) {
      return Battle.rollToCast(attacker, target, getSavingThrow());
    }
    return Battle.rollToHit(attacker, target, getPotency(attacker));
  }

  /**
   * Sends the player a message saying that the cooldown attack missed.
   * @param attacker The player using the cooldown.
   */
  public void sendMissMessage(Player attacker) {
    attacker.sendln(String.format("Your {m}%s{x} misses!", getDisplayName()));
  }

  /**
   * Sends messages to the player and that target that the cooldown attack hit.
   * @param attacker Player that is executing the cooldown.
   * @param target The target of the attack.
   * @param result The result of the to hit roll for the attack.
   * @param damage The damage dealt by the attack.
   */
  private void sendHitMessages(Player attacker, Player target, AttackResult result, int damage) {
    if (isCombo(attacker)) {
      attacker.sendln(String.format(
        "[{g}%d{x}] {y}<{Y}combo{y}>{x} Your {m}%s{x} hits %s!",
        damage, getDisplayName(), target.getName()));
    } else {
      attacker.sendln(String.format(
        "[{g}%d{x}] Your {m}%s{x} hits %s!",
        damage, getDisplayName(), target.getName()));
    }
    target.sendln(String.format(
      "<{r}%d{x}> %s hits you with an {m}%s{x}!",
      damage, attacker.getName(), getDisplayName()));
  }

  /**
   * Internal helper method for executing cooldown commands as attacks. Handles
   * targeting attack rolls, damage rolls, damage application, and battle
   * messages.
   * @param givenTarget The given target of the attack (if any).
   * @return `true` if the attack succeeded, `false` otherwise.
   */
  @SuppressWarnings("unused")
  public boolean executeAttack(Player attacker, Player givenTarget) {
    try {
      // Resolve the target
      Player target = resolveTarget(attacker, givenTarget);

      // Make the "roll to hit" the target
      AttackResult result = rollToHit(attacker, target);
      if (result.isMiss()) {
        sendMissMessage(attacker);
        return false;
      }

      // Roll damage, apply it, and we're done!
      int damage = Battle.rollDamage(attacker, target, result.isCritical(), getPotency(attacker));
      target.applyDamage(damage);
      sendHitMessages(attacker, target, result, damage);
      return true;
    } catch (InvalidTargetException ite) {
      attacker.sendln(ite.getMessage());
      return false;
    }
  }

  /**
   * Executes the cooldown action.
   * @param level Level of the skill for the action.
   * @param target A target, if any, for the action.
   * @return True if the action successfully executed, false otheriwse.
   */
  public abstract boolean execute(Player player, Player target, int level);
}
