package solace.script;
import java.util.function.BiPredicate;

import solace.cmd.Command;
import solace.game.Player;

/**
 * Defines the interface for the core script generated command data model.
 * This is the primary "bridge" type between the scripting engine and the game
 * engine in regards to command defintion.
 * @author Ryan Sandor Richards
 */
public interface ScriptedCommand {
  /**
   * @return The name of the scripted command.
   */
  public String getName();

  /**
   * Sets the name of the scripted command.
   * @param n The name to set.
   */
  public void setName(String n);

  /**
   * @return the display name for the scripted command.
   */
  public String getDisplayName();

  /**
   * Sets the display name for the scripted command.
   * @param n The name to set.
   */
  public void setDisplayName(String n);

  /**
   * @return the lambda used to run the command.
   */
  public BiPredicate<Player, String[]> getRunLambda();

  /**
   * Sets the lambda used to run the command.
   * @param l The lamdba to set.
   */
  public void setRunLambda(BiPredicate<Player, String[]> l);

  /**
   * Gets an instance of the command for a given player character.
   * @return An instance of the command.
   */
  public Command getInstance();
}
