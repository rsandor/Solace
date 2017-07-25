package solace.io;

import solace.cmd.GameException;
import solace.game.Area;
import solace.game.MobileManager;
import solace.game.Room;
import solace.game.Shop;
import solace.util.Log;
import solace.io.xml.GameParser;

import java.io.IOException;

/**
 * Loads areas for the game.
 * @author Ryan Sandor Richards
 */
public class Areas extends AbstractAssetManager<Area> {
  private static final Areas instance = new Areas();
  public static Areas getInstance() { return instance; }

  private Room defaultRoom = Room.THE_VOID;
  private Area defaultArea = Area.NULL;

  /**
   * Creates a new areas manager.
   */
  public Areas() {
    super("Areas", ".area.xml");
  }

  @Override
  public synchronized void reload() {
    Log.info("Loading areas");
    try {
      // Clean up existing shops
      forEach(area -> area.getShops().forEach(Shop::destroy));

      // Clear the mobile manager
      MobileManager.getInstance().clear();

      // Load all areas
      clear();
      load().map(String::valueOf).forEach(filename -> {
        try {
          Area area = GameParser.parseArea(filename);
          area.getRooms().forEach(Room::instantiate);
          if (has(area.getId())) {
            throw new Error(String.format("Duplicate area id '%s' encountered.", area.getId()));
          }
          Log.trace(String.format("Loaded area '%s' from '%s'", area.getId(), filename));
          add(area.getId(), area);
        } catch (Throwable t) {
          Log.warn(String.format("Error loading area '%s', skipping.", filename));
          Log.warn(t.getMessage());
        }
      });

      if (size() == 0) {
        Log.warn("No areas loaded from game directory.");
      }

      findDefaultRoom();

      // Instantiate mobiles
      MobileManager.getInstance().instantiate();

      // Initialize shops
      forEach(area -> area.getRooms().forEach(room -> {
        if (room.hasShop()) {
          room.getShop().initialize();
        }
      }));
    } catch (IOException e) {
      Log.error(String.format("Unable reload areas: %s", e.getMessage()));
    }
  }

  /**
   * Attempts to find and set the default room for the game.
   */
  private void findDefaultRoom() {
    try {
      // Determine the default room.
      defaultRoom = Room.THE_VOID;
      String defaultAreaName = Config.get("world.default.area");
      if (defaultAreaName == null) {
        throw new GameException(
          "Required configuration key 'world.default.area' does not exist."
        );
      }

      String defaultRoomName = Config.get("world.default.room");
      if (defaultRoomName == null) {
        throw new GameException(
          "Required configuration key 'world.default.room' does not exist."
        );
      }

      if (!has(defaultAreaName)) {
        throw new GameException(
          "Default area with id '" + defaultAreaName + "' does not exist."
        );
      }

      defaultArea = get(defaultAreaName);
      defaultRoom = defaultArea.getRoom(defaultRoomName);
      if (defaultRoom == null) {
        throw new GameException(
          "Default room with id '" + defaultRoomName + "' does not exist."
        );
      }
    } catch (Throwable t) {
      Log.error(String.format("Error finding default room: %s", t.getMessage()));
    }
  }

  /**
   * @return The default room for the game world.
   */
  public Room getDefaultRoom() { return defaultRoom; }

  /**
   * @return The default area for the game world.
   */
  public Area getDefaultArea() { return defaultArea; }
}
