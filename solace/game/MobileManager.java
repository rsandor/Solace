package solace.game;

import solace.game.Mobile;
import solace.game.Room;
import solace.util.Log;

import java.util.*;

/**
 * Global utility class for managing, placing, and plucking mobiles in the game
 * world.
 * @author Ryan Sandor Richards
 */
public class MobileManager {
  private class MobileInstance {
    private String mobileId;
    private Room room;

    public MobileInstance(String id, Room r) {
      mobileId = id;
      room = r;
    }

    public void instantiate() {
      try {
        Mobile mobile = TemplateFactory.getInstance().getMobile(mobileId);
        MobileManager.getInstance().add(mobile);
        mobile.place(room);
      }
      catch (TemplateNotFoundException e) {
        Log.error(
          "Could not instantiate mobile with given id: " + mobileId
        );
      }
    }
  }

  List<Mobile> mobiles;
  List<MobileInstance> instances;

  /**
   * Creates a new mobile manager.
   */
  public MobileManager() {
    mobiles = Collections.synchronizedList(new LinkedList<Mobile>());
    instances = Collections.synchronizedList(new LinkedList<MobileInstance>());
  }

  /**
   * Adds a mobile to the manager.
   * @param m Mobile to add.
   */
  public void add(Mobile m) {
    mobiles.add(m);
  }

  /**
   * Adds a moile to the manager and places it in the game world.
   * @param m Mobile to add and place.
   * @param r Room in which to place the mobile.
   */
  public void addAndPlace(Mobile m, Room r) {
    mobiles.add(m);
    m.place(r);
  }

  /**
   * Removes a mobile from the game world.
   * @param m Mobile to remove.
   */
  public void remove(Mobile m) {
    mobiles.remove(m);
    m.pluck();
  }

  public void addInstance(String mobileId, Room room) {
    instances.add(new MobileInstance(mobileId, room));
  }

  /**
   * Instantiates all pending mobile instances.
   */
  public void instantiate() {
    synchronized(instances) {
      for (MobileInstance i : instances) {
        i.instantiate();
      }
      instances.clear();
    }
  }

  /**
   * Singelton instance for the mobile manager.
   */
  private static MobileManager instance = new MobileManager();

  /**
   * @return The singelton instance for the mobile manager.
   */
  public static MobileManager getInstance() {
    return instance;
  }
}
