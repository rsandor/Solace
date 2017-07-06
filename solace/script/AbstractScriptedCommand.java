package solace.script;
import solace.cmd.StateCommand;
import solace.game.Player;
import java.util.function.BiPredicate;

/**
 * Abstract base class for all particular scripted command implementations.
 * @author Ryan Sandor Richards
 */
public abstract class AbstractScriptedCommand implements ScriptedCommand {
  private String name;
  private String displayName;
  private BiPredicate<Player, String[]> runLambda;

  /**
   * Creates a new abstract command with the given names.
   * @param name Name of the command.
   * @param displayName The display name for the command.
   */
  public AbstractScriptedCommand(String name, String displayName) {
    setName(name);
    setDisplayName(name);
  }

  /**
   * Creates a new abstract command with the given names and run lamdba.
   * @param name Name of the command.
   * @param displayName The display name for the command.
   * @param runLambda Run lambda for the command.
   */
  public AbstractScriptedCommand(
    String name,
    String displayName,
    BiPredicate<Player, String[]> runLambda
  ) {
    setName(name);
    setDisplayName(displayName);
    setRunLambda(runLambda);
  }

  /**
   * @see solace.script.ScriptedCommand
   */
  public String getName() { return name; }

  /**
   * @see solace.script.ScriptedCommand
   */
  public void setName(String n) { name = n; }

  /**
   * @see solace.script.ScriptedCommand
   */
  public String getDisplayName() { return displayName; }

  /**
   * @see solace.script.ScriptedCommand
   */
  public void setDisplayName(String n) { displayName = n; }

  /**
   * @see solace.script.ScriptedCommand
   */
  public BiPredicate<Player, String[]> getRunLambda() { return runLambda; }

  /**
   * @see solace.script.ScriptedCommand
   */
  public void setRunLambda(BiPredicate<Player, String[]> l) { runLambda = l; }

  /**
   * @see solace.script.ScriptedCommand
   */
  public abstract StateCommand getInstance(solace.game.Character ch);
}
