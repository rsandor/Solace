package solace.game;

import java.util.*;

/**
 * Holds information for areas.
 * @author Ryan Sandor Richards
 */
public class Area {
  public static final Area NULL = new Area("", "null area", "");

  String id = "";
  private String title = "";
  private String author = "";
  private Hashtable<String, Room> rooms = new Hashtable<>();
  private List<Shop> shops = new LinkedList<>();

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
  @SuppressWarnings("unused")
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
  @SuppressWarnings("WeakerAccess")
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
   * @param i The id to set for the area.
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
  @SuppressWarnings("unused")
  public void setTitle(String t) { title = t; }

  /**
   * @return The area's author.
   */
  public String getAuthor() { return author; }

  /**
   * @param a The area's author.
   */
  public void setAuthor(String a) { author = a; }
}
