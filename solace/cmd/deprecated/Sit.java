package solace.cmd.deprecated;

import solace.game.*;
import solace.net.*;

/**
 * Allows the player to sit down either on the ground or in objects such as
 * chairs, benches, etc.
 * @author Ryan Sandor Richards
 */
public class Sit extends PlayStateCommand {
  public Sit(solace.game.Character ch) {
    super("sit", ch);
  }

  public void run(Connection c, String []params) {
    Room room = character.getRoom();

    if (character.isSitting()) {
      character.sendln("You are already sitting.");
      return;
    }

    if (character.isFighting()) {
      character.sendln("You cannot sit down in the middle of battle!");
      return;
    }

    String characterMessage = "";
    String roomFormat = "";

    if (character.isStanding()) {
      characterMessage = "You sit down.";
      roomFormat = "%s sits down.";
    } else if (character.isSleeping()) {
      characterMessage = "You awake and sit up.";
      roomFormat = "%s wakes and sits up.";
    } else if (character.isResting()) {
      characterMessage = "You stop resting and sit up.";
      roomFormat = "%s stops resting and sits up.";
    }

    room.sendMessage(
      String.format(roomFormat, character.getName()),
      character
    );
    character.sendln(characterMessage);
    character.setSitting();
  }
}
