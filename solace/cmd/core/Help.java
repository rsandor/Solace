package solace.cmd.deprecated;

import solace.util.*;
import solace.net.*;

import java.util.*;

/**
 * Help command, allows players to search for and browse
 * articles in the solace help system.
 * @author Ryan Sandor Richards
 */
public class Help extends AbstractStateCommand {
  HelpSystem help;

  public Help() {
    super("help");
    help = HelpSystem.getInstance();
  }

  public void run(Connection c, String []params) {
    if (params.length < 2) {
      c.wrapln( help.getArticle("index.md") );
      return;
    }

    List<String> keywords = new LinkedList<String>();
    for (int i = 1; i < params.length; i++) {
      keywords.add(params[i].toLowerCase());
    }
    c.send("\n\r" + help.query(keywords) + "\n\r");
  }
}
