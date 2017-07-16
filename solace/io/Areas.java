package solace.io;

import solace.cmd.GameException;
import solace.game.Area;
import solace.game.MobileManager;
import solace.game.Room;
import solace.game.Shop;
import solace.util.Log;
import solace.io.xml.GameParser;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Loads areas for the game.
 * @author Ryan Sandor Richards
 */
public class Areas {
  private Map<String, Area> areas = new Hashtable<>();
  private Room defaultRoom;

  /**
   * Reloads all game areas.
   */
  public synchronized void reload() {
    Log.info("Loading areas");
    try {
      // Clean up existing shops
      areas.values().forEach(area -> area.getShops().forEach(Shop::destroy));

      // Clear the mobile manager
      MobileManager.getInstance().clear();

      // Load all areas
      Hashtable <String, Area> newAreas = new Hashtable<>();
      GameFiles.findAreas().map(String::valueOf).forEach(filename -> {
        try {
          Area area = GameParser.parseArea(filename);
          area.getRooms().forEach(Room::instantiate);
          if (newAreas.keySet().contains(area.getId())) {
            throw new Error(String.format("Duplicate area id '%s' encountered.", area.getId()));
          }
          Log.trace(String.format("Loaded area '%s' from '%s'", area.getId(), filename));
          newAreas.put(area.getId(), area);
        } catch (Throwable t) {
          Log.warn(String.format("Error loading area '%s', skipping.", filename));
          Log.warn(t.getMessage());
        }
      });

      if (newAreas.values().size() == 0) {
        Log.warn("No areas found in the game directory.");
        return;
      }

      areas = newAreas;
      defaultRoom = null;
      findDefaultRoom(newAreas);


      // Instantiate mobiles
      MobileManager.getInstance().instantiate();

      // Initialize shops
      areas.values().forEach(area -> area.getRooms().forEach(room -> {
        if (room.hasShop()) {
          room.getShop().initialize();
        }
      }));
    } catch (IOException e) {
      Log.error(String.format("Unable reload areas: %s", e.getMessage()));
    } catch (GameException ge) {
      Log.error(ge.getMessage());
    }
  }

  /**
   * Finds the default room in a list of areas.
   * @param areaHash Hashtable to search for the default area.
   */
  private void findDefaultRoom(
    Hashtable<String, Area> areaHash
  ) throws GameException {
    String aName = Config.get("world.default.area");
    if (aName == null) {
      throw new GameException(
        "Required configuration key 'world.default.area' does not exist."
      );
    }

    String rName = Config.get("world.default.room");
    if (rName == null) {
      throw new GameException(
        "Required configuration key 'world.default.room' does not exist."
      );
    }

    if (!areaHash.containsKey(aName)) {
      throw new GameException(
        "Default area with id '" + aName + "' does not exist."
      );
    }

    Area area = areaHash.get(aName);

    Room room = area.getRoom(rName);
    if (room == null) {
      throw new GameException(
        "Default room with id '" + rName + "' does not exist."
      );
    }

    defaultRoom = room;
  }

  /**
   * @return The default room for the game world.
   */
  public synchronized Room getDefaultRoom() {
    return defaultRoom;
  }

  /**
   * @param id An area id.
   * @return The <code>Area</code> associated with the id, or null if none
   *   exists.
   */
  public Area get(String id) {
    if (!areas.containsKey(id))
      return null;
    return areas.get(id);
  }

  // Areas instance.
  private static final Areas instance = new Areas();

  /**
   * @return The default Emotes instance.
   */
  public static Areas getInstance() { return instance; }
}
