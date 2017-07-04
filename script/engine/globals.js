'use strict';

/**
 * Defines globals for the solace scripting engine. Place any commonly used
 * imports here to make them available to all custom scripts. Note thate moving
 * or removing exisitng imports and code from this file may cause built-in
 * scripts to fail.
 * @author Ryan Sandor Richards
 */

this.Log = Packages.solace.util.Log;

Log.info('Solace scripting engine initialized!');

/**
 * Allows for the creation of commands through the scripting engine.
 * @author Ryan Sandor Richards
 */
this.Commands = (function () {
  var Commands = Packages.solace.script.Commands;
  var ScriptedPlayCommand = Packages.solace.script.ScriptedPlayCommand;

  return {
    /**
     * Adds a new play command to the game.
     * @param {string} name Name of the command to add.
     * @param {function} lambda Function call when executing the command.
     */
    add: function (name, lambda) {
      Commands.playCommands.add(new ScriptedPlayCommand(name, lambda));
    }
  };
})();
