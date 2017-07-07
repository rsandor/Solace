package solace.cmd.play;

import solace.cmd.deprecated.play.*;
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
    addCommands();
  }

  // TODO Remove me
  void addCommands() {
    add(new Quit());
    add(new Move());
    add(new Look());

    /*
    add(new Help());
    add(new Say());
    add(new Scan());
    add(new Tick());

    add(new Score());
    add(new Worth());
    add(new ListSkills());
    add(new solace.cmd.deprecated.play.Buffs());
    add(new Cooldown());
    add(new Passive());

    add(new Wear());
    add(new Equipment());
    add(new Remove());

    add(new ShopList());
    add(new ShopBuy());
    add(new ShopAppraise());
    add(new ShopSell());

    add(new Attack());
    add(new Flee());

    add(new Sit());
    add(new Stand());
    add(new Rest());
    add(new Sleep());
    add(new Wake());

    add(new Prompt());
    add(new Hotbar());

    // Add all scripted play commands
    for (ScriptedCommand command : Commands.getCommands()) {
      add(command.getInstance());
    }

    // Emotes
    Emote emote = new Emote();
    add(emote);
    add(Emotes.getInstance().getEmoteAliases(), new Emote());

    // Admin Commands
    add(new Inspect());
    add(new solace.cmd.admin.Set());
    */
  }

  /**
   * Adds a command to the registry.
   * @param command Command to add to the registry.
   */
  public void add(PlayCommand command) {
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
   * @param player Player for which the command is being sought.
   * @return The first command that matches the given string or the "not found" command.
   */
  public PlayCommand find(String search, Player player) {
    synchronized (commands) {
      List<PlayCommand> found = new LinkedList<>();
      for (PlayCommand command : commands) {
        if (command.matches(search)) {
          found.add(command);
        }
      }
      // TODO We need a way to rank commands with like prefixes...
      for (PlayCommand command : found) {
        if (command.hasCommand(player)) {
          return command;
        }
      }
      return notFound;
    }
  }
}
