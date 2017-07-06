package solace.cmd;
import solace.game.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Registry for all game play commands. This keeps track of the master
 * list of all commands that can be used within the context of the
 * main game.
 * @author Ryan Sandor Richards
 */
public class PlayCommandRegistry {
  private final List<PlayCommand> commands;
  private final PlayCommand notFound;
  private static final PlayCommandRegistry instance = new PlayCommandRegistry();

  /**
   * @return An instance of the play command registry.
   */
  public static final PlayCommandRegistry getInstance() {
    return instance;
  }

  /**
   * Creates a new game play commands registry.
   */
  public PlayCommandRegistry() {
    commands = Collections.synchronizedList(new ArrayList<PlayCommand>());
    notFound = new NotFoundCommand();
  }

  /**
   * Adds a command to the registry.
   * @param command Command to add to the registry.
   */
  public void addCommand(PlayCommand command) {
    commands.add(command);
  }

  /**
   * Clears all commands from the registry.
   */
  public void clear() {
    synchronized (commands) {
      commands.clear();
    }
  }

  /**
   * Finds the command with the name matching the given search string.
   * @param search String to match against.
   * @return The first command that matches the given string or the "not found" command.
   */
  public PlayCommand get(String search) {
    synchronized (commands) {
      List<PlayCommand> found = new LinkedList<>();
      for (PlayCommand command : commands) {
        if (command.matches(search)) {
          found.add(command);
        }
      }
      return (found.size() > 0) ? found.get(0) : notFound;
    }
  }
}
