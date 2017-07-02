package solace.game;

import java.util.*;

/**
 * Base class for all types of game objects that can be generated via a generic
 * template. Examples of such objects include items, equipment, and mobiles.
 * @author Ryan Sandor Richards
 */
public class Template implements Comparable<Template> {
  Area area = null;
  String id = null;
  String uuid = null;
  List<String> names = null;
  Hashtable<String, String> properties = new Hashtable<String, String>();

  /**
   * Default constructor for a templatable object.
   */
  public Template() {
    area = null;
    id = null;
  }

  /**
   * Compares two templates by their uuid.
   * @param t Template to compare.
   * @return 0 if the templates are the same, -1 or 1 otherwise.
   */
  public int compareTo(Template t) {
    return uuid.compareTo(t.getUUID());
  }

  /**
   * Creates a new templatable object that was defined in the given area with
   * the given id and names.
   * @param area Area that defined the templatable object.
   * @param id Id of the object.
   * @param names Space delimited names for the object.
   */
  public Template(String id, String names, Area area) {
    this.area = area;
    this.id = id;
    setNames(names);
  }

  /**
   * Copies the properties of one template to another.
   * @param source Source template from which to fetch properties.
   * @param destination Destination template for which to set properties.
   */
  public static void copyTemplate(Template source, Template destination) {
    source.copyProperties(destination);
  }

  /**
   * Copies the properties of this object to another and provides
   * the new object a new uuid.
   * @param t Object to recieve copied properties.
   */
  protected void copyProperties(Template t) {
    t.setArea(area);
    t.setId(id);
    t.setNames(names);
    for (String key : properties.keySet())
      t.set(key, properties.get(key));
    t.randomUUID();
  }

  /**
   * Generates a new random UUID for this object.
   */
  protected void randomUUID() {
    uuid = UUID.randomUUID().toString();
  }

  /**
   * @return The object's game UUID.
   */
  public String getUUID() {
    return uuid;
  }

  /**
   * Set's the object's game UUID.
   */
  public void setUUID(String uuid) {
    this.uuid = uuid;
  }

  /**
   * @return The object's id.
   */
  public String getId() { return id; }

  /**
   * Sets the object's id.
   * @param id Id to set for the item.
   */
  public void setId(String s) { id = s; }

  /**
   * @return The object's names.
   */
  public List<String> getNames() { return names; }

  /**
   * Sets the list of names for the object given a space
   * delimeted string.
   * @param s Space delimeted string of names for the object.
   */
  public void setNames(String s) {
    names = Arrays.asList(s.split("\\s+"));
  }

  /**
   * Sets the list of names for the object given an array of names.
   * @param a Array of name for the object.
   */
  public void setNames(String[] a) {
    names = Arrays.asList(a);
  }

  /**
   * Sets the list of names for object given a collection of names.
   * @param c Collection of names for the object.
   */
  public void setNames(Collection<String> c) {
    names = new LinkedList<String>();
    for (String s : c) {
      names.add(s);
    }
  }

  /**
   * Determines if the templated object has a name which begins with
   * the given prefix.
   * @param prefix Prefix to apply when searching names.
   * @return True if the templated object has such a name, false otherwise.
   */
  public boolean hasName(String prefix) {
    for (String name : names)
      if (name.startsWith(prefix))
        return true;
    return false;
  }

  /**
   * @return The area where the object originates.
   */
  public Area getArea() {
    return area;
  }

  /**
   * Sets the area for an object.
   * @param area Area for the object.
   */
  public void setArea(Area area) {
    this.area = area;
  }

  /**
   * @return the hashtable containing each of the
   * properties for this templatable object.
   */
  public Hashtable<String, String> getProperties() {
    return properties;
  }

  /**
   * Sets a property for the object.
   * @param key Key for the property.
   * @param value Value of the property.
   */
  public void set(String key, String value) {
    properties.put(key, value);
  }

  /**
   * Gets a property of the object.
   * @param key Key of the property to retrieve.
   * @return The value of the property.
   */
  public String get(String key) {
    return properties.get(key);
  }

  /**
   * Gets a property of the template as an integer. If the value is null or
   * fails to parse then this will return 0.
   * @param key Key for the property to retrieve.
   * @return The property as an integer, or 0 if it could not be converted.
   */
  public int getInt(String key) {
    return getInt(key, 0);
  }

  /**
   * Attempts to get the value for the given key as an integer, but returns the
   * given default if the property could not be parsed.
   * @param key Name of the property to fetch.
   * @param def Default value for the property.
   * @return The value, or the default if a value could not be parsed.
   */
  public int getInt(String key, int def) {
    try {
      String value = get(key);
      return (value != null) ? Integer.parseInt(value) : def;
    }
    catch (NumberFormatException nfe) {
      return def;
    }
  }
}
