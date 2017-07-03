package solace.cmd.play;

import solace.util.*;
import solace.net.*;
import solace.cmd.*;
import java.util.*;

/**
 * Help command, allows players to search for and browse
 * articles in the solace help system.
 * @author Ryan Sandor Richards
 */
public class Help extends AbstractCommand {
  HelpSystem help;

  public Help() {
    super("help");
    help = HelpSystem.getInstance();
  }

  public boolean run(Connection c, String []params) {
    if (params.length < 2) {
      c.wrapln( help.getArticle("index.md") );
      return true;
    }

    List<String> keywords = new LinkedList<String>();
    for (int i = 1; i < params.length; i++) {
      keywords.add(params[i].toLowerCase());
    }
    c.send("\n\r" + help.query(keywords) + "\n\r");

    return true;
  }
}
