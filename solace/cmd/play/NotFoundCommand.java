package solace.cmd.play;
import solace.game.Player;

/**
 * Default command to run when a command is not found or not available to a player.
 * @author Ryan Sandor Richards
 */
public class NotFoundCommand extends AbstractPlayCommand {
  public NotFoundCommand() {
    super("");
  }

  @Override
  public boolean hasCommand(Player player) {
    return true;
  }

  @Override
  public void run(Player player, String[] params) {
    player.sendln("Sorry, that is not an option. Type '{y}help{x}' to learn more.");
  }

  @Override
  public boolean matches(String s) {
    return false;
  }
}
