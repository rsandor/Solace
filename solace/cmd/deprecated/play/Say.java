package solace.cmd.deprecated.play;

import solace.game.*;
import solace.net.*;
import solace.util.*;

/**
 * The say command, allows players to speak to eachother in a given room.
 * @author Ryan Sandor Richards
 */
public class Say extends PlayStateCommand {
  static final String[] sleepTalkNouns = {
    "a baboon", "a silver fork", "Mr. Bojangles", "a thick slice of foie gras",
    "a pesant lady", "a busty bar maid", "a strapping horse theif", "the gods",
    "the king of a faroff land", "some mustard", "a puppet show", "a broom",
    "an old crippled lady", "fifteen thousand bloodthirsty soldiers", "a cake",
    "a pudding pop", "the grand opera", "the sea", "a wise owl", "the plague"
  };

  static final String[] sleepTalkVerbs = {
    "loving", "flogging", "spooning", "dating", "highfiveing", "cradeling",
    "consoling", "killing", "capturing", "flirting with", "dating", "smacking",
    "punching", "stabbing", "grabbing", "fondling", "groping", "doping",
    "being in awe and terror of", "crying with", "drinking with", "dancing with",
    "prancing about", "courting", "copulating with", "smothering", "screaming at"
  };

  public Say(solace.game.Character ch) {
    super("say", ch);
  }

  /**
   * Generates and broadcasts a random message coming from the player when they
   * attempt to use the say command while sleeping.
   */
  private void sleepTalk() {
    String verb = sleepTalkVerbs[Roll.index(sleepTalkVerbs.length)];
    String noun = sleepTalkNouns[Roll.index(sleepTalkNouns.length)];

    character.sendln(String.format(
      "You mumble something about %s %s while sleeping.",
      verb,
      noun
    ));

    character.getRoom().sendMessage(
      String.format(
        "%s mumbles something about %s %s while sleeping",
        character.getName(),
        verb,
        noun
      ),
      character
    );
  }

  public void run(Connection c, String []params) {
    if (params.length < 2) {
      c.sendln("What would you like to say?");
      return;
    }

    character.resetVisibilityOnAction("say");

    // Oh-ho-ho, sleep talking...
    if (character.isSleeping()) {
      sleepTalk();
      return;
    }

    // Format the message
    String message = "'";
    for (int i = 1; i < params.length; i++) {
      message += params[i];
      if (i != params.length - 1)
        message += " ";
    }
    message += "'\n";

    // Broadcast to the room
    Room room = character.getRoom();
    synchronized(room.getCharacters()) {
      for (Player ch : room.getCharacters()) {
        if (ch == character)
          c.sendln("You say " + message);
        else
          ch.sendMessage(character.getName() + " says " + message);
      }
    }
  }
}
