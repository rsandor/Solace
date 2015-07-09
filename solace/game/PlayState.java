package solace.game;

/**
 * Enumeration of all possible states for a player in the game.
 * @author Ryan Sandor Richards
 * @see solace.game.Player
 */
public enum PlayState {
  SLEEPING("sleeping"),
  RESTING("resting"),
  SITTING("sitting"),
  STANDING("standing"),
  FIGHTING("fighting"),
  DEAD("dead");

  // Instance variables
  String name;

  /**
   * Creates a new play state.
   * @param n Name of the state.
   */
  PlayState(String n) {
    name = n;
  }

  /**
   * @return The
   */
  public String toString() { return name; }

  /**
   * Determines a play state from a given string.
   * @param s String to parse.
   * @return The play state from the given string, or null if no state matches.
   */
  public static PlayState fromString(String s) {
    PlayState[] states = new PlayState[] {
      SLEEPING, RESTING, SITTING, STANDING, FIGHTING, DEAD
    };
    for (PlayState state : states) {
      if (state.toString().equals(s)) {
        return state;
      }
    }
    return null;
  }
}
