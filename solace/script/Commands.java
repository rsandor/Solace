package solace.script;
import java.util.*;

/**
 * Service for adding and accessing commands generated via the scripting engine.
 * @author Ryan Sandor Richards
 */
public class Commands {
  public static List<ScriptedPlayCommand> playCommands =
    Collections.synchronizedList(new LinkedList<ScriptedPlayCommand>());

  /**
   * Adds a new play command.
   * @param c The command to add.
   */
  public static void addPlayCommand(ScriptedPlayCommand c) {
    playCommands.add(c);
  }

  /**
   * @return An unmodifiable collection of all play commands registered via
   *   the scripting engine.
   */
  public static Collection<ScriptedPlayCommand> getPlayCommands() {
    return playCommands;
  }
}
