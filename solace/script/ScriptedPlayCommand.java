package solace.script;
import solace.cmd.StateCommand;
import solace.cmd.play.PlayStateCommand;
import solace.net.Connection;
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
    BiPredicate<Player, String[]> runLambda
  ) {
    super(name, displayName, runLambda);
  }

  /**
   * Creates an instance of the play command for use by the game engine.
   * @param ch Character for the play command.
   * @return The play command instance.
   */
  public StateCommand getInstance(solace.game.Character ch) {
    StateCommand command = new PlayStateCommand(getName(), ch) {
      public void run(Connection c, String[] params) {
        getRunLambda().test(ch, params);
      }
    };
    command.setDisplayName(getDisplayName());
    return command;
  }
}
