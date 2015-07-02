package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import solace.util.*;
import java.util.*;

/**
 * Handler for parsing the game's equipment slot configuration file.
 * @author Ryan Sandor Richards.
 */
public class EquipmentHandler extends Handler {
  enum State { INIT, EQUIPMENT }

  List<String> slots = new LinkedList<String>();
  State state = State.INIT;

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void startElement(
    String uri,
    String localName,
    String name,
    Attributes attrs
  ) {
    if (state == State.INIT) {
      if (name.equals("equipment")) {
        state = State.EQUIPMENT;
      }
    }
    else if (state == State.EQUIPMENT) {
      if (name.equals("slot")) {
        String slot = attrs.getValue("name");
        if (name == null) {
          Log.warn("Equipment slot not given name, skipping.");
          return;
        }
        // Would use a set but I want to preserve order
        if (!slots.contains(slot)) {
          slots.add(slot);
        }
      }
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void endElement(String uri, String localName, String name) {
    if (name.equals("equipment")) {
      state = State.INIT;
    }
  }

  /**
   * @return The equipment slots found by parsing the config file.
   */
  public Object getResult() {
    return slots;
  }
}
