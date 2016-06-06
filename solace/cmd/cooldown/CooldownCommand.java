package solace.cmd.cooldown;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;
import solace.cmd.AbstractCommand;

/**
 * Base class for all cool down commands. Cool down commands are ones which
 * become available for a certain duration after their use.
 * @author Ryan Sandor Richards
 */
public abstract class CooldownCommand extends AbstractCommand {
  public static final int GLOBAL_COOLDOWN = -1;

  Player player;
  int cooldownDuration;
  boolean initiatesCombat;
  boolean onCooldown;

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
   * @see solace.cmd.AbstractCommand
   */
  public boolean run(Connection c, String[] params) {
    // Determine if the player even has the skill
    int skillLevel = player.getCooldownSkillLevel(getName());
    if (skillLevel < 1) {
      player.sendln(String.format(
        "You do not possess the %s action.", getName()));
      return false;
    }

    // Players must always be at a ready state to use cooldowns
    if (!player.isStanding() && !player.isFighting()) {
      player.sendln(String.format(
        "You must be standing and alert to use %s.", getName()));
      return false;
    }

    // Check to see if the skill is on cooldown
    boolean onGCD = cooldownDuration == GLOBAL_COOLDOWN && player.isOnGCD();
    if (onGCD || onCooldown) {
      player.sendln(String.format("%s is not ready yet.", getName()));
      return false;
    }

    // If applicable, find and assign a target.
    Player target = null;
    if (params.length > 1) {
      target = player.getRoom().findPlayer(params[1]);
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
      player.sendln(String.format(
        "%s is already engaged in combat!", target.getName()));
      return false;
    }

    // Schedule global cooldowns
    if (cooldownDuration == GLOBAL_COOLDOWN) {
      player.setOnGCD();
    }

    // Execute the action and get the results
    boolean result = execute(skillLevel, target);

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
        }
      );
    }

    // Initiate combat if applicable
    if (!initiatesCombat || !result) {
      return result;
    }

    if (target != null) {
      if (!player.isFighting() && !target.isFighting()) {
        BattleManager.initiate(player, target);
        if (c != null) {
          c.skipNextPrompt();
        }
      } else if (!player.isFighting() && target.isFighting()) {
        // NOTE this currently cannot happen but will with player groups
        targetBattle.add(player);
        targetBattle.setAttacking(player, target);
      } else if (player.isFighting() && !target.isFighting()) {
        playerBattle.add(target);
        playerBattle.setAttacking(target, player);
      }
    }

    return true;
  }

  /**
   * Executes the cooldown action.
   * @param level Level of the skill for the action.
   * @param target A target, if any, for the action.
   * @return True if the action successfully executed, false otheriwse.
   */
  public abstract boolean execute(int level, Player target);
}
