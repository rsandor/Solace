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
 * @author Ryan Sandor Richards.
 */
public class AreaHandler extends Handler {
  static Area area = null;
  static Room room = null;
  static Exit exit = null;

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
