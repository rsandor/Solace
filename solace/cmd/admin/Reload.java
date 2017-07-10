package solace.cmd.admin;

import solace.cmd.CommandRegistry;
import solace.cmd.GameException;
import solace.game.*;
import solace.game.Character;
import solace.net.Connection;
import solace.script.ScriptingEngine;
import solace.cmd.CompositeCommand;
import solace.util.*;

import java.util.Collection;
import java.util.Collections;

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
    addSubCommand("areas", this::areas);
    addSubCommand("emotes", this::emotes);
    addSubCommand("skills", this::skills);
    addSubCommand("races", this::races);
  }

  @Override
  public boolean hasCommand(Player player) {
    return player.getAccount().isAdmin();
  }

  @Override
  protected void defaultCommand(Player player, String[] params) {
    player.sendln("Usage: reload (script|messages|help|areas|emotes|skills|races)");
  }

  /**
   * Reloads game scripts.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void scripts(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated script reload...", player.getName()));
    ScriptingEngine.reload();
    CommandRegistry.reload();
    player.sendln("Game scripts reloaded.");
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
      Messages.reload();
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

  /**
   * Reloads game areas.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void areas(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated area reload...", player.getName()));
    Collection<Character> characters =
      Collections.synchronizedCollection(World.getActiveCharacters());
    synchronized (characters) {
      try {
        // Freeze all the players (ignore their input)
        for (solace.game.Character ch : characters) {
          Connection con = ch.getConnection();
          con.sendln("\n{y}Game areas being reloaded, please stand by...{x}");
          con.setIgnoreInput(true);
        }

        // Reload all the areas
        World.loadAreas();
        Room defaultRoom = World.getDefaultRoom();
        if (defaultRoom == null) {
          Log.error("Default room null on area reload.");
        }

        // Place players into their original rooms if available, or the
        // default room if not
        for (solace.game.Character ch : characters) {
          Connection con = ch.getConnection();

          if (!con.hasAccount())
            continue;

          Account act = con.getAccount();
          if (act == null) {
            Log.error("Null account encountered on area reload.");
            continue;
          }

          if (!act.hasActiveCharacter()) {
            Log.error(
              "Account without active character (" +
                act.getName().toLowerCase() +
                ") encountered on area reload."
            );
            continue;
          }

          Room room = ch.getRoom();

          if (room == null) {
            Log.error("Null room encountered on area reload.");
            ch.setRoom(defaultRoom);
          }
          else {
            Area area = room.getArea();
            if (area == null) {
              ch.setRoom(defaultRoom);
            }
            else {
              String room_id = room.getId();
              String area_id = area.getId();
              Area new_area = World.getArea(area_id);

              if (new_area == null) {
                ch.setRoom(defaultRoom);
              }
              else {
                Room new_room = new_area.getRoom(room_id);
                if (new_room == null)
                  ch.setRoom(defaultRoom);
                else
                  ch.setRoom(new_room);
              }
            }
          }
        }

        player.sendln("Areas reloaded.");
      }
      catch (GameException ge) {
        String msg = "Area reload error: cannot find default room.";
        player.sendln(msg);
        Log.error(msg);
      }
      finally {
        // Un-freeze the players and force them to take a look around :)
        for (solace.game.Character ch : characters) {
          Connection con = ch.getConnection();
          con.sendln("{y}Areas reloaded, thanks for your patience!{x}\n");
          con.setIgnoreInput(false);
          con.send(con.getStateController().getPrompt());
        }
      }
    }
  }

  /**
   * Reloads game emotes.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void emotes(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated emotes reload...", player.getName()));
    try {
      Emotes.getInstance().reload();
      player.sendln("Emotes articles reloaded.");
    } catch (Throwable t) {
      player.sendln("An error occurred when reloading emotes.");
    }
  }

  /**
   * Reloads game skills.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void skills(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated skills reload...", player.getName()));
    try {
      Skills.getInstance().reload();
      World.getActiveCharacters().forEach(Character::resetSkills);
      player.sendln("Skills reloaded.");
    } catch (Throwable t) {
      player.sendln("An error occurred when reloading emotes.");
    }
  }

  /**
   * Reloads game races.
   * @param player Player initiating the reload.
   * @param params Original command parameters.
   */
  @SuppressWarnings("unused")
  private void races(Player player, String[] params) {
    Log.info(String.format("User '{m}%s{x}' initiated races reload...", player.getName()));
    try {
      Races.getInstance().reload();
      World.getActiveCharacters().forEach(Character::resetRace);
      player.sendln("Races reloaded.");
    } catch (Throwable t) {
      player.sendln("An error occurred when reloading emotes.");
    }
  }
}
