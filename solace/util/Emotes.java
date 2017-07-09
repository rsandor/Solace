package solace.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Loads and formats emotes for the game.
 * @author Ryan Sandor Richards
 */
public class Emotes {
  /**
   * Location of the emote files in the game data directory.
   */
  private static final String EMOTE_DIR = "game/emotes/";

  private static final int FORMAT_SELF = 0;
  private static final int FORMAT_ROOM = 1;
  private static final int FORMAT_TARGET_SELF = 2;
  private static final int FORMAT_TARGET = 3;
  private static final int FORMAT_TARGET_ROOM = 4;

  private final Hashtable<String, String> emotes = new Hashtable<>();

  /**
   * Creates a new Emotes instance and loads all emote files.
   */
  public Emotes() {
    try {
      reload();
    } catch (IOException e) {
      Log.error("Unable to load emotes:");
      e.printStackTrace();
    }
  }

  /**
   * Reloads all emotes.
   * @throws IOException If the emotes directory could not be read.
   */
  public void reload() throws IOException {
    emotes.clear();
    Files.find(
      Paths.get(EMOTE_DIR),
      Integer.MAX_VALUE,
      (path, attr) -> attr.isRegularFile()
    ).forEach((path) -> {
      String name = path.getFileName().toString();
      try {
        emotes.put(name, new String(Files.readAllBytes(path)));
      } catch (IOException e) {
        Log.warn(String.format("Error loading emote '%s', skipping.", name));
      }
    });
  }

  /**
   * @return A collection of emote names for use with the emote command.
   */
  public String[] getEmoteAliases() {
    return emotes.keySet().toArray(new String[0]);
  }

  /**
   * Finds an emote that matches the given prefix.
   * @param prefix Prefix to find.
   * @return The name of the emote that matches, or null if none was found.
   */
  protected String emoteName(String prefix) {
    for (String name : emotes.keySet()) {
      if (name.startsWith(prefix)) {
        return name;
      }
    }
    return null;
  }

  /**
   * Gets the format string for an emote with the given prefix.
   * @param prefix Prefix for the emote to find.
   * @param number The format string index to retrieve.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   * @throws InvalidEmoteException If the emote file is incorrectly formatted.
   */
  protected String getEmoteFormat(String prefix, int number)
    throws EmoteNotFoundException, InvalidEmoteException
  {
    String emote = emoteName(prefix);
    if (emote == null) {
      throw new EmoteNotFoundException("Emote with prefix not found: " + prefix);
    }

    String[] formats = emotes.get(emote).split("\n");
    if (formats.length <= number) {
      throw new InvalidEmoteException("Emote incorrectly formatted: " + emote);
    }

    return formats[number];
  }

  /**
   * Determines if an emote with the given name exists.
   * @param emote Name of the emote to check.
   * @return `true` if the emote exists, `false` otherwise.
   */
  public boolean hasEmote(String emote) {
    return emoteName(emote) != null;
  }

  /**
   * Formats an emote string to send to the source.
   * @param prefix Partial prefix name for the emote.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   * @throws InvalidEmoteException If the emote file is incorrectly formatted.
   */
  public String toSource(String prefix)
    throws EmoteNotFoundException, InvalidEmoteException
  {
    return getEmoteFormat(prefix, FORMAT_SELF);
  }

  /**
   * Formats an emote string with a target to send to the character who executed
   * the command.
   * @param prefix Partial prefix name for the emote.
   * @param targetName Name of the target.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   * @throws InvalidEmoteException If the emote file is incorrectly formatted.
   */
  public String toSource(String prefix, String targetName)
    throws EmoteNotFoundException, InvalidEmoteException
  {
    return String.format(
      getEmoteFormat(prefix, FORMAT_TARGET_SELF), targetName
    );
  }

  /**
   * Formats an emote string with a target to send to the character who was
   * targeted.
   * @param prefix Partial prefix name for the emote.
   * @param sourceName Name of the character who initiated the emote.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   * @throws InvalidEmoteException If the emote file is incorrectly formatted.
   */
  public String toTarget(String prefix, String sourceName)
    throws EmoteNotFoundException, InvalidEmoteException
  {
    return String.format(
      getEmoteFormat(prefix, FORMAT_TARGET), sourceName
    );
  }

  /**
   * Formats an emote string to send to the room.
   * @param prefix Partial prefix name for the emote.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   * @throws InvalidEmoteException If the emote file is incorrectly formatted.
   */
  public String toRoom(String prefix)
    throws EmoteNotFoundException, InvalidEmoteException
  {
    return getEmoteFormat(prefix, FORMAT_ROOM);
  }

  /**
   * Formats an emote string with a target to send to the room.
   * @param prefix Partial prefix name for the emote.
   * @param source Name of the character who initiated the emote.
   * @param target Name of the character who was targeted.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   * @throws InvalidEmoteException If the emote file is incorrectly formatted.
   */
  public String toRoom(String prefix, String source, String target)
    throws EmoteNotFoundException, InvalidEmoteException
  {
    return String.format(
      getEmoteFormat(prefix, FORMAT_TARGET_ROOM), source, target
    );
  }

  // Emotes singelton
  static final Emotes instance = new Emotes();

  /**
   * @return The default Emotes instance.
   */
  public static Emotes getInstance() {
    return instance;
  }
}
