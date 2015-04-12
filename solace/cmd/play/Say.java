package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The say command, allows players to speak to eachother in a given room.
 * @author Ryan Sandor Richards
 */
public class Say extends PlayCommand {
  public Say(solace.game.Character ch) {
    super("say", ch);
  }

  public boolean run(Connection c, String []params) {
    if (params.length < 2) {
      c.sendln("What would you like to say?");
      return false;
    }

    // Format the message
    String message = "'";
    for (int i = 1; i < params.length; i++) {
      message += params[i];
      if (i != params.length - 1)
        message += " ";
    }
    message += "'\n";

    // Broadcast to the room
    Room room = character.getRoom();
    synchronized(room.getCharacters()) {
      for (solace.game.Character ch : room.getCharacters()) {
        if (ch == character)
          c.sendln("You say " + message);
        else
          ch.sendMessage(character.getName() + " says " + message);
      }
    }

    return true;
  }
}
