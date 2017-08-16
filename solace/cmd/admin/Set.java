package solace.cmd.admin;

import solace.cmd.AbstractCommand;
import solace.game.*;
import solace.io.Races;

/**
 * Sets variables and parameters for game objects. Currently this command only
 * supports the setting of state and level for players.
 *
 * TODO Flesh this out over time to add new things that can be set.
 *
 * @author Ryan Sandor Richards
 */
public class Set extends AbstractCommand {
  /**
   * Default constructor.

   */
  public Set() {
    super("set");
  }

  /**
   * Sets parameters for players.
   * @param target The player for which to set the parameter.
   * @param param Name of the parameter to set.
   * @param value Value to set for the parameter.
   */
  private void setPlayerParam(Player player, Player target, String param, String value) {
    try {
      switch (param) {
        case "level":
          int level = Integer.parseInt(value);
          if (level < 1 || level > 100) {
            throw new Exception("Level out of bounds: " + level);
          }
          target.setLevel(level);
          break;
        case "state":
          PlayState state = PlayState.fromString(value);
          if (state == null) {
            throw new Exception("Invalid play state: " + value);
          }
          target.setPlayState(state);
          break;
        case "hp":
          int hp = Integer.parseInt(value);
          target.setHp(hp);
          break;
        case "mp":
          int mp = Integer.parseInt(value);
          target.setMp(mp);
          break;
        case "sp":
          int sp = Integer.parseInt(value);
          target.setSp(sp);
          break;
        case "race":
          if (!Races.getInstance().has(value)) {
            throw new Exception("Invalid player race: " + value);
          }
          if (target.isMobile()) {
            throw new Exception("Mobiles cannot have races.");
          }
          ((solace.game.Character) target).setRace(Races.getInstance().get(value));
          break;
        case "immortal":
          // Player immortality can only be set by immortals, it prevents all
          // damage to the player who is currently flagged as such. This shouldn't
          // be used outside of testing.
          // TODO Support `true` and `false` value commands.
          target.toggleImmortal(player);
          break;
        default:
          throw new Exception("Invalid parameter: " + param);
      }
    } catch (NumberFormatException nfe) {
      player.sendln("Invalid number format: " + value);
    } catch (Exception e) {
      player.sendln(e.getMessage());
    }
  }

  @Override
  public boolean hasCommand(Player player) {
    return player.getAccount().isAdmin();
  }

  @Override
  public void run(Player player, String []params) {
    String target = params[1];
    String param = params[2];
    String value = params[3];

    Player playerTarget = player.getRoom().findPlayer(target);
    if (playerTarget == null) {
      player.sendln("Could not find player with name: " + target);
      return;
    }

    setPlayerParam(player, playerTarget, param, value);
  }
}
