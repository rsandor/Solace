package solace.cmd.play;

import solace.net.*;
import solace.util.*;
import solace.game.*;
import solace.cmd.*;

/**
 * Quit command. Exits the game and returns to the main menu.
 * @author Ryan Sandor Richards.
 */
public class Quit extends PlayCommand {
  public Quit(solace.game.Character ch) {
    super("quit", ch);
  }

  public boolean run(Connection c, String []params) {
    if (character.isFighting()) {
      character.sendln("You cannot quit, you are in BATTLE!");
      return false;
    }

    Room room = character.getRoom();
    room.getCharacters().remove(character);

    String message = String.format(
      "%s has left the game.",
      character.getName()
    );
    room.sendMessage(message);

    World.getActiveCharacters().remove(character);
    Game.writer.save(character);

    c.setStateController( new MainMenu(c) );

    return true;
  }
}
