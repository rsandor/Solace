package solace.cmd.core;

import solace.cmd.AbstractCommand;
import solace.game.*;
import solace.util.*;

/**
 * The movement command is used to move about the game world.
 * @author Ryan Sandor Richards
 */
public class Move extends AbstractCommand {
  private static final String[] aliases = {
    "go", "north", "south",
    "east", "west", "up", "down",
    "exit", "enter"
  };

  public Move() {
    super("move", aliases);
  }

  public void run(Player player, String []params) {
    if (player.isFighting()) {
      player.sendln("You cannot leave while engaged in battle!");
      return;
    }

    if (player.isRestingOrSitting()) {
      player.sendln("You must stand up before you can leave.");
      return;
    }

    if (player.isSleeping()) {
      player.sendln("You cannot move while you are asleep.");
      return;
    }

    String cmd = params[0];
    String direction;
    boolean notEast = !(new String("east").startsWith(cmd));

    if (cmd.equals("move") || cmd.equals("go")) {
      if (params.length < 2) {
        player.sendln("What direction would you like to move?");
        return;
      }
      direction = params[1];
    }
    else if (new String("enter").startsWith(cmd) && notEast) {
      if (params.length < 2) {
        player.sendln("Where would you like to enter?");
        return;
      }
      direction = params[1];
    }
    else if (new String("exit").startsWith(cmd) && notEast) {
      if (params.length < 2) {
        player.sendln("Where would you like to exit?");
        return;
      }
      direction = params[1];
    }
    else {
      direction = cmd;
    }

    if (direction == null) {
      player.sendln("That is not a direction.");
      Log.error("Null direction encountered during move.");
      return;
    }

    Exit exit = player.getRoom().findExit(direction);
    if (exit == null) {
      player.sendln("There is no exit '" + direction + "'.");
      return;
    }

    Area area = player.getRoom().getArea();
    Room origin = player.getRoom();
    Room destination = area.getRoom(exit.getToId());

    if (destination == null) {
      player.sendln("There is no exit '" + direction + "'");
      Log.error("Null destination encountered on move from '" +
        player.getRoom().getId() + "' along exit with names '" +
        exit.getCompiledNames() + "'");
      return;
    }

    // Determine the exit and enter messages
    String exitFormat = "%s leaves.";
    String enterFormat = "%s arrives.";

    if (new String("north").startsWith(direction)) {
      exitFormat = "%s leaves to the north.";
      enterFormat = "%s arrives from the south.";
    }
    else if (new String("south").startsWith(direction)) {
      exitFormat = "%s heads to the south.";
      enterFormat = "%s arrives from the north.";
    }
    else if (new String("east").startsWith(direction)) {
      exitFormat = "%s leaves heading east.";
      enterFormat = "%s arrives from the west.";
    }
    else if (new String("west").startsWith(direction)) {
      exitFormat = "%s heads west.";
      enterFormat = "%s arrives from the east.";
    }
    else if (new String("up").startsWith(direction)) {
      exitFormat = "%s leaves heading up.";
      enterFormat = "%s arrives from below.";
    }
    else if (new String("down").startsWith(direction)) {
      exitFormat = "%s leaves going down.";
      enterFormat = "%s arrives from above.";
    }
    else if (
      new String("enter").startsWith(cmd) ||
      new String("exit").startsWith(cmd)
    ) {
      exitFormat = "%s enters " + destination.getTitle() + ".";
      enterFormat = "%s arrives from " + origin.getTitle() + ".";
    }

    String charName = player.getName();

    player.resetVisibilityOnAction("move");

    // Remove the character from its current room
    origin.removePlayer(player);
    origin.sendMessage(String.format(exitFormat, charName));

    // Send it to the destination room
    player.setRoom(destination);
    destination.sendMessage(String.format(enterFormat, charName));
    destination.addPlayer(player);

    // Show them the room they just entered
    player.sendln(destination.describeTo(player));
  }
}
