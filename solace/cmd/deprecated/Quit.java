package solace.cmd.deprecated;

import solace.cmd.AbstractCommand;
import solace.game.*;
import solace.cmd.*;
import solace.net.Connection;

/**
 * Quit command. Exits the game and returns to the main menu.
 * @author Ryan Sandor Richards.
 */
public class Quit extends AbstractCommand {
  public Quit() {
    super("quit");
  }

  public void run(Player player, String []params) {
    if (player.isMobile()) {
      player.sendln("Sorry, mobiles cannot quit the game.");
      return;
    }

    if (player.isFighting()) {
      player.sendln("You cannot quit, you are in {R}BATTLE{x}!");
      return;
    }

    Room room = player.getRoom();
    room.removePlayer(player);

    String message = String.format("%s has left the game.", player.getName());
    room.sendMessage(message);

    solace.game.Character character = player.getCharacter();
    if (character != null) {
      World.getActiveCharacters().remove(character);
      Game.writer.save(character);
      Connection c = character.getConnection();
      c.setStateController( new MainMenuController(c) );
    }
  }
}
