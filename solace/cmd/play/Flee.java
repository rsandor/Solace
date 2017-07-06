package solace.cmd.play;

import solace.game.*;
import solace.net.*;
import solace.util.*;
import java.util.*;

/**
 * Allows for characters to flee combat.
 * - TODO Flesh out flee chances...
 * - TODO Flesh out flee flavor text...
 * @author Ryan Sandor Richards
 */
public class Flee extends PlayStateCommand {
  public Flee(solace.game.Character ch) {
    super("flee", ch);
  }

  public void run(Connection c, String []params) {
    if (!character.isFighting()) {
      character.sendln("You are not currently fighting.");
      return;
    }

    Battle battle = BattleManager.getBattleFor(character);

    if (battle == null) {
      Log.error(
        "Player '" + character.getName() + "' is set as fighting" +
        " but is not in a battle");
      character.sendln("You are not currently fighting.");
      return;
    }

    Room origin = character.getRoom();
    Area area = origin.getArea();
    List<Exit> exits = origin.getExits();

    if (exits.size() == 0) {
      character.sendln("There is no way to escape!");
      return;
    }

    // Find a random exit and destination room
    Exit exit = exits.get(Roll.index(exits.size()));
    Room destination = area.getRoom(exit.getToId());

    // Remove from battle
    battle.remove(character);
    character.sendln("You flee from battle!");

    // Construct the format messages to broadcast to the rooms
    String exitFormat = "%s flees from battle!";
    String enterFormat = "%s arrives, panting and scared.";

    // Move the character from the current room to the random destination
    origin.getCharacters().remove(character);
    origin.sendMessage(String.format(exitFormat, character.getName()));

    character.setRoom(destination);
    destination.sendMessage(String.format(enterFormat, character.getName()));
    destination.getCharacters().add(character);
    character.sendln(destination.describeTo(character));
  }
}
