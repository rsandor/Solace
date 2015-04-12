package solace.game;

/**
 * Represents a type of item in the game world. It is important to note that
 * this is not a particular instance of an item (i.e. one that would be carried
 * by a character or placed in a room), but simply the the classification for
 * that type of item.
 * @author Ryan Sandor Richards
 */
public class Item extends Template {
  public Item() {
    super();
  }

  public Item(String id, String names, Area area) {
    super(id, names, area);
  }
}
