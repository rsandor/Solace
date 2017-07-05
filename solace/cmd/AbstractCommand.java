package solace.cmd;

import solace.net.Connection;

/**
 * Basic abstract command class that serves as the base class for all commands.
 * This class handles command name storage and retrieval, as well as the command
 * matching by using the well known "prefix" match.
 *
 * @author Ryan Sandor Richards (Gaius)
 */
public abstract class AbstractCommand
  implements Command
{
  String name;
  String displayName;
  boolean skipPrompt = false;

  /**
   * Creates a new <code>AbstractCommand</code> with the given name.
   * @param n Name for the command.
   */
  public AbstractCommand(String n) {
    name = n.toLowerCase();
    setDisplayName(n);
  }

  /**
   * Default behavior set to always return <code>true</code>, override in sub
   * classes to add varied functionality.
   * @param c Connection to test against.
   */
  public boolean canExecute(Connection c) {
    return true;
  }

  /**
   * Implementation of the basic "prefix" match used by many other MUDs.
   * @param s String to check for match.
   * @return <code>true</code> if the given string matches, <code>false</code>
   *   otherwise.
   */
  public boolean matches(String s) {
    return name.startsWith(s);
  }

  /**
   * @see solace.cmd.Command.getName()
   */
  public String getName() {
    return name;
  }

  /**
   * @see solace.cmd.Command.getName()
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Method used to set the display name of the command if it differs from the
   * name.
   * @param d Desired display name for the command.
   */
  public void setDisplayName(String d) {
    displayName = d;
  }

  /**
   * @see solace.cmd.Command.run(Connection c, String []params)
   */
  public abstract boolean run(Connection c, String[] params);
}
