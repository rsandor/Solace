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
public class AccountHandler extends Handler {
  /**
   * Enumeration for the basic states handled by the parser.
   */
  private enum State {
    INIT, USER, CHARACTERS, CHARACTER, INVENTORY,
    EQUIPMENT, ITEM, PROPERTY, SKILLS, HOTBAR
  };

  // Instance variables
  Account account;
  solace.game.Character character;
  Area area;
  Item item;
  State state = State.INIT;
  State itemState = State.INIT;
  String propertyKey;
  StringBuffer propertyBuffer;

  /**
   * @return The area as a result of the parse, or <code>null</code> if no area
   *   could be or has yet been parsed.
   */
  public Object getResult() {
    return account;
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
    switch (state) {
      case INIT: state = startInit(name, attrs); break;
      case USER: state = startUser(name, attrs); break;
      case CHARACTERS: state = startCharacters(name, attrs); break;
      case CHARACTER: state = startCharacter(name, attrs); break;
      case INVENTORY: state = startInventory(name, attrs); break;
      case EQUIPMENT: state = startEquipment(name, attrs); break;
      case ITEM: state = startItem(name, attrs); break;
      case SKILLS: state = startSkills(name, attrs); break;
      case HOTBAR: state = startHotbar(name, attrs); break;
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void characters(char[] ch, int start, int length) {
    if (state == State.PROPERTY) {
      propertyBuffer.append(Strings.xmlCharacters(ch, start, length));
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void endElement(String uri, String localName, String name) {
    if (name.equals("user")) {
      state = State.INIT;
      return;
    }

    if (name.equals("characters")) {
      state = State.USER;
      return;
    }

    if (name.equals("character")) {
      account.addCharacter(character);
      state = State.CHARACTERS;
      return;
    }

    if (
      name.equals("inventory") ||
      name.equals("equipment") ||
      name.equals("skills") ||
      name.equals("hotbar")
    ) {
      state = State.CHARACTER;
      return;
    }

    if (name.equals("item") && itemState == State.INVENTORY) {
      character.addItem(item);
      state = State.INVENTORY;
      item = null;
      return;
    }

    if (name.equals("item") && itemState == State.EQUIPMENT) {
      try {
        character.equip(item);
      }
      catch (NotEquipmentException e) {
        // This shouldn't happen if the character saved correctly
        Log.error(
          "Could not equiped saved item: " +
          item.get("description.inventory")
        );
        e.printStackTrace();
      }

      state = State.EQUIPMENT;
      return;
    }

    if (name.equals("property")) {
      if (item == null) {
        Log.error("Null item when setting property");
      }
      else {
        item.set(propertyKey, propertyBuffer.toString().trim());
      }
      state = State.ITEM;
      return;
    }
  }

  /**
   * Handles start elements for the initial state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startInit(String name, Attributes attrs) {
    if (!name.equals("user")) {
      return State.INIT;
    }
    String accountName = attrs.getValue("name");
    String admin = attrs.getValue("admin");
    String password = attrs.getValue("password");
    account = new Account(accountName, password, Boolean.parseBoolean(admin));
    return State.USER;
  }

  /**
   * Handles start elements for the user state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startUser(String name, Attributes attrs) {
    if (!name.equals("characters")) {
      return State.USER;
    }
    return State.CHARACTERS;
  }

  /**
   * Handles start elements for the characters state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startCharacters(String name, Attributes attrs) {
    if (!name.equals("character")) {
      return State.CHARACTERS;
    }
    String characterName = attrs.getValue("name");
    character = new solace.game.Character(characterName);

    character.setGold(0);
    if (attrs.getValue("gold") != null) {
      character.setGold(Long.parseLong(attrs.getValue("gold")));
    }

    character.setLevel(1);
    if (attrs.getValue("level") != null) {
      character.setLevel(Integer.parseInt(attrs.getValue("level")));
    }

    character.setHp(character.getMaxHp());
    if (attrs.getValue("hp") != null) {
      character.setHp(Integer.parseInt(attrs.getValue("hp")));
    }

    character.setMp(character.getMaxMp());
    if (attrs.getValue("mp") != null) {
      character.setMp(Integer.parseInt(attrs.getValue("mp")));
    }

    character.setSp(character.getMaxSp());
    if (attrs.getValue("sp") != null) {
      character.setSp(Integer.parseInt(attrs.getValue("sp")));
    }

    character.setMajorStat("strength");
    if (attrs.getValue("major-stat") != null) {
      character.setMajorStat(attrs.getValue("major-stat"));
    }

    character.setMinorStat("vitality");
    if (attrs.getValue("minor-stat") != null) {
      character.setMinorStat(attrs.getValue("minor-stat"));
    }

    character.setStanding();
    if (attrs.getValue("play-state") != null) {
      character.setPlayState(
        PlayState.fromString(attrs.getValue("play-state"))
      );
    }

    if (attrs.getValue("prompt") != null) {
      character.setPrompt(attrs.getValue("prompt"));
    }

    return State.CHARACTER;
  }

  /**
   * Handles start elements for the character state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startCharacter(String name, Attributes attrs) {
    if (name.equals("location")) {
      area = World.getArea(attrs.getValue("area"));
      character.setRoom(area.getRoom(attrs.getValue("room")));
    } else if (name.equals("inventory")) {
      return State.INVENTORY;
    } else if (name.equals("equipment")) {
      return State.EQUIPMENT;
    } else if (name.equals("skills")) {
      return State.SKILLS;
    } else if (name.equals("hotbar")) {
      return State.HOTBAR;
    }
    return State.CHARACTER;
  }

  /**
   * Handles start elements for the inventory state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startInventory(String name, Attributes attrs) {
    if (!name.equals("item")) {
      return State.INVENTORY;
    }
    itemState = State.INVENTORY;
    setItemFromAttrs(attrs);
    return State.ITEM;
  }

  /**
   * Handles start elements for the equipment state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startEquipment(String name, Attributes attrs) {
    if (!name.equals("item")) {
      return State.EQUIPMENT;
    }
    itemState = State.EQUIPMENT;
    setItemFromAttrs(attrs);
    return State.ITEM;
  }

  /**
   * Handles start elements for the item state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startItem(String name, Attributes attrs) {
    if (!name.equals("property")) {
      return State.ITEM;
    }
    propertyKey = attrs.getValue("key");
    propertyBuffer = new StringBuffer();
    return State.PROPERTY;
  }

  /**
   * Handles start elements for the skills state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startSkills(String name, Attributes attrs) {
    if (name.equals("skill")) {
      String id = attrs.getValue("id");
      String level = attrs.getValue("level");

      if (level == null) {
        Log.error(String.format(
          "Skill %s missing level, setting to 1.",
          id
        ));
        level = "1";
      }

      try {
        character.addSkill(id, Integer.parseInt(level));
      }
      catch (SkillNotFoundException snfe) {
        Log.error(String.format(
          "Skill with id %s could not be found, skipping.", id
        ));
      }
      catch (NumberFormatException nfe) {
        Log.error(String.format(
          "Could not parse skill level '%s' for skill %s and character %s",
          level,
          id,
          character.getName()
        ));
      }
    }
    return State.SKILLS;
  }

  /**
   * Handles start elements for the hotbar state.
   * @param name Name of the element.
   * @param attrs Attributes for the element.
   */
  protected State startHotbar(String name, Attributes attrs) {
    if (name.equals("entry")) {
      String key = attrs.getValue("key");
      String command = attrs.getValue("command");
      character.setHotbarCommand(key, command);
    }
    return State.HOTBAR;
  }

  /**
   * Sets the current item based to a new item based on the given attributes.
   * @param attrs Attributes for the item.
   */
  protected void setItemFromAttrs(Attributes attrs) {
    String uuid = attrs.getValue("uuid");
    String id = attrs.getValue("id");
    String names = attrs.getValue("names");
    String areaId = attrs.getValue("area");

    Log.trace(String.format(
      "AccountHandler: creating item from attributes: '%s' '%s' '%s' '%s'",
      uuid, id, names, areaId
    ));

    item = new Item(id, names, World.getArea(areaId));
    item.setUUID(uuid);
  }
}
