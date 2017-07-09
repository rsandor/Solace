package solace.cmd.core;

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
    Set<String> keywords = new HashSet<>(
      Arrays.asList(params).subList(1, params.length)
    );
    if (keywords.size() == 0) {
      player.wrapln( help.getArticle("index.md") );
      return;
    }
    player.send("\n\r" + help.query(keywords) + "\n\r");
  }
}
