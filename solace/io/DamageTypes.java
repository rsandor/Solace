package solace.io;

import org.json.JSONException;
import org.json.JSONObject;
import solace.game.DamageType;
import solace.util.Log;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Asset manager for loading damage types from the game directory.
 */
public class DamageTypes extends AbstractAssetManager<DamageType> {
  private static final DamageTypes instance = new DamageTypes();
  public static DamageTypes getInstance() { return instance; }

  /**
   * Creates a new damage types asset manager.
   */
  DamageTypes() {
    super("damage-type", ".damage.json");
  }

  @Override
  public void reload() {
    Log.info("Loading damage types");
    try {
      load().forEach(path -> {
        String filename = String.valueOf(path);
        try {
          String json = new String(Files.readAllBytes(path));
          JSONObject object = new JSONObject(json);
          String name = object.getString("name");
          String category = object.getString("category");
          add(name, new DamageType(name, category));
        } catch (JSONException je) {
          Log.warn(String.format("Invalid JSON for damage type '%s', skipping.", filename));
          Log.warn(je.getMessage());
        } catch (IOException e) {
          Log.warn(String.format("Error loading damage type '%s', skipping.", filename));
        }
      });
    } catch (IOException e) {
      Log.error(String.format("Unable to load damage types: %s", e.getMessage()));
    }
  }
}
