package solace.io;

import solace.game.Area;
import solace.game.Room;
import solace.util.Log;
import solace.io.xml.GameParser;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Loads areas for the game.
 * @author Ryan Sandor Richards
 */
public class Areas {
  /**
   * Reloads all game areas.
   * @return The loaded areas.
   * @throws IOException If an error occurred when loading areas.
   */
  public Collection<Area> reload() throws IOException {
    Collection<Area> areas = new LinkedList<>();
    GameFiles.findAreas().map(String::valueOf).forEach(filename -> {
      try {
        Area area = GameParser.parseArea(filename);
        area.getRooms().stream().forEach(Room::instantiate);
        Log.debug(String.format("Area '%s' loaded from '%s'", area.getId(), filename));
        areas.add(area);
      } catch (Throwable t) {
        Log.warn(String.format("Error loading area '%s', skipping.", filename));
        Log.warn(t.getMessage());
      }
    });
    return areas;
  }

  // Areas instance.
  private static final Areas instance = new Areas();

  /**
   * @return The default Emotes instance.
   */
  public static Areas getInstance() { return instance; }
}
