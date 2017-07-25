'use strict';

var sleepTalkNouns = [
  "a baboon", "a silver fork", "Mr. Bojangles", "a thick slice of foie gras",
  "a pesant lady", "a busty bar maid", "a strapping horse theif", "the gods",
  "the king of a faroff land", "some mustard", "a puppet show", "a broom",
  "an old crippled lady", "fifteen thousand bloodthirsty soldiers", "a cake",
  "a pudding pop", "the grand opera", "the sea", "a wise owl", "the plague"
];

var sleepTalkVerbs = [
  "loving", "flogging", "spooning", "dating", "highfiveing", "cradeling",
  "consoling", "killing", "capturing", "flirting with", "dating", "smacking",
  "punching", "stabbing", "grabbing", "fondling", "groping", "doping",
  "being in awe and terror of", "crying with", "drinking with", "dancing with",
  "prancing about", "courting", "copulating with", "smothering", "screaming at"
];

/**
 * Generates and broadcasts a random message coming from the player when they
 * attempt to use the say command while sleeping.
 */
function sleepTalk(player) {
  var verb = sleepTalkVerbs[Roll.index(sleepTalkVerbs.length)];
  var noun = sleepTalkNouns[Roll.index(sleepTalkNouns.length)];

  player.sendln(format(
    "You mumble something about %s %s while sleeping.",
    verb,
    noun
  ));

  player.getRoom().sendMessage(
    format(
      "%s mumbles something about %s %s while sleeping",
      player.getName(), verb, noun),
    player
  );
}

/**
 * The say command, allows players to speak to eachother in a given room.
 * @author Ryan Sandor Richards
 */
Commands.add('say', function (player, params) {
  if (params.length < 2) {
    return player.sendln("What would you like to say?");
  }

  player.resetVisibilityOnAction("say");

  // Oh-ho-ho, sleep talking...
  if (player.isSleeping()) {
    return sleepTalk(player);
  }

  // Format the message
  // TODO Make me cleaner...
  var builder = new StringBuilder();
  builder.append("'");
  for (var i = 1; i < params.length; i++) {
    var word = params[i];
    if (word.length == 0) continue;
    builder.append(word)
    builder.append(i < params.length - 1 ? ' ' : '');
  }
  builder.append("'\n\r");

  // TODO Make me cleaner...
  var message = builder.toString();
  if (message === "''\n\r") {
    return player.sendln("What would you like to say?");
  }

  // Broadcast to the room
  var room = player.getRoom();
  room.getPlayers().forEach(function (other) {
    if (other == player)
      player.sendln("You say " + message);
    else
      other.sendMessage(player.getName() + " says " + message);
  });
});
