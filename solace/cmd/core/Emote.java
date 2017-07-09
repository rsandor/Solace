package solace.cmd.core;

import solace.cmd.AbstractCommand;
import solace.util.*;
import solace.game.*;
import java.util.*;

/**
 * Allows players to describe actions they are taking. This is a social command
 * the has no impact on gameplay and is added for flavor.
 * @author Ryan Sandor Richards
 */
public class Emote extends AbstractCommand {
  public Emote() {
    super("emote", Emotes.getInstance().getEmoteAliases());
    setPriority(AbstractCommand.ORDER_LOW);
  }

  public void run(Player player, String []params) {
    Room room = player.getRoom();

    if (player.isSleeping()) {
      player.sendln("You cannot display emotes while asleep.");
      return;
    }

    if (params[0].equals("emote")) {
      if (params.length < 2) {
        player.sendln("What would you like to emote?");
        return;
      }

      StringBuilder buffer = new StringBuilder();
      for (int i = 1; i < params.length; i++) {
        buffer.append(" ");
        buffer.append(params[i]);
      }

      String msg = buffer.toString().trim();

      player.resetVisibilityOnAction("emote");
      room.sendMessage(
        player.getName() + " " + msg,
        player
      );
      player.sendln("You " + msg);
      return;
    }

    try {
      Emotes emotes = Emotes.getInstance();
      if (params.length == 1) {
        player.wrapln(emotes.toSource(params[0]));
        room.sendMessage(emotes.toRoom(params[0], player.getName()), player);
        return;
      }

      String emote = params[0];
      String targetName = params[1];
      Player target = room.findPlayerIfVisible(targetName, player);

      if (target == null) {
        player.wrapln("You do not see " + targetName + " here.");
        return;
      }

      player.resetVisibilityOnAction("emote");

      // TODO This should be synchronized by the room itself :(
      Collection<Player> roomChars = room.getPlayers();
      for (Player ch : roomChars) {
        if (ch == player || ch == target) {
          continue;
        }
        ch.sendMessage(emotes.toRoom(
          emote,
          player.getName(),
          target.getName()
        ));
      }

      player.sendln(emotes.toSource(emote, target.getName()));
      target.sendMessage(emotes.toTarget(emote, player.getName()));
    }
    catch (EmoteNotFoundException e) {
      Log.error(e.getMessage());
      player.sendln("Could not emote. Try again later.");
    }
  }
}
