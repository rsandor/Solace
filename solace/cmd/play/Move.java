package solace.cmd.play;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;

/**
 * The movement command is used to move about the game world.
 *
 * Syntax:
 *   move [direction]
 *   go [direction]
 *   north
 *   south
 *   east
 *   west
 *   up
 *   down
 *   enter [place]
 *   exit [place]
 */
public class Move extends PlayCommand {
  public Move(solace.game.Character ch) {
    super("move", ch);
  }

  public boolean run(Connection c, String []params) {
    PlayState state = character.getPlayState();

    if (state == PlayState.FIGHTING) {
      character.sendln("You cannot leave while engaged in battle!");
      return false;
    }

    if (state == PlayState.SITTING || state == PlayState.RESTING) {
      character.sendln("You must stand up before you can leave.");
      return false;
    }

    if (state == PlayState.SLEEPING) {
      character.sendln("You cannot move while you are asleep.");
      return false;
    }

    String cmd = params[0];
    String direction;
    boolean notEast = !(new String("east").startsWith(cmd));

    if (cmd.equals("move") || cmd.equals("go")) {
      if (params.length < 2) {
        character.sendln("What direction would you like to move?");
        return false;
      }
      direction = params[1];
    }
    else if (new String("enter").startsWith(cmd) && notEast) {
      if (params.length < 2) {
        character.sendln("Where would you like to enter?");
        return false;
      }
      direction = params[1];
    }
    else if (new String("exit").startsWith(cmd) && notEast) {
      if (params.length < 2) {
        character.sendln("Where would you like to exit?");
        return false;
      }
      direction = params[1];
    }
    else {
      direction = cmd;
    }

    if (direction == null) {
      character.sendln("That is not a direction.");
      Log.error("Null direction encountered during move.");
      return false;
    }

    Exit exit = character.getRoom().findExit(direction);
    if (exit == null) {
      character.sendln("There is no exit '" + direction + "'.");
      return false;
    }

    Area area = character.getRoom().getArea();
    Room origin = character.getRoom();
    Room destination = area.getRoom(exit.getToId());

    if (destination == null) {
      character.sendln("There is no exit '" + direction + "'");
      Log.error("Null destination encountered on move from '" +
        character.getRoom().getId() + "' along exit with names '" +
        exit.getCompiledNames() + "'");
      return false;
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

    String charName = character.getName();

    // Remove the character from its current room
    origin.getCharacters().remove(character);
    origin.sendMessage(String.format(exitFormat, charName));

    // Send it to the destination room
    character.setRoom(destination);
    destination.sendMessage(String.format(enterFormat, charName));
    destination.getCharacters().add(character);

    // Show them the room they just entered
    character.sendln(destination.describeTo(character));

    return true;
  }
}
