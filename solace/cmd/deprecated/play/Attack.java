package solace.cmd.deprecated.play;

import solace.game.*;
import solace.cmd.play.*;

/**
 * The attack command is used to attack other players in the game and initiate
 * combat.
 * @author Ryan Sandor Richards
 */
public class Attack extends AbstractPlayCommand {
  private static final String[] aliases = {
    "attack", "kill", "fight"
  };

  public Attack() {
    super("attack", Attack.aliases);
  }

  public void run(Player player, String []params) {
    Room room = player.getRoom();

    if (player.isFighting()) {
      player.sendln("You are already in battle!");
      return;
    }

    if (player.isRestingOrSitting()) {
      player.sendln("You cannot initiate battle unless standing and alert.");
      return;
    }

    if (player.isSleeping()) {
      player.sendln("You dream of attacking, as you are asleep.");
      return;
    }

    if (params.length < 2) {
      player.sendln("Who would you like to attack?");
      return;
    }

    String name = params[1];
    Player target = room.findPlayerIfVisible(name, player);

    if (target == null) {
      player.sendln(String.format("You do not see %s here.", name));
      return;
    }

    if (!target.isMobile()) {
      player.sendln("You cannot attack other players.");
      return;
    }

    if (target.isDead()) {
      player.sendln("You cannot attack a target that is already dead!");
      return;
    }

    if (((Mobile)target).isProtected()) {
      player.sendln("You cannot attack " + target.getName() + ".");
      return;
    }

    // TODO going to have to modify this when player groups come along
    if (target.isFighting()) {
      player.sendln(
        String.format("%s is already engaged in battle.",
        target.getName()
      ));
      return;
    }

    // Start the battle
    BattleManager.initiate(player, target);

    // TODO Need a way to handle this
    // c.skipNextPrompt();
  }
}
