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
