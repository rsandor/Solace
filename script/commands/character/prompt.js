'use strict';

var DEFAULT_PROMPT = '(( {G}%h{x}/{g}%H{x}hp {M}%m{x}/{m}%M{x}mp {Y}%s{x}/{y}%S{x}sp )) {Y}%gg{x} %T>';

/**
 * Command that allows players to set custom prompt formats.
 * @author Ryan Sandor Richards
 */
Commands.add('prompt', function (player, params) {
  if (player.isMobile()) {
    return player.sendln('Mobiles do not have prompts.');
  }

  var prompt;
  if (params.length < 2) {
    player.sendln('Setting default prompt.');
    prompt = DEFAULT_PROMPT;
  } else {
    prompt = params[1];
  }

  if (params.length > 2) {
    player.sendln("Setting prompt as: " + prompt + ' (try using quotes)');
  } else {
    player.sendln("Setting prompt as: " + prompt);
  }
  player.getCharacter().setPrompt(prompt);
});
