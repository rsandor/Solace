package solace.cmd.play;
import solace.game.Player;

/**
 * Abstract base class for all play commands.
 * @author Ryan Sandor Richards.
 */
public abstract class AbstractPlayCommand implements PlayCommand {
  private String name;
  private String displayName;
  private String[] aliases = new String[0];

  /**
   * Creates an abstract play command with the given name.
   * @param n Name for the command.
   */
  public AbstractPlayCommand(String n) {
    name = n;
    displayName = n;
  }

  /**
   * Creates an abstract play command with the given name and aliases.
   * @param n Name for the command.
   */
  public AbstractPlayCommand(String n, String[] a) {
    name = n;
    displayName = n;
    aliases = a;
  }

  /**
   * Creates a new abstract play command with the given name and display name.
   * @param n Name for the command.
   * @param d Display name for the command.
   */
  public AbstractPlayCommand(String n, String d) {
    name = n;
    displayName = d;
  }

  /**
   * Creates a new abstract play command with the given name, display name, and aliases.
   * @param n Name for the command.
   * @param d Display name for the command.
   */
  public AbstractPlayCommand(String n, String d, String[] a) {
    name = n;
    displayName = d;
    aliases = a;
  }

  /**
   * @see PlayCommand
   */
  public abstract void run(Player player, String[] params);

  /**
   * @see PlayCommand
   */
  public boolean hasCommand(Player player) {
    return true;
  }

  /**
   * @see solace.cmd.Command;
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @see solace.cmd.Command;
   */
  @Override
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Determines if the given string is a prefix of this command's name.
   * @param s String to test for a match
   * @return True if the string is prefix of this command's name, false otherwise.
   */
  @Override
  public boolean matches(String s) {
    if (name.toLowerCase().startsWith(s.toLowerCase())) {
      return true;
    }
    for (String alias : aliases) {
      if (alias.toLowerCase().startsWith(s.toLowerCase())) {
        return true;
      }
    }
    return false;
  }
}

