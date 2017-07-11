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
  private String plainText;
  private String displayText;
  private Map<String, String> annotations;
  private String title;

  /**
   * Creates a new help page with given markdown source.
   * @param src Markdown source for the page.
   */
  private HelpPage(String src) throws RequiredAnnotationException, TitleNotFoundException {
    annotations = Markdown.getAnnotationMap(src);
    if (!hasAnnotation("name")) {
      throw new RequiredAnnotationException("@name");
    }
    plainText = Markdown.strip(src);
    displayText = Markdown.render(src);
    title = Markdown.getTitle(src);
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
   * @return The @name annotation for the help page.
   */
  String getName() { return getAnnotation("name"); }

  /**
   * @return The title of the help page.
   */
  String getTitle() { return title; }

  /**
   * @return Plain text of the help file with markdown syntax stripped.
   */
  String getPlainText() { return plainText; }

  /**
   * @return True if the page has been flagged as admin only, false otherwise.
   */
  boolean isAdminOnly() { return hasAnnotation("admin"); }

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
