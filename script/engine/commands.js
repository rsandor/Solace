'use strict';

/**
 * Allows for the creation of commands through the scripting engine.
 * @author Ryan Sandor Richards
 */
this.Commands = (function () {
  var Commands = Packages.solace.script.Commands;
  var ScriptedPlayCommand = Packages.solace.script.ScriptedPlayCommand;
  var ScriptedCooldown = Packages.solace.script.ScriptedCooldown;
  var SpCost = Packages.solace.cmd.SpCost;
  var MpCost = Packages.solace.cmd.MpCost;

  /**
   * General error handler for logging issues when adding game commands.
   * @param {Error} e The error that occurred.
   */
  function errorHandler (e) {
    Log.error('Cannot create script command - ' + e.getMessage());
  }

  /**
   * Adds a new play command to the game.
   * @param {string} name Name of the command to add.
   * @param {function|object} options An object with options to use for the
   *   creation of the command, or simply the lambda run handler for the
   *   command.
   */
  function addPlayCommand (name, options) {
    var displayName, runLambda;

    if (typeof options === 'function') {
      displayName = name;
      runLambda = options;
    } else if (typeof options === 'object') {
      if (!options.run) {
        throw new Error('Commands.add: missing run function for ' + name + '.');
      }
      displayName = options.displayName || name;
      runLambda = options.run;
    } else {
      throw new Error('Commands.add: invalid options given for ' + name);
    }

    var command = new ScriptedPlayCommand(name, displayName, runLambda);
    Commands.add(command);
  }

  /**
   * Adds a cooldown command to the game.
   */
  function addCooldown (name, options) {
    if (typeof options !== 'object') {
      throw new Error(
        'Commands.addCooldown: invalid options given for ' + name + '.'
      )
    }

    if (typeof options.run !== 'function' && !options.executeAttack) {
      throw new Error(
        'Commands.addCooldown: missing run function for ' + name + '.'
      );
    }

    var cooldown = new ScriptedCooldown(name, options.displayName || name)
    cooldown.setCooldownDuration(options.cooldownDuration || 180);
    cooldown.setInitiatesCombat(options.initiatesCombat || false);
    cooldown.setCastTime(options.castTime || 0);
    cooldown.setCastMessage(options.castMessage || "You begin to cast...");
    cooldown.setCombosWith(options.combosWith || null);
    cooldown.setBasePotency(options.basePotency || 100);
    cooldown.setComboPotency(options.comboPotency || 0);
    cooldown.setSavingThrow(options.savingThrow || null);
    cooldown.setExecuteLambda(options.run);

    if (typeof options.spCost === 'number') {
      cooldown.addResourceCost(new SpCost(options.spCost));
    }
    if (typeof options.mpCost === 'number') {
      cooldown.addResourceCost(new MpCost(options.mpCost));
    }

    if (typeof options.checkValidTarget == 'function') {
      cooldown.setCheckValidTarget(options.checkValidTarget);
    }

    Commands.add(cooldown);
  }

  /**
   * Expose the interface for the script command service.
   */
  return {
    GLOBAL_COOLDOWN: -1,
    add: function (name, options) {
      try {
        addPlayCommand(name, options);
      } catch (e) {
        errorHandler(e);
      }
    },
    addCooldown: function (name, options) {
      try {
        addCooldown(name, options);
      } catch(e) {
        errorHandler(e);
      }
    },
  };
})();
