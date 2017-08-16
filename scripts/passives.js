'use strict';

/**
 * Allows for the creation of passive enhancements through the scripting engine.
 * @author Ryan Sandor Richards
 */
this.Passives = (function () {
  var Passive = Packages.solace.game.Passive;
  var ScriptedPassives = Packages.solace.script.ScriptedPassives;

  return {
    /**
     * Adds a new passive to the game.
     * @param {string} name Name for the new passive.
     * @param {function} Callback to execute to sculpt the passive.
     */
    add: function (name, callback) {
      var passive = new Passive(name);
      var effect = passive.getEffect();
      callback(passive, effect);
      ScriptedPassives.add(passive);
    }
  };
})();
