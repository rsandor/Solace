package solace.util;

import java.nio.file.*;
import java.util.*;
import java.io.*;

/**
 * Queries and fetches help articles.
 * @author Ryan Sandor Richards
 */
public class HelpSystem {
  /**
   * Path to the keywords file relative to the project root.
   */
  private static final String KEYWORDS_FILE = "game/help/keywords.yml";

  /**
   * Path the the games help files.
   */
  private static final String HELP_PATH = "game/help/";

  // Instance variables
  private HashSet<String> keywords;
  private Hashtable<String, List<String>> keywordIndex;
  private Hashtable<String, String> primaryKeyword;
  private Hashtable<String, String> articles;

  /**
   * Creates a new help system to query help articles.
   */
  private HelpSystem() {
    Log.info("Generating help system keyword indexes");
    generateKeywordIndex();
    articles = new Hashtable<>();
    Log.info("Help system loaded and ready to query");
  }

  /**
   * Generates an keyword index and primary keywords for help articles based on
   * the keywords file.
   */
  private void generateKeywordIndex() {
    keywords = new HashSet<>();
    keywordIndex = new Hashtable<>();
    primaryKeyword = new Hashtable<>();

    try {
      for (String line : Files.readAllLines(Paths.get(KEYWORDS_FILE))) {
        String[] parts = line.split(":\\s*");

        if (parts.length < 2) {
          continue;
        }

        String path = parts[0];
        String[] articleKeywords = parts[1].split("\\s+");

        if (articleKeywords.length < 1) {
          continue;
        }

        // The first given keyword should always be unique to the path
        primaryKeyword.put(path, articleKeywords[0]);

        for (String keyword : articleKeywords) {
          keywords.add(keyword);

          List<String> files;
          if (!keywordIndex.containsKey(keyword)) {
            files = new LinkedList<>();
            keywordIndex.put(keyword, files);
          }
          else {
            files = keywordIndex.get(keyword);
          }
          files.add(parts[0]);
        }
      }
    }
    catch (IOException ioe) {
      Log.error("Unable to read help system index file: " + KEYWORDS_FILE);
      ioe.printStackTrace();
    }
  }

  /**
   * Fetches a particular article from the help system.
   * @param path Path to the article markdown file in the data directory.
   * @return The article's contents.
   */
  public String getArticle(String path) {
    if (articles.containsKey(path)) {
      return articles.get(path);
    }

    try {
      String article = Markdown.convertFile(HELP_PATH + path);
      articles.put(path, article);
      return article;
    }
    catch (IOException ioe) {
      Log.error("Problem reading help article: " + path);
      ioe.printStackTrace();
      return "A problem occurred, please try again later.";
    }
  }

  /**
   * Queries the help system and attempts to find an article or set of artciles
   * that match the given keywords.
   * @param input Prefix keywords to use for the search (given by user)
   * @return A list of articles matching the given keywords or the article
   */
  public String query(Set<String> input) {
    TreeSet<String> articlePaths = new TreeSet<>();
    TreeSet<String> validKeywords = new TreeSet<>();

    // See if any of the keywords given is a prefix to a keyword in the system
    for (String keyword : keywords) {
      for (String prefix : input) {
        if (keyword.startsWith(prefix)) {
          validKeywords.add(keyword);
          break;
        }
      }
    }

    for (String keyword : validKeywords) {
      // This should never happen if we've parsed the files correctly...
      if (!keywordIndex.containsKey(keyword)) {
        continue;
      }
      articlePaths.addAll(keywordIndex.get(keyword));
    }

    // Couldn't find an article that matches
    if (articlePaths.size() == 0) {
      return "No articles with the given keywords could be found.";
    }

    // Found exactly one, simply return its contents
    if (articlePaths.size() == 1) {
      return getArticle(articlePaths.first());
    }

    // Found multiple, give the player a list of articles to check out
    StringBuilder result = new StringBuilder();
    result.append("The following articles match your search:\n\r\n\r");
    for (String path : articlePaths) {
      result.append("  {y}").append(primaryKeyword.get(path)).append("{x}\n\r");
    }
    return result.toString();
  }

  /**
   * Default help system instance.
   */
  private static HelpSystem instance = new HelpSystem();

  /**
   * Reloads the game's help messages.
   */
  public static void reload() {
    instance = new HelpSystem();
  }

  /**
   * @return the default help system instance.
   */
  public static synchronized HelpSystem getInstance() {
    return instance;
  }
}
