package solace.cmd;

import java.util.*;
import solace.game.*;
import solace.net.*;
import java.io.*;
import solace.util.*;
import solace.cmd.play.*;

/**
 * Main game play controller (the actual game).
 * @author Ryan Sandor Richards
 */
public class PlayController
  extends AbstractStateController
{
  solace.game.Character character;

  static final String[] moveAliases = {
    "move", "go", "north", "south",
    "east", "west", "up", "down",
    "exit", "enter"
  };

  static final String[] attackAliases = {
    "attack", "kill", "fight"
  };

  /**
   * Creates a new game play controller.
   * @param c The connection.
   * @param ch The character.
   * @throws GameException if anything goes wrong when logging the user in.
   */
  public PlayController(Connection c, solace.game.Character ch)
    throws GameException
  {
    // Initialize the menu
    super(c, "Sorry, that is not an option. Type '{yhelp{x' to see a list.");
    character = ch;

    // Character location initialization
    if (ch.getRoom() == null) {
      Room room = World.getDefaultRoom();
      room.getCharacters().add(ch);
      ch.setRoom(room);
    }

    // Inform other players in the room that they player has entered the game
    ch.getRoom()
      .sendMessage(ch.getName() + " has entered the game.", character);

    // Add commands
    addCommands();

    // Place the player in the world
    World.getActiveCharacters().add(ch);
    c.sendln("\n\rNow playing as {y" + ch.getName() + "{x, welcome!\n\r");
    c.setPrompt("{c>{x ");

    // Describe the room to the player
    if (ch.isSleeping()) {
      ch.sendln("You are fast asleep.");
    } else {
      c.sendln(ch.getRoom().describeTo(ch));
    }
  }

  /**
   * Adds basic gameplay commands to the controller.
   */
  protected void addCommands() {
    addCommand(new Quit(character));
    addCommand(new Help());
    addCommand(moveAliases, new Move(character));
    addCommand(new Look(character));
    addCommand(new Say(character));
    addCommand(new Scan(character));
    addCommand(new Get(character));
    addCommand(new Drop(character));
    addCommand(new Tick());

    addCommand(new Score(character));
    addCommand(new Worth(character));
    addCommand(new Inventory(character));
    addCommand(new ListSkills(character));

    addCommand(new Wear(character));
    addCommand(new Equipment(character));
    addCommand(new Remove(character));

    addCommand(new ShopList(character));
    addCommand(new ShopBuy(character));
    addCommand(new ShopAppraise(character));
    addCommand(new ShopSell(character));

    addCommand(attackAliases, new Attack(character));

    addCommand(new Sit(character));
    addCommand(new Stand(character));
    addCommand(new Rest(character));
    addCommand(new Sleep(character));
    addCommand(new Wake(character));

    Emote emote = new Emote(character);
    addCommand(emote);
    addCommand(Emotes.getInstance().getEmoteAliases(), new Emote(character));
  }
}
