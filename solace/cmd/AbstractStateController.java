package solace.cmd;

import solace.net.Connection;
import java.util.*;
import java.util.concurrent.*;
import solace.util.Log;
import solace.util.CommandParser;

/**
 * Base class for all state controllers used in the Solace Engine. Works as a
 * nice implementation of StateController adding some features that allow
 * controllers to work more fluidly with the <code>StateCommand</code> objects.
 *
 * @author Ryan Sandor Richards
 */
public abstract class AbstractStateController implements StateController {
  /**
   * Simple command tuple.
   * @author Ryan Sandor Richards
   */
  protected class CommandTuple {
    // Instance variables
    String name;
    StateCommand command;

    /**
     * Creates a new command tuple.
     * @param n Name for the command.
     */
    public CommandTuple(String n, StateCommand c) {
      name = n;
      command = c;
    }

    /**
     * @return The name associated with the command.
     */
    public String getName() {
      return name;
    }

    /**
     * @return The executable command.
     */
    public StateCommand getCommand() {
      return command;
    }

    /**
     * Implementation of the basic "prefix" match used by many other MUDs.
     * @param s String to check for match.
     * @return <code>true</code> if the given string matches, <code>false</code>
     *   otherwise.
     */
    public boolean matches(String s) {
      return name.toLowerCase().startsWith(s.toLowerCase());
    }
  }

  // Instance Variables
  Connection connection;
  LinkedList<CommandTuple> commands = new LinkedList<CommandTuple>();
  String invalidCommandMessage;

  /**
   * @return the invalidCommandMessage
   */
  public String getInvalidCommandMessage() {
    return invalidCommandMessage;
  }

  /**
   * @param invalidCommandMessage the invalidCommandMessage to set
   */
  public void setInvalidCommandMessage(String invalidCommandMessage) {
    this.invalidCommandMessage = invalidCommandMessage;
  }

  /**
   * Creates a new controller.
   * @param c Connection the controller is to work with.
   */
  public AbstractStateController(Connection c) {
    init(c);
    invalidCommandMessage = "Unknown command.";
  }

  /**
   * Creates a new controller with the specified connection and invalid command
   * message.
   * @param c Connection for the controller.
   * @param icm Invalid command message for the controller.
   */
  public AbstractStateController(Connection c, String icm) {
    init(c);
    invalidCommandMessage = icm;
  }

  /**
   * Initializes this controller to work with the given connection.
   */
  public void init(Connection c) {
    connection = c;
  }

  /**
   * Adds a command to this controller.
   * @param c Command to add.
   */
  public void addCommand(StateCommand c) {
    commands.add(new CommandTuple(c.getName(), c));
  }

  /**
   * Adds a command to the controller under the given alias.
   * This is useful for commands that can be executed via
   * multiple names (such as movement commands).
   *
   * @param alias Alias for the command.
   * @param c Command to add.
   */
  public void addCommand(String alias, StateCommand c) {
    commands.add(new CommandTuple(alias, c));
  }

  /**
   * Adds a command to the controller under each given alias.
   * @param aliases Alias names for the command.
   * @param c Command to add.
   */
  public void addCommand(String[] aliases, StateCommand c) {
    for (String alias : aliases) {
      commands.add(new CommandTuple(alias, c));
    }
  }

  /**
   * Adds a command to the controller under each given alias.
   * @param aliases Alias names for the command.
   * @param c Command to add.
   */
  public void addCommand(Collection<String> aliases, StateCommand c) {
    for (String alias : aliases) {
      commands.add(new CommandTuple(alias, c));
    }
  }

  /**
   * Attempts to find a command that matches with the given string.
   * @param c Search criteria.
   * @return A command that matches, or null if no commands match the string.
   */
  protected StateCommand findCommand(String c) {
    for (CommandTuple t : commands)
      if (t.matches(c))
        return t.getCommand();
    return null;
  }

  /**
   * Forces a user to execute a command.
   * Note: This function is VERY useful for unit testing :).
   * @param command Command to execute.
   */
  public void force(String command) {
    connection.sendln(command);
    parse(command);
  }

  /**
   * Parses commands using a "prefix" search routine.
   * @param input Input to parse.
   */
  public void parse(String input) {
    if (input == null || connection == null)
      return;

    if (input.length() < 1) {
      return;
    }

    String[] params = CommandParser.parse(input);
    if (params.length < 1) {
      return;
    }

    StateCommand cmd = findCommand(params[0]);
    if (cmd != null && cmd.canExecute(connection)) {
      try {
        cmd.run(connection, params);
      }
      catch (Exception e) {
        Log.error(
          "AbstractStateController.parse() - Error in command callback: " +
          e.getMessage()
        );
        e.printStackTrace();
      }
    }
    else {
      connection.sendln(invalidCommandMessage);
    }
  }

  /**
   * @see solace.cmd.StateController
   */
  public abstract String getPrompt();
}
