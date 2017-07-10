package solace.util;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Hashtable;

/**
 * The message manager for the game. Messages are things like the introduction
 * screen, or any other "static" information that is displayed to users by the
 * game.
 * @author Ryan Sandor Richards
 */
public class Messages {
  private static Hashtable<String, String> messages = new Hashtable<>();

  /**
   * Reloads messages on the fly.
   */
  public static void reload() throws IOException {
    Log.info("Reloading game messages");
    messages.clear();
    GameFiles.findMessages().forEach(path -> {
      String filename = String.valueOf(path);
      try {
        String name = String.valueOf(path.getFileName()).replace(".message.txt","");
        Log.debug(String.format("Loading message '%s'", name));
        messages.put(name, new String(Files.readAllBytes(path)));
      } catch (IOException e) {
        Log.warn(String.format("Error loading message '%s', skipping.", filename));
      }
    });
    if (messages.values().size() == 0) {
      Log.warn("No game messages found.");
    }
  }

  /**
   * Retrieves a message contents.
   * @param name Name of the message.
   */
  public static String get(String name) {
    String msg = messages.get(name);
    if (msg == null) {
      Log.error(String.format("Unable to load message with name: %s", name));
      return "";
    }
    return msg.replace("\n", "\n\r");
  }
}
