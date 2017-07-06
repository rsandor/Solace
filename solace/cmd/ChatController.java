package solace.cmd;

import solace.game.*;
import solace.net.Connection;
import solace.util.*;

/**
 * Out of game chat controller. The out of game chat allows people to chat on
 * the server without having to actually be logged into the game and playing
 * (useful for discussing strategy, talking with friends, asking questions, and
 * general banter when not actually playing).
 * @author Ryan Sandor Richards (Gaius)
 */
public class ChatController implements Controller {
  private Connection connection;

  ChatController(Connection c) {
    connection = c;
    World.addChatConnection(connection);
    connection.sendln(Message.get("ChatIntro"));
  }

  /**
   * @see solace.cmd.Controller
   */
  public String getPrompt() {
    return "{c}chat>{x} ";
  }

  /**
   * Parses chat commands and broadcasts messages.
   * @param message Message or command to parse.
   */
  public void parse(String message) {
    if (message == null || message.length() == 0) {
      return;
    }
    if (message.toLowerCase().startsWith("/quit")) {
      connection.sendln("Later!");
      World.removeChatconnection(connection);
      connection.setStateController(new MainMenu(connection));
    } else if (message.toLowerCase().startsWith("/help")) {
      String help = Message.get("ChatHelp");
      connection.sendln(help);
    } else {
      String name = connection.getAccount().getName().toLowerCase();
      String format = "{y}" + name + ": {x}" + message;
      for (Object chatter : World.getChatConnections()) {
        Connection c = (Connection) chatter;
        c.sendln(format);
      }
    }
  }
}
