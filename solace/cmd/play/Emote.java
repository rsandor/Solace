package solace.cmd.play;

import solace.util.*;
import solace.net.*;
import solace.cmd.*;
import solace.game.*;
import java.util.*;

/**
 * Allows players to describe actions they are taking. This is a social command
 * the has no impact on gameplay and is added for flavor.
 * @author Ryan Sandor Richards
 */
public class Emote extends PlayCommand {
  HelpSystem help;
  Emotes emotes;

  public Emote(solace.game.Character ch) {
    super("emote", ch);
    emotes = Emotes.getInstance();
  }

  public boolean run(Connection c, String []params) {
    Room room = character.getRoom();

    if (character.isSleeping()) {
      character.sendln("You cannot display emotes while asleep.");
      return false;
    }

    if (params[0].equals("emote")) {
      if (params.length < 2) {
        character.sendln("What would you like to emote?");
        return false;
      }

      StringBuffer buffer = new StringBuffer();
      for (int i = 1; i < params.length; i++) {
        buffer.append(" " + params[i]);
      }

      String msg = buffer.toString().trim();

      room.sendMessage(
        character.getName() + " " + msg,
        character
      );
      character.sendln("You " + msg);

      return true;
    }

    try {
      if (params.length == 1) {
        character.wrapln(emotes.toSource(params[0]));
        room.sendMessage(emotes.toRoom(params[0]), character);
        return true;
      }

      String emote = params[0];
      String targetName = params[1];
      Player target = room.findCharacter(targetName);

      if (target == null) {
        character.wrapln("You do not see " + targetName + " here.");
        return false;
      }

      Collection<Player> roomChars = room.getCharacters();
      synchronized(roomChars) {
        for (Player ch : roomChars) {
          if (ch == character || ch == target) {
            continue;
          }
          ch.sendMessage(emotes.toRoom(
            emote,
            character.getName(),
            target.getName()
          ));
        }
      }

      character.sendln(emotes.toSource(emote, target.getName()));
      target.sendMessage(emotes.toTarget(emote, character.getName()));

      return true;
    }
    catch (EmoteNotFoundException enfe) {
      Log.error(enfe.getMessage());
      character.sendln("Could not emote. Try again later.");
    }
    catch (InvalidEmoteException iee) {
      Log.error(iee.getMessage());
      character.sendln("Could not emote. Try again later.");
    }

    return false;
  }
}
