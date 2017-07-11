'use strict';

var HelpSystem = Packages.solace.util.HelpSystem;

/**
 * Builds a single query string from a help command's parameters.
 * @param {Array} params Parameters (including command name) for the command.
 * @return A help system query string to build from the parameters.
 */
function getQuery(params) {
  if (params.length < 2) {
    return "help";
  } else {
    var queryBuilder = new StringBuilder();
    for (var k = 1; k < params.length; k++) {
      queryBuilder.append(params[k]).append(" ");
    }
    return queryBuilder.toString().trim();
  }
}

/**
 * Helper function to perform a help system search query.
 * @param {Player} player The player making the search.
 * @param {String} query The query for which to search.
 * @return {String} The results of the search to display to the player.
 */
function search(player, query) {
  var help = HelpSystem.getInstance();
  var results = help.search(player, query);

  if (results.length === 0) {
    return format("No help pages matching '%s' found.", query);
  }

  var k = 1;
  var resultsBuilder = new StringBuilder();
  resultsBuilder.append(format(
    "%d search results found for '{m}%s{x}':\n\r\n\r", results.length, query));

  results.forEach(function (page) {
    resultsBuilder.append(format(
      "%3s. {y}[%s]{x} {c}%s{x}\n\r", k+"", page.getName(), page.getTitle()));
    k++;
  });

  return resultsBuilder.toString();
}

/**
 * Help command, allows players to search for and browse pages in the help
 * system.
 * @author Ryan Sandor Richards
 */
Commands.add('help', function (player, params) {
  var help = HelpSystem.getInstance();
  var query = getQuery(params);
  try {
    var page = help.getPage(player, query);
    player.sendln(page.getRenderedMarkdown());
  } catch (e) {
    player.sendln(search(player, query));
  }
});

/**
 * Performs a search for the given query in the help system and displays the
 * results to the user.
 * @author Ryan Sandor Richards
 */
Commands.add('help-search', {
  aliases: ['helpsearch'],
  run: function (player, params) {
    player.sendln(search(player, getQuery(params)));
  }
});
