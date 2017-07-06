package solace.cmd.play;
import solace.game.Player;

/**
 * Abstract base class for all play commands.
 * @author Ryan Sandor Richards.
 */
public abstract class AbstractPlayCommand implements PlayCommand {
  private String name;
  private String displayName;

  /**
   * Creates an abstract play command with the given name.
   * @param n Name for the command.
   */
  public AbstractPlayCommand(String n) {
    name = n;
    displayName = n;
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
   * @see PlayCommand ;
   */
  public abstract void run(Player player, String[] params);

  /**
   * @see PlayCommand ;
   */
  public abstract boolean hasCommand(Player player);

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
    return name.toLowerCase().startsWith(s.toLowerCase());
  }
}

