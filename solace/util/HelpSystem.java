package solace.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Queries and fetches help articles.
 * @author Ryan Sandor Richards
 */
public class HelpSystem {
  /**
   * Internal helper class used for logging meaningful messages when processing a help page.
   * @author Ryan Sandor Richards
   */
  private class HelpPageLoadException extends Exception {
    HelpPageLoadException(String msg) {
      super(msg);
    }
  }

  private Directory directory = new RAMDirectory();
  private NameTrie<HelpPage> pageByName;
  private Analyzer analyzer;

  /**
   * Reloads all help pages.
   * @throws IOException If an error occurs while reloading help pages.
   */
  public void reload() throws IOException {
    Log.info("Reloading game help pages.");

    directory.close(); // Close out the old ram directory (if any)

    Set<String> titles = new HashSet<>();
    directory = new RAMDirectory();
    pageByName = new NameTrie<>(null);
    analyzer = new EnglishAnalyzer();


    IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
    GameFiles.findHelpFiles().forEach(path -> {
      String filename = String.valueOf(path);
      Log.debug(String.format("Loading help page '%s'", filename));
      try {
        HelpPage page = HelpPage.fromPath(path);

        // Check for duplicate names
        String name = page.getName().toLowerCase();
        if (pageByName.containsName(name)) {
          throw new HelpPageLoadException(String.format(
            "Duplicate name '%s' encountered for help file '%s', skipping.", name, filename));
        }

        // Check for duplicate titles
        String title = page.getTitle().toLowerCase();
        if (titles.contains(title)) {
          throw new HelpPageLoadException(String.format(
            "Duplicate title '%s' encountered for help file '%s', skipping.", page.getTitle(), filename));
        }

        pageByName.put(name, page);
        titles.add(title);

        Document document = new Document();
        document.add(new Field("name", page.getName(), TextField.TYPE_STORED));
        document.add(new Field("title", page.getTitle(), TextField.TYPE_STORED));
        document.add(new Field("body", page.getPlainText(), TextField.TYPE_STORED));
        indexWriter.addDocument(document);
      } catch (RequiredAnnotationException rae) {
        Log.warn(String.format("Failed to find @name annotation in help page file '%s', skipping.", filename));
      } catch (TitleNotFoundException tnf) {
        Log.warn(String.format("Failed to find title in markdown in help page file '%s', skipping.", filename));
      } catch (HelpPageLoadException hpl) {
        Log.warn(hpl.getMessage());
      } catch (Throwable t) {
        Log.warn(String.format("Unknown error encountered when parsing help file '%s', skipping.", filename));
        Log.warn(t.getMessage());
      }
    });
    indexWriter.close();
  }

  /**
   * Attempts to find a help page by direct match. If no such page could be found then
   * this will then use the full text of the parameters to the help command to perform
   * a search.
   * @param prefix Direct name prefix to match against.
   * @param text Full text to search with.
   * @return The help page or the search results.
   */
  @SuppressWarnings("unused")
  public String direct(String prefix, String text) {
    HelpPage page = pageByName.find(prefix.toLowerCase());
    if (page != null) {
      return page.getDisplayText();
    }
    return search(text);
  }

  /**
   * Searches for help files that match the given text.
   * @param text Text for which to search.
   * @return A string describing the matching pages by rank.
   */
  public String search(String text) {
    StringBuilder results = new StringBuilder();
    try {
      DirectoryReader reader = DirectoryReader.open(directory);
      IndexSearcher searcher = new IndexSearcher(reader);

      QueryParser parser = new QueryParser("body", analyzer);
      Query query = parser.parse(text);
      ScoreDoc[] hits = searcher.search(query, 10).scoreDocs;

      if (hits.length > 0) {
        results.append("Search results:\n\r\n\r");
        for (int i = 0; i < hits.length; i++) {
          Document document = searcher.doc(hits[i].doc);
          HelpPage page = pageByName.find(document.get("name"));
          results.append(String.format(
            "    %-2d. [{y}%s{x}] {c}%s{x}\n\r",
            (i + 1), page.getName(), document.get("title")));
        }
      } else {
        results.append("No help pages match the given query.");
      }

      reader.close();
    } catch (Throwable t) {
      Log.error("Error searching help pages lucene: " + t.getMessage());
      t.printStackTrace();
      return "No help pages match the given query.";
    }
    return results.toString();
  }

  /**
   * Default help system instance.
   */
  private static HelpSystem instance = new HelpSystem();

  /**
   * @return the default help system instance.
   */
  public static synchronized HelpSystem getInstance() { return instance; }
}
