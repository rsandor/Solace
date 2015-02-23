package solace.game;
import java.util.*;

/**
 * Represents a type of item in the game world. It is important to note
 * that this is not a particular instance of an item (i.e. one that would
 * be carried by a character or placed in a room), but simply the the
 * classification for that type of item.
 * @author Ryan Sandor Richards
 */
public class Item {
    String id = null;
    List<String> names = null;
    Area area = null;
    Hashtable<String, String> properties = new Hashtable<String, String>();

    /**
     * Creates a new item with the given id, names, and associated area.
     * @param id Id of the item.
     * @param names Names for the item.
     * @param area Area to which the item belongs.
     */
    public Item(String id, String names, Area area) {
        this.id = id;
        setNames(names);
        this.area = area;
    }

    /**
     * @return The item's id.
     */
    public String getId() { return id; }

    /**
     * Sets the item's id.
     * @param id Id to set for the item.
     */
    public void setId(String s) { id = s; }

    /**
     * @return The item's names.
     */
    public List<String> getNames() { return names; }

    /**
     * Sets the list of names for the item given a space
     * delimeted string.
     * @param s Space delimeted string of names for the item.
     */
    public void setNames(String s) {
        names = Arrays.asList(s.split("\\s+"));
    }

    /**
     * Sets the list of names for the item given an array of names.
     * @param a Array of name for the item.
     */
    public void setNames(String[] a) {
        names = Arrays.asList(a);
    }

    /**
     * Sets the list of names for item given a collection of names.
     * @param c Collection of names for the item.
     */
    public void setNames(Collection<String> c) {
        names = new LinkedList<String>();
        for (String s : c) {
            names.add(s);
        }
    }

    /**
     * @return The area where the item originates.
     */
    public Area getArea() {
        return area;
    }

    /**
     * Sets the area for an item.
     * @param area Area for the item.
     */
    public void setArea(Area area) {
        this.area = area;
    }

    /**
     * Sets a property for the item.
     * @param key Key for the property.
     * @param value Value of the property.
     */
    public void set(String key, String value) {
        properties.put(key, value);
    }

    /**
     * Gets a property of the item.
     * @param key Key of the property to retrieve.
     * @return The value of the property.
     */
    public String get(String key) {
        return properties.get(key);
    }
}
