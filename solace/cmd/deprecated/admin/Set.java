package solace.cmd.deprecated.admin;

import solace.cmd.Command;
import solace.cmd.deprecated.PlayStateCommand;
import solace.game.*;
import solace.net.*;
import solace.util.*;

/**
 * Sets variables and parameters for game objects. Currently this command only
 * supports the setting of state and level for players.
 *
 * TODO Flesh this out over time to add new things that can be set.
 * TODO Add admin only help and an entry for this command.
 *
 * @author Ryan Sandor Richards
 */
public class Set extends PlayStateCommand {
  /**
   * Default constructor.
   * @param ch Character associated with the command.
   */
  public Set(solace.game.Character ch) {
    super("set", ch);
  }

  /**
   * Sets parameters for players.
   * @param p The player for which to set the parameter.
   * @param param Name of the parameter to set.
   * @param value Value to set for the parameter.
   */
  protected void setPlayerParam(Player p, String param, String value) {
    solace.game.Character character = getCharacter();
    try {
      if (param.equals("level")) {
        int level = Integer.parseInt(value);
        if (level < 1 || level > 100) {
          throw new Exception("Level out of bounds: " + level);
        }
        p.setLevel(level);
      } else if (param.equals("state")) {
        PlayState state = PlayState.fromString(value);
        if (state == null) {
          throw new Exception("Invalid play state: " + value);
        }
        p.setPlayState(state);
      } else if (param.equals("hp")) {
        int hp = Integer.parseInt(value);
        p.setHp(hp);
      } else if (param.equals("mp")) {
        int mp = Integer.parseInt(value);
        p.setMp(mp);
      } else if (param.equals("race")) {
        if (!Races.has(value)) {
          throw new Exception("Invalid player race: " + value);
        }
        if (p.isMobile()) {
          throw new Exception("Mobiles cannot have races.");
        }
        ((solace.game.Character)p).setRace(Races.get(value));
      } else if (param.equals("immortal")) {
        // Player immortality can only be set by immortals, it prevents all
        // damage to the player who is currently flagged as such. This shouldn't
        // be used outside of testing.
        // TODO Support `true` and `false` value commands.
        p.toggleImmortal(character);
      } else {
        throw new Exception("Invalid parameter: " + param);
      }
    } catch (NumberFormatException nfe) {
      character.sendln("Invalid number format: " + value);
    } catch (Exception e) {
      character.sendln(e.getMessage());
    }
  }

  /**
   * @see Command
   */
  public void run(Connection c, String []params) {
    solace.game.Character character = getCharacter();
    if (params.length != 4) {
      character.sendln("Usage: set [target] [param] [value]");
      return;
    }

    String target = params[1];
    String param = params[2];
    String value = params[3];

    Player playerTarget = character.getRoom().findPlayer(target);
    if (playerTarget == null) {
      character.sendln("Could not find player with name: " + target);
      return;
    }

    setPlayerParam(playerTarget, param, value);
  }
}