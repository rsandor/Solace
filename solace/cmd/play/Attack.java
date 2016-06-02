package solace.cmd.play;

import solace.game.*;
import solace.net.*;
import solace.util.*;
import java.util.*;
import java.io.*;

/**
 * The attack command is used to attack other players in the game and initiate
 * combat.
 * @author Ryan Sandor Richards
 */
public class Attack extends PlayCommand {
  public Attack(solace.game.Character ch) {
    super("attack", ch);
  }

  public boolean run(Connection c, String []params) {
    Room room = character.getRoom();

    if (character.isFighting()) {
      character.sendln("You are already in battle!");
      return false;
    }

    if (character.isRestingOrSitting()) {
      character.sendln("You cannot initiate battle unless standing and alert.");
      return false;
    }

    if (character.isSleeping()) {
      character.sendln("You dream of attacking, as you are asleep.");
      return false;
    }

    if (params.length < 2) {
      character.sendln("Who would you like to attack?");
      return false;
    }

    String name = params[1];
    Player target = room.findPlayer(name);

    if (target == null) {
      character.sendln(String.format("You do not see %s here.", name));
      return false;
    }

    if (!target.isMobile()) {
      character.sendln("You cannot attack other players.");
      return false;
    }

    if (((Mobile)target).isProtected()) {
      character.sendln("You cannot attack " + target.getName() + ".");
      return false;
    }

    // TODO going to have to modify this when player groups come along
    if (target.isFighting()) {
      character.sendln(
        String.format("%s is already engaged in battle.",
        target.getName()
      ));
      return false;
    }

    // Start the battle
    BattleManager.initiate(character, target);
    return true;
  }
}
