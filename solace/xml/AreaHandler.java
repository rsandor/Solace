package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import solace.game.*;
import solace.util.*;
import java.util.*;

/**
 * Handles the parsing of area XML files.
 *
 * NOTE: This class uses quite a few static variables to get away with some
 *       trickery with how we use and override states. This means you cannot
 *       load two areas concurrently (via threading).
 *
 * @author Ryan Sandor Richards.
 */
public class AreaHandler extends Handler {
  static Area area = null;
  static Room room = null;
  static Exit exit = null;
  static Shop shop = null;

  static Template template = null;
  static String propertyKey = null;
  static TemplateFactory templates = TemplateFactory.getInstance();

  static StringBuffer description = null;
  static String descriptionNames = null;

  static Stack<StringBuffer> buffers = new Stack<StringBuffer>();

  /**
   * Enumeration for the basic states handled by the parser.
   */
  private enum State {
    INIT {
      public State start(String name, Attributes attrs) {
        if (name != "area")
          return INIT;

        String id = attrs.getValue("id").trim();
        String title = attrs.getValue("title").trim();
        String author = attrs.getValue("author").trim();

        if (id == null)
          id = "";
        if (title == null)
          title = "";
        if (author == null)
          author = "";

        area = new Area(id, title, author);
        return AREA;
      }

      public State end(String name) {
        return INIT;
      }
    },

    AREA() {
      public State start(String name, Attributes attrs) {
        if (name.equals("room")) {
          String id = attrs.getValue("id").trim();
          room = new Room(id);
          return ROOM;
        }
        else if (name.equals("item") || name.equals("mobile")) {
          String id = attrs.getValue("id");
          String names = attrs.getValue("names");
          template = new Template(id, names, area);

          if (name.equals("mobile")) {
            String state = attrs.getValue("state");
            if (state == null) {
              state = "stationary";
            }
            template.set("state", state);
          }

          return TEMPLATE;
        }

        return AREA;
      }

      public State end(String name) {
        return INIT;
      }
    },

    ROOM() {
      public State start(String name, Attributes attrs) {
        if (name.equals("title")) {
          buffers.push(new StringBuffer());
          return TITLE;
        }
        else if (name.equals("exit")) {
          String names = attrs.getValue("names");
          String to = attrs.getValue("to");
          exit = new Exit(names, to);
          return EXIT;
        }
        else if (name.equals("describe")) {
          description = new StringBuffer();
          descriptionNames = attrs.getValue("names");
          return ROOM_DESCRIBE;
        }
        else if (name.equals("instance")) {
          String type = attrs.getValue("type"),
            globalId = attrs.getValue("id");

          // Check if the given id represents a global id
          // if not, then make sure to prepend the local
          // area.
          if (globalId.indexOf(".") < 0) {
            globalId = area.getId() + "." + globalId;
          }

          if (type.startsWith("item")) {
            room.addItemInstance(globalId);
          }
          else if (type.startsWith("mobile")) {
            MobileManager.getInstance().addInstance(globalId, room);
          }
        }
        else if (name.equals("shop")) {
          String id = attrs.getValue("id");
          String shopName = attrs.getValue("name");
          String sell = attrs.getValue("sell");
          String buy = attrs.getValue("buy");

          // TODO Make the default buy and sell multipliers configurable in the
          //      data/world.txt file.

          if (sell == null) {
            sell = "1.5";
          }

          if (buy == null) {
            buy = "0.5";
          }

          double buyM, sellM;
          try {
            buyM = Double.parseDouble(buy);
            sellM = Double.parseDouble(sell);
          }
          catch (NumberFormatException nfe) {
            Log.error(
              "Error parsing a buy or sell multiplier: " + buy + ", " + sell +
              ". Setting default."
            );
            buyM = 0.5;
            sellM = 1.5;
          }

          shop = new Shop(id, shopName, room);
          shop.setSellMultiplier(sellM);
          shop.setBuyMultiplier(buyM);

          return SHOP;
        }

        return ROOM;
      }

      public State end(String name) {
        // TODO Check for duplicate ids
        if (name != "room")
          return ROOM;
        area.addRoom(room);
        return AREA;
      }
    },

    ROOM_DESCRIBE() {
      public State start(String name, Attributes attrs) {
        if (name.equals("exit")) {
          String color = Config.get("world.colors.room.exit");
          description.append((color == null) ? "" : color);
          return ROOM_DESCRIBE_FEATURE;
        }
        else if (name.equals("look")) {
          String color = Config.get("world.colors.room.look");
          description.append((color == null) ? "" : color);
          return ROOM_DESCRIBE_FEATURE;
        }

        return ROOM_DESCRIBE;
      }

      public void characters(String str) {
        description.append(str);
      }

      public State end(String name) {
        String descriptionStr = description.toString().trim()
          .replaceAll("\\s([,.;:])", "$1");
        if (descriptionNames == null) {
          room.setDescription(descriptionStr);
        }
        else {
          room.addFeature(descriptionNames, descriptionStr);
        }
        return ROOM;
      }
    },

    ROOM_DESCRIBE_FEATURE() {
      public void characters(String str) {
        description.append(str);
      }

      public State end(String name) {
        description.append("{x");
        return ROOM_DESCRIBE;
      }
    },

    TEMPLATE() {
      public State start(String name, Attributes attrs) {
        if (name.equals("property")) {
          propertyKey = attrs.getValue("key");
          buffers.push(new StringBuffer());
          return PROPERTY;
        }
        return TEMPLATE;
      }

      public State end(String name) {
        if (name.equals("item")) {
          templates.addItemTemplate(
            area.getId(),
            template.getId(),
            template
          );
        }
        else if (name.equals("mobile")) {
          templates.addMobileTemplate(
            area.getId(),
            template.getId(),
            template
          );
        }
        return AREA;
      }
    },

    EXIT() {
      public State end(String name) {
        // String desc = room.getDescription();
        // String endColor =
        //   Config.get("world.colors.room.exit") == null ? "" : "{x";
        // room.setDescription(desc.trim() + endColor);
        room.addExit(exit);
        return ROOM;
      }

      public void characters(String str) {
        exit.addToDescription(str);
      }
    },

    TITLE() {
      public State end(String name) {
        StringBuffer buffer = buffers.pop();
        room.setTitle(buffer.toString().trim());
        return ROOM;
      }

      public void characters(String str) {
        buffers.peek().append(str);
      }
    },

    PROPERTY() {
      public State end(String name) {
        StringBuffer buffer = buffers.pop();
        template.set(propertyKey, buffer.toString().trim());
        return TEMPLATE;
      }

      public void characters(String str) {
        buffers.peek().append(str);
      }
    },

    SHOP() {
      public State start(String name, Attributes attrs) {
        if (name.equals("stock")) {
          String itemId = attrs.getValue("id");
          if (itemId.indexOf(".") < 0) {
            itemId = area.getId() + "." + itemId;
          }

          if (itemId == null) {
            Log.error(String.format(
              "Unable to stock item in shop %s, missing id.", shop.getName()
            ));
            return SHOP;
          }

          // TODO Perhaps pull out the default values into constants on the
          //      ShopItem class.
          ShopItem shopItem = new ShopItem(shop, itemId);
          shopItem.setQuantity(
            parseAttr(attrs.getValue("quantity"), -1)
          );
          shopItem.setRestockInterval(
            parseAttr(attrs.getValue("restock"), -1)
          );
          shopItem.setRestockFrequency(
            parseAttr(attrs.getValue("restock-freq"), -1)
          );
          shopItem.setRestockAmount(
            parseAttr(attrs.getValue("restock-amount"), -1)
          );

          shop.addItem(shopItem);
        }

        return SHOP;
      }

      public State end(String name) {
        if (name.equals("shop")) {
          room.setShop(shop);
          area.getShops().add(shop);
          return ROOM;
        }
        return SHOP;
      }
    };

    State() {
    }

    public State start(String name, Attributes attrs) {
      return this;
    }

    public State end(String name) {
      return this;
    }

    public void characters(String str) {
    }
  }

  // Instance variables
  State state = State.INIT;

  /**
   * Helper function for parsing integers from attributes.
   * @param  a Attribute to parse.
   * @param  def Default value if parsting fails.
   * @return The parsed value, or the default if none could be parsed.
   */
  protected static int parseAttr(String a, int def) {
    if (a == null) {
      return def;
    }
    try {
      return Integer.parseInt(a);
    }
    catch (NumberFormatException nfe) {
      Log.error(String.format(
        "Value contains unparsable quantity %s.", a
      ));
      return def;
    }
  }

  /**
   * @return The area as a result of the parse, or <code>null</code> if no area
   * could be or has yet been parsed.
   */
  public Object getResult() {
    return AreaHandler.area;
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void startElement(
    String uri,
    String localName,
    String name,
    Attributes attrs
  ) {
    state = state.start(name, attrs);
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void endElement(String uri, String localName, String name) {
    state = state.end(name);
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void characters(char[] ch, int start, int length) {
    state.characters(Strings.xmlCharacters(ch, start, length));
  }
}
