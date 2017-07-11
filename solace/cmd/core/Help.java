package solace.cmd.core;

import com.google.common.base.Joiner;
import solace.cmd.AbstractCommand;
import solace.game.Player;
import solace.util.*;

import java.util.*;

/**
 * Help command, allows players to search for and browse
 * articles in the solace help system.
 * @author Ryan Sandor Richards
 */
public class Help extends AbstractCommand {
  public Help() {
    super("help");
    setPriority(AbstractCommand.ORDER_CORE);
  }

  public void run(Player player, String []params) {
    HelpSystem help = HelpSystem.getInstance();
    String result;

    if (params.length < 2) {
      result = help.direct("help", "");
    } else {
      String directName = params[1];
      StringBuilder queryBuilder = new StringBuilder();
      for (int k = 1; k < params.length; k++) {
        queryBuilder.append(params[k]).append(" ");
      }
      String queryText = queryBuilder.toString().trim();
      result = help.direct(directName, queryText);
    }

    player.sendln(result);
  }
}
