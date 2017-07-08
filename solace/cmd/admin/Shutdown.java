package solace.cmd.admin;

import solace.cmd.AbstractCommand;
import solace.game.Game;
import solace.game.Player;
import solace.util.Log;

/**
 * Admin command used to shut down the game server.
 * @author Ryan Sandor Richards
 */
public class Shutdown extends AbstractCommand {
  public Shutdown() {
    super("shutdown");
  }

  @Override
  public boolean hasCommand(Player player) {
    return player.getAccount().isAdmin();
  }

  @Override
  public void run(Player player, String[] params) {
    Log.info(String.format("Shutdown initiated by '%s'", player.getName()));
    Game.shutdown();
  }
}
