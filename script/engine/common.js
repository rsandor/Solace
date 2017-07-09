'use strict';

/**
 * Adds commonly used objects and functions to the global scope for use by
 * all scripts.
 * @author Ryan Sandor Richards
 */

this.Log = Packages.solace.util.Log;
this.Strings = Packages.solace.util.Strings;
this.Color = Packages.solace.util.Color;

this.format = java.lang.String.format;
this.StringBuffer = java.lang.StringBuffer;
this.StringBuilder = java.lang.StringBuilder;

this.Joiner = Packages.com.google.common.base.Joiner;
