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

  private Connection connection;
  private NameTrie<MenuCommand> commands = new NameTrie<>(this::notFound);

  /**
   * Functional interface for menu commands.
   */
  private interface MenuCommand {
    void run(String[] params);
  }

  /**
   * Creates a new main menu controller for the given connection.
   * @param c Connection for the controller.s
   */
  public MainMenuController(Connection c) {
    connection = c;
    commands.put("help", this::help);
    commands.put("quit", this::quit);
    commands.put("who", this::who);
    commands.put("chat", this::chat);
    commands.put("list", this::list);
    commands.put("create", this::create);
    commands.put("play", this::play);
    help(new String[0]);
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
    commands.find(s).run(s.split("\\s"));
  }

  /**
   * Command that is run with no commands could be found.
   * @param params Ignored.
   */
  @SuppressWarnings("unused")
  private void notFound(String[] params) {
    connection.sendln(commandNotFound);
  }

  /**
   * Sends player to out of game chat.
   */
  @SuppressWarnings("unused")
  private void chat(String[] params) {
    connection.setStateController(new ChatController(connection));
  }

  /**
   * Sends player to the character creator.
   */
  @SuppressWarnings("unused")
  private void create(String[] params) {
    connection.setStateController(new CreateCharacterController(connection));
  }

  /**
   * Displays the main menu help.
   */
  @SuppressWarnings("unused")
  private void help(String[] params) {
    connection.sendln(Messages.get("MainMenu"));
  }

  /**
   * Quits the game.
   */
  @SuppressWarnings("unused")
  private void quit(String[] params) {
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
  @SuppressWarnings("unused")
  private void who(String[] params) {
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
  @SuppressWarnings("unused")
  private void list(String[] params) {
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

  /**
   * Enters the connected user into the game.
   * @param params First parameter should be the name of the character they wish to play.
   */
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
}
