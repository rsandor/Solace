package solace.cmd.admin;

import solace.cmd.CommandRegistry;
import solace.game.Player;
import solace.script.Engine;
import solace.cmd.CompositeCommand;
import solace.util.HelpSystem;
import solace.util.Log;
import solace.util.Message;

/**
 * Command for reloading game data while the engine is running (e.g areas,
 * scripts, help files, etc.).
 * @author Ryan Sandor Richards
 */
public class Reload extends CompositeCommand {
  public Reload() {
    super("reload");
    addSubCommand("scripts", this::scripts);
    addSubCommand("messages", this::messages);
    addSubCommand("help", this::help);
  }

  @Override
  public boolean hasCommand(Player player) {
    return player.getAccount().isAdmin();
  }

  @Override
  protected void defaultCommand(Player player, String[] params) {
    player.sendln("Usage: reload (script)");
  }

  /**
   * Reloads game scripts.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void scripts(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated script reload...", player.getName()));
    Engine.reload();
    CommandRegistry.reload();
  }

  /**
   * Reloads game messages.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void messages(Player player, String[] params) {
    try {
      Log.info(String.format("User '{m}%s{x}' initiated messages reload", player.getName()));
      Message.reload();
      player.sendln("Game messages reloaded.");
    } catch (Throwable t) {
      player.sendln("Error encountered when reloading messages...");
      Log.error("Unable to reload game messages.");
      t.printStackTrace();
    }
  }

  /**
   * Reloads game help files.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void help(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated help reload...", player.getName()));
    HelpSystem.reload();
    player.sendln("Help articles reloaded.");
  }
}
