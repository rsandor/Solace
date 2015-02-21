package solace.cmd;

import solace.net.Connection;
import java.util.*;


/**
 * Base class for all state controllers used in the Solace Engine.
 * Works as a nice implementation of StateController adding some
 * features that allow controllers to work more fluidly with the
 * <code>Command</code> class.
 *
 * @author Ryan Sandor Richards
 */
public abstract class AbstractStateController
    implements StateController
{
    /**
     * Simple command tuple.
     * @author Ryan Sandor Richards
     */
    private class CommandTuple {
        // Instance variables
        String name;
        Command command;

        /**
         * Creates a new command tuple.
         * @param n Name for the command.
         */
        public CommandTuple(String n, Command c) {
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
        public Command getCommand() {
            return command;
        }

        /**
         * Implementation of the basic "prefix" match used by many other MUDs.
         * @param s String to check for match.
         * @return <code>true</code> if the given string matches, <code>false</code> otherwise.
         */
        public boolean matches(String s) {
            return name.startsWith(s);
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
    public AbstractStateController(Connection c)
    {
        init(c);
        invalidCommandMessage = "Unknown command.";
    }

    /**
     * Creates a new controller with the specified connection and invalid command
     * message.
     * @param c Connection for the controller.
     * @param icm Invalid command message for the controller.
     */
    public AbstractStateController(Connection c, String icm)
    {
        init(c);
        invalidCommandMessage = icm;
    }

    /**
     * Initializes this controller to work with the given connection.
     */
    public void init(Connection c)
    {
        connection = c;
    }

    /**
     * Adds a command to this controller.
     * @param c Command to add.
     */
    public void addCommand(Command c)
    {
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
    public void addCommand(String alias, Command c) {
        commands.add(new CommandTuple(alias, c));
    }

    /**
     * Attempts to find a command that matches with the given string.
     * @param c Search criteria.
     * @return A command that matches, or null if no commands match the string.
     */
    protected Command findCommand(String c)
    {
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
    public void parse(String input)
    {
        if (input == null || connection == null)
            return;

        String []params = input.split("\\s+");

        if (params.length < 1)
            return;

        Command cmd = findCommand(params[0]);

        if (cmd != null && cmd.canExecute(connection))
            cmd.run(connection, params);
        else
            connection.sendln(invalidCommandMessage);
    }
}
