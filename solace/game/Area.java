package solace.game;

import java.util.*;

/**
 * Holds information pretaining to an area.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Area
{
  String id = "";
  String title = "";
  String author = "";
  Hashtable<String, Room> rooms = new Hashtable<String, Room>();
  List<Shop> shops = new LinkedList<Shop>();
  Collection<Mobile> mobiles;

  /**
   * Creates a new area with the given name and name of the creator.
   * @param i The area's id.
   * @param t The area's title.
   * @param a The area's author.
   */
  public Area(String i, String t, String a) {
    id = i;
    title = t;
    author = a;
    mobiles = Collections.synchronizedCollection(new LinkedList<Mobile>());
  }

  /**
   * @return A collection of all mobiles in this area.
   */
  public Collection<Mobile> getMobiles() {
    return mobiles;
  }

  /**
   * Adds a room to the area.
   * @param r Room to add.
   */
  public void addRoom(Room r) {
    rooms.put(r.getId(), r);
    r.setArea(this);
  }

  /**
   * Removes a room from this area.
   * @param r Room to remove.
   */
  public void removeRoom(Room r) {
    rooms.remove(r.getId());
  }

  /**
   * @param id Id of the room to fetch.
   * @return The room with the given id, or null if none exists.
   */
  public Room getRoom(String id) {
    return rooms.get(id);
  }

  /**
   * Returns a collection of the rooms in this area.
   * @return A collection of the rooms in this area.
   */
  public Collection<Room> getRooms() {
    return rooms.values();
  }

  /**
   * @return A collection of shops associated with the area.
   */
  public Collection<Shop> getShops() {
    return shops;
  }

  /**
   * @return The area's id.
   */
  public String getId() { return id; }

  /**
   * @param name The id to set for the area.
   */
  public void setId(String i) { id = i; }

  /**
   * @return The area's title.
   */
  public String getTitle() { return title; }

  /**
   * Sets the area's title.
   * @param t The title to set.
   */
  public void setTitle(String t) { title = t; }

  /**
   * @return The area's author.
   */
  public String getAuthor() { return author; }

  /**
   * @param author The area's author.
   */
  public void setAuthor(String a) { author = a; }
}
