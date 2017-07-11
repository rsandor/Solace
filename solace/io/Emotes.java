package solace.io;

import org.json.JSONException;
import org.json.JSONObject;
import solace.util.EmoteNotFoundException;
import solace.util.Log;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Loads and formats emotes for the game.
 * @author Ryan Sandor Richards
 */
public class Emotes {
  /**
   * Java representation of emote JSON data.
   * @author Ryan Sandor Richards
   */
  private class EmoteData {
    private String name;
    private String toPlayer;
    private String toRoom;
    private String toPlayerWithTarget;
    private String toTarget;
    private String toRoomWithTarget;

    /**
     * Creates a new emote data entry.
     * @param n Name of the emote.
     * @param p Format to use when sending emote text to a player.
     * @param r Format to use when sending emote text to a room.
     * @param pt Format to use when sending emote text to a player when the emote is targeted.
     * @param t Format to use when sending emote text to the target of the emote.
     * @param rt Format to use when sending emote text to a room when the emote is targeted.
     */
    EmoteData(String n, String p, String r, String pt, String t, String rt) {
      name = n;
      toPlayer = p;
      toRoom = r;
      toPlayerWithTarget = pt;
      toTarget = t;
      toRoomWithTarget = rt;
    }

    /**
     * @return The name of the emote.
     */
    String getName() { return name; }

    /**
     * @return The format to use when sending emote messages to the player.
     */
    String getToPlayer() { return toPlayer; }

    /**
     * @return The format to use when sending the emote message to a room.
     */
    String getToRoom() { return toRoom; }

    /**
     * @return The format to use when sending emote messages to the player when the emote is targeted.
     */
    String getToPlayerWithTarget() { return toPlayerWithTarget; }

    /**
     * @return The format to use when sending emote messages to the target.
     */
    String getToTarget() { return toTarget; }

    /**
     * @return The format to use when sending emote messages to the room when the emote is targeted.
     */
    String getToRoomWithTarget() { return toRoomWithTarget; }
  }

  private final Hashtable<String, EmoteData> emotes = new Hashtable<>();

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
    Log.info("Reloading emotes");
    GameFiles.findEmotes().forEach(this::loadEmote);
  }

  /**
   * Parse the emote data located at the given path and adds it to the list of emotes.
   * @param path Path for the emote JSON file.
   */
  public void loadEmote (Path path) {
    String filename = path.getFileName().toString();
    try {
      String json = new String(Files.readAllBytes(path));

      JSONObject object = new JSONObject(json);
      String name = object.getString("name");

      String toPlayer = object.getString("toPlayer");
      String toRoom = object.getString("toRoom");

      JSONObject withTarget = object.getJSONObject("withTarget");
      String toPlayerWithTarget = withTarget.getString("toPlayer");
      String toTarget = withTarget.getString("toTarget");
      String toRoomWithTarget = withTarget.getString("toRoom");

      addEmoteData(new EmoteData(name, toPlayer, toRoom, toPlayerWithTarget, toTarget, toRoomWithTarget));
    } catch (JSONException je) {
      Log.warn(String.format("Invalid JSON for emote '%s', skipping.", filename));
      Log.warn(je.getMessage());
    } catch (IOException e) {
      Log.warn(String.format("Error loading emote '%s', skipping.", filename));
    }
  }

  /**
   * Adds emote data to the emotes list.
   * @param data Emote data to add.
   */
  private void addEmoteData(EmoteData data) {
    emotes.put(data.getName(), data);
  }

  /**
   * @return A collection of emote names for use with the emote command.
   */
  public String[] getEmoteAliases() { return emotes.keySet().toArray(new String[0]); }

  /**
   * Finds an emote that matches the given prefix.
   * @param prefix Prefix to find.
   * @return The name of the emote that matches, or null if none was found.
   */
  private EmoteData findEmote(String prefix)
    throws EmoteNotFoundException
  {
    for (String name : emotes.keySet()) {
      if (name.startsWith(prefix)) {
        return emotes.get(name);
      }
    }
    throw new EmoteNotFoundException(String.format("Emote with prefix '%s' not found.", prefix));
  }

  /**
   * Formats an emote string to send to the source.
   * @param prefix Partial prefix name for the emote.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   */
  public String toSource(String prefix) throws EmoteNotFoundException {
    return findEmote(prefix).getToPlayer();
  }

  /**
   * Formats an emote string with a target to send to the character who executed
   * the command.
   * @param prefix Partial prefix name for the emote.
   * @param targetName Name of the target.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   */
  public String toSource(String prefix, String targetName) throws EmoteNotFoundException {
    return String.format(findEmote(prefix).getToPlayerWithTarget(), targetName);
  }

  /**
   * Formats an emote string with a target to send to the character who was
   * targeted.
   * @param prefix Partial prefix name for the emote.
   * @param sourceName Name of the character who initiated the emote.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   */
  public String toTarget(String prefix, String sourceName) throws EmoteNotFoundException {
    return String.format(findEmote(prefix).getToTarget(), sourceName);
  }

  /**
   * Formats an emote string to send to the room.
   * @param prefix Partial prefix name for the emote.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   */
  public String toRoom(String prefix, String sourceName) throws EmoteNotFoundException {
    return String.format(findEmote(prefix).getToRoom(), sourceName);
  }

  /**
   * Formats an emote string with a target to send to the room.
   * @param prefix Partial prefix name for the emote.
   * @param source Name of the character who initiated the emote.
   * @param target Name of the character who was targeted.
   * @return The formatted emote string.
   * @throws EmoteNotFoundException If an emote with the given prefix could not
   *   be found.
   */
  public String toRoom(String prefix, String source, String target) throws EmoteNotFoundException {
    return String.format(findEmote(prefix).getToRoomWithTarget(), source, target);
  }

  // Emotes singleton
  private static final Emotes instance = new Emotes();

  /**
   * @return The default Emotes instance.
   */
  public static Emotes getInstance() { return instance; }
}
