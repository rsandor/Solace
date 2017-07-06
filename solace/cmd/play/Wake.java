package solace.cmd.play;

import solace.game.*;
import solace.net.*;
import solace.util.*;
import java.util.*;
import java.io.*;

/**
 * Allows the player wake up from sleeping.
 * @author Ryan Sandor Richards
 */
public class Wake extends PlayCommand {
  public Wake(solace.game.Character ch) {
    super("wake", ch);
  }

  public void run(Connection c, String []params) {
    PlayState state = character.getPlayState();
    Room room = character.getRoom();

    if (character.isStanding()) {
      character.sendln("You are already awake and standing.");
      return;
    }

    room.sendMessage(
      String.format("%s wakes and stands up.", character.getName()),
      character
    );
    character.sendln("You wake and stand up.");
    character.setStanding();
  }
}
