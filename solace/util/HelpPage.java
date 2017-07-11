package solace.util;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Represents a single page in the help system.
 * @author Ryan Sandor Richards
 */
public class HelpPage {
  private Document document;
  private String displayText;
  private Map<String, String> annotations;

  /**
   * Creates a new help page with given markdown source.
   * @param src Markdown source for the page.
   */
  private HelpPage(String src) throws RequiredAnnotationException, TitleNotFoundException {
    annotations = Markdown.getAnnotationMap(src);
    if (!hasAnnotation("name")) {
      throw new RequiredAnnotationException("@name");
    }
    displayText = Markdown.render(src);
    document = createLuceneDocument(getName(), Markdown.getTitle(src), Markdown.strip(src));
  }

  /**
   * Generates a lucene document from the given markdown source.
   * @param name The direct name of the page.
   * @param title The title of the page.
   * @param body The plain text body of the page
   * @return An indexable lucene document for the given markdown.
   */
  private Document createLuceneDocument(String name, String title, String body) {
    Document doc = new Document();
    doc.add(new Field("name", name, TextField.TYPE_STORED));
    doc.add(new Field("title", title, TextField.TYPE_STORED));
    doc.add(new Field("body", body, TextField.TYPE_STORED));
    return doc;
  }

  /**
   * Determines if the help page has an annotation of the given name.
   * @param name Name of the annotation.
   * @return True if the document has an annotation of the given name, false otherwise.
   */
  boolean hasAnnotation(String name) { return annotations.keySet().contains(name); }

  /**
   * Finds the value for a help page annotation with the given name.
   * @param name Name of the annotation.
   * @return The value of the annotation, or the empty string if no such annotation was found.
   */
  String getAnnotation(String name) {
    if (!hasAnnotation(name)) {
      return "";
    }
    return annotations.get(name);
  }

  /**
   * @return The @name annotation for the help file.
   */
  String getName() { return getAnnotation("name"); }

  /**
   * @return The indexable lucene document associated with the help page.
   */
  Document getDocument() {
    return document;
  }

  /**
   * @return The renderd text to display to users for this help page.
   */
  public String getDisplayText() {
    return displayText;
  }

  /**
   * Creates a new help page from a given path.
   * @param path Path to a help page markdown file.
   * @return The help page.
   * @throws IOException If an error occurs while reading the path.
   * @throws RequiredAnnotationException If an required annotation was missing from the help page markdown.
   * @throws TitleNotFoundException If a title could not be determined from the markdown in the file.
   */
  public static HelpPage fromPath(Path path)
    throws IOException, RequiredAnnotationException, TitleNotFoundException
  {
    return new HelpPage(new String(Files.readAllBytes(path)));
  }
}
