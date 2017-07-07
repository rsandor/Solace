package solace.script;
import solace.cmd.AbstractCommand;
import solace.cmd.Command;
import solace.game.Player;
import java.util.function.BiPredicate;

/**
 * Data model for scripted gameplay commands (`PlayStateCommand`). Gameplay commands
 * handle basic actions such as getting items, movement, etc.
 * @author Ryan Sandor Richards
 */
public class ScriptedPlayCommand extends AbstractScriptedCommand {
  /**
   * Creates a new play command with the given names and run lamdba.
   * @param name Name of the command.
   * @param displayName The display name for the command.
   * @param runLambda Run lambda for the command.
   */
  public ScriptedPlayCommand(
    String name,
    String displayName,
    String[] aliases,
    BiPredicate<Player, String[]> runLambda
  ) {
    super(name, displayName, aliases, runLambda);
  }

  /**
   * Creates an instance of the play command for use by the game engine.
   * @return The play command instance.
   */
  public Command getInstance() {
    return new AbstractCommand(getName(), getDisplayName(), getAliases()) {
      public void run(Player p, String[] params) {
        getRunLambda().test(p, params);
      }
    };
  }
}
