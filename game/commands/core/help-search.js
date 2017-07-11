'use strict';

var HelpSystem = Packages.solace.util.HelpSystem;

/**
 * Performs a search for the given query in the help system and displays the
 * results to the user.
 * @author Ryan Sandor Richards
 */
Commands.add('help-search', function (player, params) {
  var help = HelpSystem.getInstance();
  var result;
  if (params.length < 2) {
    result = help.direct("help", "");
  } else {
    var queryBuilder = new StringBuilder();
    for (var k = 1; k < params.length; k++) {
      queryBuilder.append(params[k]).append(" ");
    }
    var queryText = queryBuilder.toString().trim();
    result = help.search(queryText);
  }
  player.sendln(result);
});
