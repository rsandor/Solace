package solace.io;

import solace.util.Log;
import solace.util.Roll;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Asset manager for loading dreams from the game directory into the game.
 */
public class Dreams extends AbstractAssetManager<String> {
  private static final Dreams instance = new Dreams();
  public static Dreams getInstance() { return instance; }

  /**
   * Creats a new dreams asset manager.
   */
  Dreams() {
    super("dream", ".dream.txt");
  }

  /**
   * @return The text for a random dream from those that are loaded.
   */
  public String getRandom() {
    List<String> dreams = getAll();
    return dreams.get(Roll.index(dreams.size()));
  }

  @Override
  public void reload() {
    try {
      load().forEach(path -> {
        String filename = String.valueOf(path);
        try {
          String name = filename.split("\\.dream\\.txt$")[0];
          String value = new String(Files.readAllBytes(path));
          add(name, value);
        } catch (Throwable t) {
          Log.warn(String.format("Error loading dream %s: %s", filename, t.getMessage()));
        }
      });
    } catch (IOException e) {
      Log.error(String.format("Unable load dreams: %s", e.getMessage()));
    }
  }
}
