package solace.cmd.play;

import solace.game.*;
import solace.net.*;
import solace.util.*;
import java.util.*;
import java.io.*;

/**
 * Allows the player to rest. Resting allows a player to be aware of the game
 * world while increasing the regeneration rate of resource stats such as HP.
 * @author Ryan Sandor Richards
 */
public class Rest extends PlayCommand {
  public Rest(solace.game.Character ch) {
    super("rest", ch);
  }

  public boolean run(Connection c, String []params) {
    PlayState state = character.getPlayState();
    Room room = character.getRoom();

    if (state == PlayState.RESTING) {
      character.sendln("You are already resting.");
      return false;
    }

    if (state == PlayState.FIGHTING) {
      character.sendln("You cannot rest while fighting!");
      return false;
    }

    String characterMessage = "";
    String roomFormat = "";

    if (state == PlayState.SITTING) {
      characterMessage = "You lie back and rest.";
      roomFormat = "%s lies back and rests.";
    } else if (state == PlayState.SLEEPING) {
      characterMessage = "You awake and begin resting.";
      roomFormat = "%s wakes and begins resting.";
    } else if (state == PlayState.STANDING) {
      characterMessage = "You sit down, lie back, and begin resting.";
      roomFormat = "%s sits, lies back, and begins to rest.";
    }

    room.sendMessage(
      String.format(roomFormat, character.getName()),
      character
    );
    character.sendln(characterMessage);
    character.setPlayState(PlayState.RESTING);

    return true;
  }
}
