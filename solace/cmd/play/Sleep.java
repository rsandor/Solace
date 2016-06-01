package solace.cmd.play;

import solace.game.*;
import solace.net.*;
import solace.util.*;
import java.util.*;
import java.io.*;

/**
 * Allows the player to sleep. Sleeping dramatically increases the rate at which
 * resources generate, but at the cost of the player's ability to interact with
 * the game world.
 * @author Ryan Sandor Richards
 */
public class Sleep extends PlayCommand {
  public Sleep(solace.game.Character ch) {
    super("sleep", ch);
  }

  public boolean run(Connection c, String []params) {
    PlayState state = character.getPlayState();
    Room room = character.getRoom();

    if (state == PlayState.SLEEPING) {
      character.sendln("You are already asleep.");
      return false;
    }

    if (state == PlayState.FIGHTING) {
      character.sendln("Are you daft? You cannot sleep while fighting!");
      return false;
    }

    String characterMessage = "";
    String roomFormat = "";

    if (state == PlayState.SITTING) {
      characterMessage = "You fall asleep.";
      roomFormat = "%s falls asleep.";
    } else if (state == PlayState.RESTING) {
      characterMessage = "While comfortable resting, you doze off.";
      roomFormat = "%s gets a bit too comfortable resting and dozes off.";
    } else if (state == PlayState.STANDING) {
      characterMessage = "You sit, lie back, and fall immediately asleep.";
      roomFormat = "Exhausted, %s sits, lies back, and falls fast asleep.";
    }

    room.sendMessage(
      String.format(roomFormat, character.getName()),
      character
    );
    character.sendln(characterMessage);
    character.setPlayState(PlayState.SLEEPING);

    return true;
  }
}
