package solace.comm;
import solace.net.Connection;

/**
 * Describes basic behaviors needed for all communication channels.
 * @author Ryan Sandor Richards (Gaius)
 */
public interface Channel
{
  /**
   * Returns the format for messages in the channel.
   *
   * Formats have the following special "hooks" for various pieces of data:
   *
   *    %n  Name of the account
   *    %N  Name of the player for the account
   *    %m  Message
   *    %t  Time the message was sent
   *
   * @return The format for the channel.
   */
  public String getFormat();

  /**
   * Determines if a connected user can see a message.
   * @param c Connection for the user to send against.
   * @return True if the player can see the message, false otherwise.
   */
  public boolean canSeeMessage(Connection c);

  /**
   * Determines if a connected user can see the sender of the message.
   * @param s Connection of the sender.
   * @param c Connection of the user to test against.
   * @return True if the player can see the sender, false otherwise.
   */
  public boolean canSeeSender(Connection s, Connection c);
}
