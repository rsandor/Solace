package solace.game;
import java.util.*;
import solace.util.*;

/**
 * The TemplateFactory class is responsible for keeping track of all item and
 * mobile templates in the game. It can be used to instantiate a given template
 * into an actual game object.
 * @author Ryan Sandor Richards
 */
public class TemplateFactory {
  private static final TemplateFactory INSTANCE = new TemplateFactory();
  Hashtable<String, Template> items;
  Hashtable<String, Template> mobiles;

  /**
   * Constructs a new template factory.
   */
  private TemplateFactory() {
    items = new Hashtable<String, Template>();
    mobiles = new Hashtable<String, Template>();
  }

  /**
   * @return Singelton instance of the template factory.
   */
  public static TemplateFactory getInstance() {
    return INSTANCE;
  }

  /**
   * Adds an item template to the factory.
   * @param areaId Id of the area that defines the item.
   * @param id Id of the item template.
   * @param t The template.
   */
  public void addItemTemplate(String areaId, String id, Template t) {
    String globalId = areaId + '.' + id;
    items.put(globalId, t);
  }

  /**
   * Adds a mobile template to the factory.
   * @param areaId Id of the area that defines the mobile.
   * @param id Id of the mobile template.
   * @param t The template.
   */
  public void addMobileTemplate(String areaId, String id, Template t) {
    String globalId = areaId + '.' + id;
    Log.info(globalId);
    mobiles.put(globalId, t);
  }

  /**
   * Constructs an item from a template with the given id.
   * @param id Id of the template from which to derive the item.
   * @return An item with the same properties of the template but
   *  with a unique uuid.
   */
  public Item getItem(String id)
    throws TemplateNotFoundException
  {
    Template template = items.get(id);
    if (template == null)
      throw new TemplateNotFoundException(id);
    Item item = new Item();
    template.copyProperties(item);
    return item;
  }

  /**
   * Constructs an instance of the mobile with the given id.
   * @param id Id for the mobile to instantiate.
   */
  public Mobile getMobile(String id)
    throws TemplateNotFoundException
  {
    Template template = mobiles.get(id);
    if (template == null) {
      throw new TemplateNotFoundException(id);
    }
    Mobile mob = new Mobile();
    template.copyProperties(mob);
    return mob;
  }
}
