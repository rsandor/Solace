package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import solace.game.*;
import java.util.*;

/**
 * Handles the parsing of area XML files.
 * @author Ryan Sandor Richards.
 */
public class AccountHandler extends Handler {
  /**
   * Enumeration for the basic states handled by the parser.
   */
  private enum State { INIT, USER, CHARACTERS, CHARACTER }

  // Instance variables
  Account account;
  solace.game.Character character;
  Area area;

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
    if (name == "user") {
      String accountName = attrs.getValue("name");
      String admin = attrs.getValue("admin");
      String password = attrs.getValue("password");
      account = new Account(accountName, password, Boolean.parseBoolean(admin));
    }
    else if (name == "character") {
      String characterName = attrs.getValue("name");
      character = new solace.game.Character(characterName);
      setStatsFromAttrs(attrs);
    }
    else if (name == "location") {
      area = World.getArea(attrs.getValue("area"));
      character.setRoom(area.getRoom(attrs.getValue("room")));
    }
  }

  protected void setStatsFromAttrs(Attributes attrs) {
    character.setLevel(1);
    if (attrs.getValue("level") != null) {
      character.setHp(Integer.parseInt(attrs.getValue("level")));
    }

    character.setHp(20);
    if (attrs.getValue("hp") != null) {
      character.setHp(Integer.parseInt(attrs.getValue("hp")));
    }

    character.setMaxHp(20);
    if (attrs.getValue("maxhp") != null) {
      character.setMaxHp(Integer.parseInt(attrs.getValue("maxhp")));
    }

    character.setMp(20);
    if (attrs.getValue("mp") != null) {
      character.setMp(Integer.parseInt(attrs.getValue("mp")));
    }

    character.setMaxMp(20);
    if (attrs.getValue("maxmp") != null) {
      character.setMaxMp(Integer.parseInt(attrs.getValue("maxmp")));
    }

    character.setSp(20);
    if (attrs.getValue("sp") != null) {
      character.setSp(Integer.parseInt(attrs.getValue("sp")));
    }

    character.setMaxSp(20);
    if (attrs.getValue("maxsp") != null) {
      character.setMaxSp(Integer.parseInt(attrs.getValue("maxsp")));
    }

    character.setStrength(8);
    if (attrs.getValue("strength") != null) {
      character.setStrength(Integer.parseInt(attrs.getValue("strength")));
    }

    character.setVitality(8);
    if (attrs.getValue("vitality") != null) {
      character.setVitality(Integer.parseInt(attrs.getValue("vitality")));
    }

    character.setMagic(8);
    if (attrs.getValue("magic") != null) {
      character.setMagic(Integer.parseInt(attrs.getValue("magic")));
    }

    character.setSpeed(8);
    if (attrs.getValue("speed") != null) {
      character.setSpeed(Integer.parseInt(attrs.getValue("speed")));
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void endElement(String uri, String localName, String name) {
    if (name == "character") {
      account.addCharacter(character);
    }
  }
}
