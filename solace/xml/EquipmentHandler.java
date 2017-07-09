package solace.xml;

import org.xml.sax.*;
import solace.util.*;
import java.util.*;

/**
 * Handler for parsing the game's equipment slot configuration file.
 * @author Ryan Sandor Richards.
 */
public class EquipmentHandler extends Handler {
  enum State { INIT, EQUIPMENT }

  private List<String> slots = new LinkedList<>();
  private State state = State.INIT;

  @Override
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
        if (name.length() == 0) {
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

  @Override
  public void endElement(String uri, String localName, String name) {
    if (name.equals("equipment")) {
      state = State.INIT;
    }
  }

  @Override
  public Object getResult() {
    return slots;
  }
}
