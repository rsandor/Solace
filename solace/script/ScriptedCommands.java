package solace.script;
import java.util.*;

/**
 * Service for adding and accessing commands generated via the scripting engine.
 * @author Ryan Sandor Richards
 */
public class ScriptedCommands {
  private static List<ScriptedCommand> commands =
    Collections.synchronizedList(new LinkedList<ScriptedCommand>());

  public static void clear() { commands.clear(); }

  /**
   * Adds a new scripted command.
   * @param c The command to add.
   */
  public static void add(ScriptedCommand c) {
    commands.add(c);
  }

  /**
   * @return An unmodifiable collection of all play commands registered via
   *   the scripting engine.
   */
  public static Collection<ScriptedCommand> getCommands() {
    return Collections.unmodifiableCollection(commands);
  }
}
