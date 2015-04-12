package solace.util;

import java.util.*;
import java.io.*;

/**
 * The message manager for the game. Messages are things like the introduction
 * screen, or any other "static" information that is displayed to users by the
 * game.
 * @author Ryan Sandor Richards
 */
public class Message
{
  static final String messageDir = "data/messages/";
  static Hashtable<String, String> messages = new Hashtable<String, String>();

  /**
   * Loads messages into the manager.
   */
  public static void load() throws IOException
  {
    File dir = new File(messageDir);
    File []messageFiles = dir.listFiles();
    for (int i = 0; i < messageFiles.length; i++)
    {
      String name = messageFiles[i].getName();
      StringBuffer contents = new StringBuffer("");

      try {
        BufferedReader in = new BufferedReader(new FileReader(messageFiles[i]));
        String line = in.readLine();
        while (line != null)
        {
          contents.append(line   + "\n");
          line = in.readLine();
        }
        messages.put(name, new String(contents));
      }
      catch (IOException ioe)
      {
        Log.error(
          "Error loading message " + name + " from the messages directory, '" +
          messageDir + "'."
        );
      }
    }
  }

  /**
   * Reloads messages on the fly.
   */
  public static void reload() throws IOException {
    messages.clear();
    load();
    Log.info("Game Messages Reloaded");
  }

  /**
   * Retrieves a message contents.
   * @param name Name of the message.
   */
  public static String get(String name) {
    return messages.get(name).replace("\n", "\n\r");
  }
}
