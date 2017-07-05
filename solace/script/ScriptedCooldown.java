package solace.script;
import java.util.*;
import solace.cmd.Command;
import solace.cmd.cooldown.CooldownCommand;
import solace.cmd.cooldown.ResourceCost;
import solace.util.Log;
import solace.net.Connection;
import solace.game.Player;

/**
 * Data model for scripted gameplay cooldowns (`CooldownCommand`).
 * @author Ryan Sandor Richards
 */
public class ScriptedCooldown extends AbstractScriptedCommand {
  /**
   * Functional interface for cooldown execution methods defined in JavaScript.
   */
  @FunctionalInterface
  public interface CooldownExecuteFunction {
    public boolean execute(int level, Player player, Player target);
  }

  private int cooldownDuration = 0;
  private boolean initiatesCombat = false;
  private int castTime;
  private String castMessage;
  private List<ResourceCost> resourceCosts = new LinkedList<ResourceCost>();
  private String combosWith;
  private int basePotency = 0;
  private int comboPotency = 0;
  private String savingThrow;
  private CooldownExecuteFunction executeLambda;

  /**
   * Creates a new scripted cooldown.
   * @param name Name of the cooldown.
   * @param displayName Display name of the cooldown.
   */
  public ScriptedCooldown(String name, String displayName) {
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
  public int getCastTime() { return castTime; }

  /**
   * Sets the cast time for the cooldown.
   * @param ct Casting time in seconds.
   */
  public void setCastTime(int ct) { castTime = ct; }

  /**
   * @return The message to send when the player begins casting for this action.
   */
  public String getCastMessage() { return castMessage; }

  /**
   * Sets the casting message for this cooldown.
   * @param String m Message to set.
   */
  public void setCastMessage(String m) { castMessage = m; }

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
   * @return The name of the saving throw associated with the cooldown.
   */
  public String getSavingThrow() { return savingThrow; }

  /**
   * Sets the name of the saving throw for the cooldown.
   * @param s Name of the saving throw to set.
   */
  public void setSavingThrow(String s) { savingThrow = s; }

  /**
   * Adds a resource cost to this cooldown action.
   * @param c The cost to add.
   */
  public void addResourceCost(ResourceCost c) {
    resourceCosts.add(c);
  }

  /**
   * @return The execution lambda for the cooldown.
   */
  public CooldownExecuteFunction getExecuteLambda() {
    return executeLambda;
  }

  /**
   * Sets the execution lambda for the cooldown.
   * @param l The lamdba to set.
   */
  public void setExecuteLambda(CooldownExecuteFunction l) {
    executeLambda = l;
  }

  /**
   * Creates an instance of the play command for use by the game engine.
   * @param ch Character for the play command.
   * @return The play command instance.
   */
  public Command getInstance(solace.game.Character ch) {
    CooldownCommand command = new CooldownCommand(getName(), ch) {
      public boolean execute(int level, Player target) {
        return executeLambda.execute(level, getPlayer(), target);
      }
    };
    command.setDisplayName(getDisplayName());
    command.setCooldownDuration(getCooldownDuration());
    command.setInitiatesCombat(getInitiatesCombat());
    command.setCastTime(getCastTime());
    command.setCastMessage(getCastMessage());
    command.setCombosWith(getCombosWith());
    command.setBasePotency(getBasePotency());
    command.setComboPotency(getComboPotency());
    command.setSavingThrow(getSavingThrow());
    resourceCosts.forEach((cost) -> command.addResourceCost(cost));
    return command;
  }
}
