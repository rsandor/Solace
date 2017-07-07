package solace.cmd.deprecated;

import solace.net.Connection;

/**
 * Abstract base class for all StateCommand implementations. This classes that
 * extend this are used by AbstractStateControllers.
 * @see solace.cmd.AbstractStateController
 * @author Ryan Sandor Richards
 */
public abstract class AbstractStateCommand implements StateCommand {
  private String name;
  private String displayName;

  /**
   * Creates a new <code>AbstractStateCommand</code> with the given name.
   * @param n Name for the command.
   */
  public AbstractStateCommand(String n) {
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
    return name.toLowerCase().startsWith(s.toLowerCase());
  }

  /**
   * @see StateCommand
   */
  public String getName() {
    return name;
  }

  /**
   * @see StateCommand
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * @see StateCommand
   */
  public void setDisplayName(String d) {
    displayName = d;
  }

  /**
   * @see solace.cmd.Command
   */
  public abstract void run(Connection c, String[] params);
}
