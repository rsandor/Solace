package solace.cmd;

import solace.cmd.play.PlayCommandRegistry;
import solace.cmd.play.PlayCommand;
import solace.game.*;
import solace.net.*;
import solace.util.*;
import com.google.common.base.Joiner;

/**
 * Main game play controller (the actual game).
 * @author Ryan Sandor Richards
 */
public class PlayController implements Controller {
  solace.game.Character character;

  /**
   * Creates a new game play controller.
   * @param ch The character.
   * @throws GameException if anything goes wrong when logging the user in.
   */
  public PlayController(solace.game.Character ch)
    throws GameException
  {
    character = ch;

    // Character location initialization
    if (ch.getRoom() == null) {
      Room room = World.getDefaultRoom();
      room.addPlayer(ch);
      ch.setRoom(room);
    }

    // Inform other players in the room that they player has entered the game
    ch.getRoom()
      .sendMessage(ch.getName() + " has entered the game.", character);

    // Place the player in the world
    World.getActiveCharacters().add(ch);
    ch.sendln("\n\rNow playing as {y}" + ch.getName() + "{x}, welcome!\n\r");

    // Describe the room to the player
    if (ch.isSleeping()) {
      ch.sendln("You are fast asleep.");
    } else {
      ch.sendln(ch.getRoom().describeTo(ch));
    }
  }

  /**
   * Generates the dynamic custom prompt for the player.
   * NOTE This may not belong here, factor out?
   * TODO %t    Target health percentage
   * TODO %T    Target health percentage, with colors
   * @return The generated prompt for the player.
   */
  public String getPrompt() {
    String out = new String(character.getPrompt());
    out = out
      .replace("%h", character.getHp() + "")
      .replace("%H", character.getMaxHp() + "")
      .replace("%m", character.getMp() + "")
      .replace("%M", character.getMaxMp() + "")
      .replace("%s", character.getSp() + "")
      .replace("%S", character.getMaxSp() + "")
      .replace("%g", character.getGold() + "")
      .replace("%a", character.getRoom().getArea().getTitle());

    // TODO Refactor the fuck out of me
    int targetHealthRemaning = -1;
    if (character.isFighting()) {
      Battle battle = BattleManager.getBattleFor(character);
      if (battle == null) {
        Log.error(String.format(
          "Player '%s' marked as fighting but not in battle.",
          character.getName()
        ));
      } else {
        Player target = battle.getTargetFor(character);
        if (target != null) {
          targetHealthRemaning = (int)(
            100.0 * (double)target.getHp() / (double)target.getMaxHp()
          );
        }
      }
    }

    if (targetHealthRemaning < 0) {
      out = out.replace("%t", "").replace("%T", "");
    } else {
      String healthRemaining = targetHealthRemaning + "%%";
      out = out.replace("%t", " " + healthRemaining);

      if (targetHealthRemaning > 85) {
        out = out.replace("%T", "{G}" + healthRemaining + "{x}");
      } else if (targetHealthRemaning > 70) {
        out = out.replace("%T", "{g}" + healthRemaining + "{x}");
      } else if (targetHealthRemaning > 55) {
        out = out.replace("%T", "{y}" + healthRemaining + "{x}");
      } else if (targetHealthRemaning > 40) {
        out = out.replace("%T", "{Y}" + healthRemaining + "{x}");
      } else if (targetHealthRemaning > 25) {
        out = out.replace("%T", "{r}" + healthRemaining + "{x}");
      } else {
        out = out.replace("%T", "{R}" + healthRemaining + "{x}");
      }
    }
    // TODO end "refactor the fuck out of me"

    return out.replace("%%", "%");
  }

  /**
   * Determines if a input command from a user represents a hotbar command.
   * @param  input [description]
   * @return       [description]
   */
  public boolean isHotbarCommand(String input) {
    return input.length() == 1 && (
      (input.charAt(0) >= '0' && input.charAt(0) <= '9') ||
      input.charAt(0) == '-' ||
      input.charAt(0) == '='
    );
  }

  /**
   * Parses input commands while accounting for hotbar commands, macros, etc.
   * @param input Input to parse.
   */
  public void parse(String input) {
    if (character.hasBuff("stun")) {
      character.sendln("You are stunned and cannot act!");
      return;
    }

    if (character.isCasting()) {
      character.sendln("You are focusing on casting and cannot act further!");
      return;
    }

    if (input == null || input.length() < 1) {
      return;
    }

    String[] tokens = input.split("\\s");
    if (tokens.length < 1) {
      return;
    }

    if (isHotbarCommand(tokens[0])) {
      String command = character.getHotbarCommand(tokens[0]);
      if (command != null && command.length() > 0) {
        tokens[0] = command;
      }
      input = Joiner.on(" ").join(tokens);
    }

    String[] params = CommandParser.parse(input);
    String namePrefix = params[0];
    PlayCommand command = PlayCommandRegistry.getInstance().find(namePrefix, character);
    try {
      command.run(character, params);
    } catch (Throwable t) {
      Log.error(String.format(
        "Error processing command %s: %s", command.getName(), t.getMessage()));
      t.printStackTrace();
    }
  }
}
