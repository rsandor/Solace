package solace.cmd;
import solace.game.Player;

import java.util.Arrays;
import java.util.Collection;

/**
 * Abstract base class for all play commands.
 * @author Ryan Sandor Richards.
 */
public abstract class AbstractCommand implements Command {
  public static int ORDER_CORE = 0;
  public static int ORDER_DEFAULT = 50;
  public static int ORDER_LOW = 100;

  private String name;
  private String displayName;
  private int priority = ORDER_DEFAULT;
  private String[] aliases = new String[0];

  /**
   * Creates an abstract play command with the given name.
   * @param n Name for the command.
   */
  public AbstractCommand(String n) {
    name = n;
    displayName = n;
  }

  /**
   * Creates an abstract play command with the given name and aliases.
   * @param n Name for the command.
   */
  public AbstractCommand(String n, String[] a) {
    name = n;
    displayName = n;
    aliases = a;
  }

  /**
   * Creates a new abstract play command with the given name and display name.
   * @param n Name for the command.
   * @param d Display name for the command.
   */
  public AbstractCommand(String n, String d) {
    name = n;
    displayName = d;
  }

  /**
   * Creates a new abstract play command with the given name, display name, and aliases.
   * @param n Name for the command.
   * @param d Display name for the command.
   */
  public AbstractCommand(String n, String d, String[] a) {
    name = n;
    displayName = d;
    aliases = a;
  }

  /**
   * Sets aliases for the command.
   * @param a Aliases for the command.
   */
  protected void setAliases(String[] a) { aliases = a; }

  /**
   * Sets the priority for the command.
   * @param p Priority to set.
   */
  public void setPriority(int p) { priority = p; }

  @Override
  public int getPriority() { return priority; }

  @Override
  public abstract void run(Player player, String[] params);

  @Override
  public boolean hasCommand(Player player) { return true; }

  @Override
  public String getName() { return name; }

  @Override
  public String getDisplayName() { return displayName; }

  @Override
  public Collection<String> getAliases() { return Arrays.asList(aliases); }
}

