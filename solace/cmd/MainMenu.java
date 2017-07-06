package solace.cmd;

import java.util.*;
import java.io.*;

import solace.game.*;
import solace.net.*;
import solace.util.*;


/**
 * Controller for the game's main menu.
 * @author Ryan Sandor Richards (Gaius)
 */
public class MainMenu
  extends AbstractStateController
{
  /**
   * Creates a new main menu controller.
   */
  public MainMenu(Connection c)
  {
    // Initialize the menu
    super(c, "Sorry, that is not an option. Type '{y}help{x}' to see a list.");

    // Neato trick, actually use the help command to show the menu on login:
    StateCommand help = new Help();
    help.run(c, new String("help").split(" "));

    // Add all of the commands to the main menu
    addCommand(help);
    addCommand(new Quit());
    addCommand(new Who());
    addCommand(new Chat());
    addCommand(new List());
    addCommand(new Create());
    addCommand(new Play());

    addCommand(new Shutdown());
    addCommand(new Reload());
    addCommand(new Peek());
  }

  public String getPrompt() {
    return "{c}Choose an option:{x} ";
  }

  /**
   * List command - lists all the characters for the user's account.
   * @author Ryan Sandor Richards.
   */
  class List extends AbstractStateCommand {
    public List() { super("list"); }
    public void run(Connection c, String []params) {
      Collection<solace.game.Character> chars = c.getAccount().getCharacters();
      if (chars.size() == 0) {
        c.sendln(
          "You have no characters, " +
          "use the '{y}create{x}' command to create a new one."
        );
        c.sendln("");
      }
      else {
        c.sendln("{y}---- {x}Your Characters {y}----{x}");
        for (solace.game.Character ch : chars) {
          c.sendln(ch.getName());
        }
        c.sendln("");
      }
    }
  }

  /**
   * Create command - allows players to create new characters.
   * @author Ryan Sandor Richards
   */
  class Create extends AbstractStateCommand {
    public Create() { super("create"); }
    public void run(Connection c, String []params) {
      c.setStateController( new CreateCharacter(c) );
    }
  }

  /**
   * Play - enter and play the main game with a character.
   */
  class Play extends AbstractStateCommand {
    public Play() { super("play"); }
    public void run(Connection c, String []params) {
      try {
        Account act = c.getAccount();
        solace.game.Character ch;

        if (!act.hasCharacter()) {
          c.sendln(
            "You currently have no characters. Use the {y}create{x} command " +
            "to create a new character."
          );
          c.sendln("");
          return;
        }

        if (params.length < 2) {
          ch = act.getFirstCharacter();
        }
        else {
          String name = params[1];
          if (!act.hasCharacter(name)) {
            c.sendln(
              "Character '" + name + "' not found, " +
              "use the '{y}list{x}' command to see a list of your characters."
            );
            c.sendln("");
            return;
          }
          ch = act.getCharacter(name);
        }

        Room room = ch.getRoom();
        if (room == null) {
          room = World.getDefaultRoom();
          ch.setRoom(room);
        }
        room.getCharacters().add(ch);

        act.setActiveCharacter(ch);
        c.setStateController(new PlayController(c, ch));
      }
      catch (GameException ge) {
        Log.error(ge.getMessage());
        c.sendln("An {r}error{x} occured, please try again later.");
        c.sendln("");
      }
    }
  }

  /**
   * Chat command - Logs people into the out of game (OOG) chat room.
   * @author Ryan Sandor Richards
   */
  class Chat extends AbstractStateCommand
  {
    public Chat() { super("chat"); }
    public void run(Connection c, String []params) {
      c.setStateController(new ChatController(c));
    }
  }

  /**
   * Help Command
   * @author Ryan Sandor Richards (Gaius)
   */
  class Help extends AbstractStateCommand {
    public Help() { super("help"); }
    public void run(Connection c, String []params) {
      c.sendln(Message.get("MainMenu"));
      if (c.getAccount().isAdmin())
        c.sendln(Message.get("AdminMenu"));
    }
  }

  /**
   * Quit Command
   * @author Ryan Sandor Richards (Gaius)
   */
  class Quit extends AbstractStateCommand {
    public Quit() { super("quit"); }
    public void run(Connection c, String []params) {
      c.sendln("Goodbye!");

      // If the connection has an account, remove it from the accounts list
      if (connection.hasAccount()) {
        World.removeAccount(connection.getAccount());
      }

      // Remove it from the connections list
      World.removeConnection(connection);

      c.close();
    }
  }

  /**
   * Who command - lists who is online.
   * @author Ryan Sandor Richards
   */
  class Who extends AbstractStateCommand {
    public Who() { super("who"); }

    /*
     * Note: This whole thing will become SIGNIFICANTLY easier
     * when I switch the code base to java 6.
     */
    public void run(Connection c, String []params) {
      Collection connections = World.getConnections();
      c.sendln("{y}---- {x}Players Online{y} ----{x}");
      synchronized (connections) {
        Iterator iter = connections.iterator();
        while (iter.hasNext()) {
          Connection oc = (Connection)iter.next();
          if (oc.hasAccount()) {
            Account acct = (Account)oc.getAccount();
            c.sendln(acct.getName());
          }
        }
      }
      c.sendln("");
    }
  }

  /**
   * Shutdown (Admin Command) - Safely shuts the game server down and exits the
   * program.
   *
   * TODO Make this more robust, for now it will work fine. What I want to do is
   * have it take a couple of arguments (message, possibly a time in minutes for
   * the shutdown to occur, for instance).
   *
   * @author Ryan Sandor Richards
   */
  class Shutdown extends AdminCommand {
    public Shutdown() { super("shutdown"); }
    public void run(Connection c, String []params) {
      Game.shutdown();
    }
  }

  /**
   * Reload (Admin Command) - Reloads all "static" game messages (such as help
   * files, etc.). This is useful for when you have to make changes/corrections
   * to game message files and you just need to quickly update them without
   * doing a full reboot/reload of the game.
   *
   * @author Ryan Sandor Richards
   */
  class Reload extends AdminCommand {
    public Reload() { super("reload"); }

    /**
     * Reloads the game's areas.
     * @param c Connection that initiated the reload.
     */
    protected void reloadAreas(Connection c) throws IOException {
      Log.info(
        "Area reload commenced by '" +
        c.getAccount().getName().toLowerCase() + "'."
      );
      Collection<solace.game.Character> players =
        Collections.synchronizedCollection(World.getActiveCharacters());
      synchronized (players) {
        try {
          // Freeze all the players (ignore their input)
          // TODO: Once battle is in place we will need to freeze battle as well
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

          c.sendln("Areas reloaded.");
        }
        catch (GameException ge) {
          c.sendln(
            "Unable to reload areas: " +
            "default room could not be determined for the game."
          );
          Log.error(
            "Area reload by user '" +
            c.getAccount().getName().toLowerCase() +
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

    /**
     * Reloads the game's static messages.
     * @param c Connection that initiated the reload.
     */
    protected void reloadMessages(Connection c) throws IOException {
      Message.reload();
      c.sendln("Game messages reloaded.");
    }

    /**
     * Reloads the game's help files.
     * @param c Connection that initiated the reload.
     */
    protected void reloadHelp(Connection c) {
      HelpSystem.reload();
    }

    public void run(Connection c, String []params) {
      String errorStr = "";

      try {
        boolean hasParam = params.length > 1;
        boolean isAreas = false;
        boolean isMessages = false;
        boolean isHelp = false;

        if (hasParam) {
          isAreas = new String("areas").startsWith(params[1]);
          isMessages = new String("messages").startsWith(params[1]);
          isHelp = new String("help").startsWith(params[1]);
        }

        if (isAreas) {
          errorStr = "areas";
          reloadAreas(c);
        }
        else if (isHelp) {
          errorStr = "help";
          reloadHelp(c);
        }
        else if (isMessages || params.length == 1) {
          errorStr = "game messages";
          reloadMessages(c);
        }
        else {
          c.sendln(
            "Unable to reload '" + params[1] +
            "', you can either reload 'areas' or 'messages'."
          );
        }
      }
      catch (IOException ioe) {
        c.sendln("An error occured while trying to reload " + errorStr + ".");
        Log.error(
          "An IO exception occured when reloading " + errorStr + " by user '" +
          c.getAccount().getName().toLowerCase() + "'"
        );
      }
    }
  }

  /**
   * The peek command allows an administrator to view details about a particular
   * player's connection to the game.
   *
   * Note: This will get more and more detailed as the engine gets fleshed out.
   *
   * @author Ryan Sandor Richards
   */
  class Peek extends AdminCommand {
    public Peek() { super("peek"); }

    public void run(Connection c, String []params) {
      if (params.length < 2) {
        c.sendln("Syntax: peek <player1> <player2> ... | all");
        return;
      }

      if (params[1].toLowerCase().equals("all")) {
        Collection connections = Collections.synchronizedCollection(
          World.getConnections()
        );
        synchronized (connections) {
          Iterator i = connections.iterator();
          while (i.hasNext())
            c.sendln( formatInfo((Connection)i.next()) );
        }
      }
      else {
        // Generate peek reports for each of the given names
        for (int i = 1; i < params.length; i++) {
          if (!World.isLoggedIn(params[i])) {
            c.sendln("Player '" + params[i] + "' is not currently logged in.");
          }
          else {
            Connection target = World.connectionFromName(params[i]);
            c.sendln(formatInfo(target));
          }
          c.sendln("");
        }
      }
    }

    /**
     * Helper function to format information for the peek command.
     * @param c Connection to peek into.
     * @return Formatted peek information about the user.
     */
    protected String formatInfo(Connection c) {
      String format = "Player '" + c.getAccount().getName() +
        "', logged on at: " + c.getConnectionTime() +
        " from address: " + c.getInetAddress();
      return format;
    }
  }
}
