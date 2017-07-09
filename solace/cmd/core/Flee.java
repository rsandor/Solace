package solace.cmd.core;

import solace.cmd.AbstractCommand;
import solace.game.Area;
import solace.game.Battle;
import solace.game.BattleManager;
import solace.game.Exit;
import solace.game.Player;
import solace.game.Room;
import solace.util.Log;
import solace.util.Roll;

import java.util.List;

/**
 * Allows for characters to flee combat.
 * - TODO Flesh out flee chances...
 * - TODO Flesh out flee flavor text...
 * @author Ryan Sandor Richards
 */
public class Flee extends AbstractCommand {
  public Flee() {
    super("flee");
  }

  public void run(Player player, String []params) {
    if (!player.isFighting()) {
      player.sendln("You are not currently fighting.");
      return;
    }

    Battle battle = BattleManager.getBattleFor(player);
    if (battle == null) {
      Log.error(
        "Player '" + player.getName() + "' is set as fighting" +
        " but is not in a battle");
      player.sendln("You are not currently fighting.");
      return;
    }

    Room origin = player.getRoom();
    Area area = origin.getArea();
    List<Exit> exits = origin.getExits();

    if (exits.size() == 0) {
      player.sendln("There is no way to escape!");
      return;
    }

    // Find a random exit and destination room
    Exit exit = exits.get(Roll.index(exits.size()));
    Room destination = area.getRoom(exit.getToId());

    // Remove from battle
    battle.remove(player);
    player.sendln("You flee from battle!");

    // Construct the format messages to broadcast to the rooms
    String exitFormat = "%s flees from battle!";
    String enterFormat = "%s arrives, panting and scared.";

    // Move the character from the current room to the random destination
    origin.removePlayer(player);
    origin.sendMessage(String.format(exitFormat, player.getName()));

    player.setRoom(destination);
    destination.sendMessage(String.format(enterFormat, player.getName()));
    destination.addPlayer(player);
    player.sendln(destination.describeTo(player));
  }
}
