'use strict';

/**
 * Exposes the engine's Log utility directly to the global scope for use by
 * all scripts.
 */
this.Log = Packages.solace.util.Log;

/**
 * Expose Java's String.format method as a globale.
 */
this.format = java.lang.String.format;
