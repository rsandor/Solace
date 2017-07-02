package solace.cmd.admin;

import solace.cmd.play.PlayCommand;
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
public class Set extends PlayCommand {
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
   * @return True if the parameter was set, false otherwise.
   */
  protected boolean setPlayerParam(Player p, String param, String value) {
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
      } else {
        throw new Exception("Invalid parameter: " + param);
      }
      return true;
    } catch (NumberFormatException nfe) {
      character.sendln("Invalid number format: " + value);
    } catch (Exception e) {
      character.sendln(e.getMessage());
    }
    return false;
  }

  /**
   * @see solace.cmd.PlayCommand
   */
  public boolean run(Connection c, String []params) {
    solace.game.Character character = getCharacter();
    if (params.length != 4) {
      character.sendln("Usage: set [target] [param] [value]");
      return false;
    }

    String target = params[1];
    String param = params[2];
    String value = params[3];

    Player playerTarget = character.getRoom().findPlayer(target);
    if (playerTarget == null) {
      character.sendln("Could not find player with name: " + target);
      return false;
    }

    return setPlayerParam(playerTarget, param, value);
  }
}
