package solace.cmd;

import java.util.*;
import solace.game.*;
import solace.net.*;
import solace.util.*;

/**
 * Controller for the game's main menu.
 * @author Ryan Sandor Richards
 */
public class MainMenuController implements Controller {
  private static final String commandNotFound =
    "Unknown option, use {y}help{x} to see all menu options.";
  private final Hashtable<String, Runnable> commands = new Hashtable<>();
  private final Hashtable<String, Runnable> adminCommands = new Hashtable<>();
  private Connection connection;

  /**
   * Creates a new main menu controller for the given connection.
   * @param c Connection for the controller.s
   */
  public MainMenuController(Connection c) {
    connection = c;

    // Add commands
    commands.put("help", this::help);
    commands.put("quit", this::quit);
    commands.put("who", this::who);
    commands.put("chat", this::chat);
    commands.put("list", this::list);
    commands.put("create", this::create);
    adminCommands.put("shutdown", () -> Game.shutdown());

    // Show the main menu
    help();
  }

  /**
   * @see solace.cmd.Controller
   */
  public String getPrompt() {
    return "{c}Choose an option:{x} ";
  }

  /**
   * @see solace.cmd.Controller
   */
  public void parse(String s) {
    String[] params = s.split("\\s");
    if (params.length == 0 || params[0] == null) {
      connection.sendln(commandNotFound);
      return;
    }

    String prefix = params[0].toLowerCase();

    // Handle special case for play, which takes parameters
    if ("play".startsWith(prefix)) {
      play(params);
      return;
    }

    // Regular commands
    for (String name : Collections.list(commands.keys())) {
      if (name.toLowerCase().startsWith(prefix)) {
        commands.get(name).run();
        return;
      }
    }

    // Admin Commands
    if (connection.hasAccount() && connection.getAccount().isAdmin()) {
      for (String name : Collections.list(adminCommands.keys())) {
        if (name.toLowerCase().startsWith(s.toLowerCase())) {
          adminCommands.get(name).run();
          return;
        }
      }
    }

    // Command was not found, send the not found message
    connection.sendln(commandNotFound);
  }

  /**
   * Sends player to out of game chat.
   */
  private void chat() {
    connection.setStateController(new ChatController(connection));
  }

  /**
   * Sends player to the character creator.
   */
  private void create() {
    connection.setStateController(new CreateCharacterController(connection));
  }

  /**
   * Displays the main menu help.
   */
  private void help() {
    connection.sendln(Message.get("MainMenu"));
    if (connection.getAccount().isAdmin()) {
      connection.sendln(Message.get("AdminMenu"));
    }
  }

  /**
   * Quits the game.
   */
  private void quit() {
    connection.sendln("Goodbye!");
    if (connection.hasAccount()) {
      World.removeAccount(connection.getAccount());
    }
    World.removeConnection(connection);
    connection.close();
  }

  /**
   * Displays a list of player accounts connected to the game.
   */
  private void who() {
    connection.sendln("{y}---- {x}Players Online{y} ----{x}");
    for (Connection c : World.getConnections()) {
      if (c.hasAccount()) {
        Account acct = c.getAccount();
        connection.sendln(acct.getName());
      }
    }
    connection.sendln("");
  }

  /**
   * Lists an account's player characters.
   */
  private void list() {
    if (!connection.hasAccount()) {
      return;
    }
    Collection<solace.game.Character> chars = connection.getAccount().getCharacters();
    if (chars.size() == 0) {
      connection.sendln(
        "You have no characters, " +
          "use the '{y}create{x}' command to create a new one."
      );
      connection.sendln("");
    }
    else {
      connection.sendln("{y}---- {x}Your Characters {y}----{x}");
      for (solace.game.Character ch : chars) {
        connection.sendln(ch.getName());
      }
      connection.sendln("");
    }
  }

  private void play(String[] params) {
    try {
      Account act = connection.getAccount();
      solace.game.Character ch;

      if (!act.hasCharacter()) {
        connection.sendln(
          "You currently have no characters. Use the {y}create{x} command " +
            "to create a new character."
        );
        connection.sendln("");
        return;
      }

      if (params.length < 2) {
        ch = act.getFirstCharacter();
      }
      else {
        String name = params[1];
        if (!act.hasCharacter(name)) {
          connection.sendln(
            "Character '" + name + "' not found, " +
              "use the '{y}list{x}' command to see a list of your characters."
          );
          connection.sendln("");
          return;
        }
        ch = act.getCharacter(name);
      }

      Room room = ch.getRoom();
      if (room == null) {
        room = World.getDefaultRoom();
        ch.setRoom(room);
      }
      room.addPlayer(ch);

      act.setActiveCharacter(ch);
      connection.setStateController(new PlayController(ch));
    }
    catch (GameException ge) {
      Log.error(ge.getMessage());
      connection.sendln("An {r}error{x} occured, please try again later.");
      connection.sendln("");
    }
  }

  // TODO Need to move these into a game play command (more useful there).
  /*
  private void reloadAreas() {
    Log.info(
      "Area reload commenced by '" +
        connection.getAccount().getName().toLowerCase() + "'."
    );
    Collection<solace.game.Character> players =
      Collections.synchronizedCollection(World.getActiveCharacters());
    synchronized (players) {
      try {
        // Freeze all the players (ignore their input)
        for (solace.game.Character ch : players) {
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
        for (solace.game.Character ch : players) {
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

        connection.sendln("Areas reloaded.");
      }
      catch (GameException ge) {
        connection.sendln(
          "Unable to reload areas: " +
            "default room could not be determined for the game."
        );
        Log.error(
          "Area reload by user '" +
            connection.getAccount().getName().toLowerCase() +
            "' aborted: no default room could be determined."
        );
      }
      finally {
        // Un-freeze the players and force them to take a look around :)
        for (solace.game.Character ch : players) {
          Connection con = ch.getConnection();
          con.sendln("{y}Areas reloaded, thanks for your patience!{x}\n");
          con.setIgnoreInput(false);
          con.send(con.getStateController().getPrompt());
        }
      }
    }
  }

  private void reloadMessages() {
    try {
      Message.reload();
      connection.sendln("Game messages reloaded.");
    } catch (Throwable t) {
      connection.sendln("Error encountered when reloading messages.");
    }
  }

  private void reloadHelp() {
    HelpSystem.reload();
    connection.sendln("Help articles reloaded.");
  }
  */
}
