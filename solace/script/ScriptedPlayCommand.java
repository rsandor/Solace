package solace.script;
import solace.cmd.Command;
import solace.cmd.play.PlayCommand;
import solace.net.Connection;
import solace.game.Player;
import java.util.function.BiPredicate;

/**
 * Data model for scripted gameplay commands (`PlayCommand`). Gameplay commands
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
    BiPredicate<Player, String[]> runLambda
  ) {
    super(name, displayName, runLambda);
  }

  /**
   * Creates an instance of the play command for use by the game engine.
   * @param ch Character for the play command.
   * @return The play command instance.
   */
  public Command getInstance(solace.game.Character ch) {
    Command command = new PlayCommand(getName(), ch) {
      public boolean run(Connection c, String[] params) {
        return getRunLambda().test(ch, params);
      }
    };
    command.setDisplayName(getDisplayName());
    return command;
  }
}
