package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * Scan command, shows players and mobiles in adjacent rooms.
 * @author Ryan Sandor Richards
 */
public class Scan extends PlayCommand {
  public Scan(solace.game.Character ch) {
    super("scan", ch);
  }

  public boolean run(Connection c, String []params) {
    PlayState state = character.getPlayState();

    if (state == PlayState.SITTING || state == PlayState.RESTING) {
      character.sendln("You cannot scan around unless standing.");
      return false;
    }

    if (state == PlayState.FIGHTING) {
      character.sendln("You are too busy to scan while engaged in combat!");
      return false;
    }

    if (state == PlayState.SLEEPING) {
      character.sendln("You cannot scan whilst asleep.");
      return false;
    }

    Room room = character.getRoom();
    Area area = room.getArea();
    List<Exit> exits = room.getExits();
    String message = "";

    for (Exit ex : exits) {
      Room r = area.getRoom(ex.getToId());

      if (r.getCharacters().size() == 0)
        continue;

      message += ex.getDescription().trim() + ":\n\r";

      synchronized(r.getCharacters()) {
        for (Player ch : r.getCharacters()) {
          message += "  " + ch.getName() + "\n\r";
        }
      }
    }

    if (message.equals("")) {
      message = "There is nobody of interest in any direction.";
    }

    // Tell the other players in the room what the character is doing
    room.sendMessage(
      character.getName() + " scans the surrounding area.",
      character
    );

    c.sendln(message);

    return true;
  }
}
