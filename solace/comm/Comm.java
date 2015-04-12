package solace.comm;

import solace.net.*;

/**
 * Handles all communication channels.
 *
 * @author Ryan Sandor Richards (Gaius)
 */
public class Comm
{
  public static Chat chat_channel = new Chat();

  /**
   * Sends a message over a specific channel.
   * @param ch Channel over which to send the message.
   * @param c Connection the message was sent from.
   * @param msg Message to send.
   */
  public static void message(Channel ch, Connection c, String msg)
  {
  }

  /**
   * Sends a message over the chat channel.
   * @param c Connection sending the message.
   * @param msg Message to send.
   */
  public static void chat(Connection c, String msg)
  {
    message(chat_channel, c, msg);
  }
}
