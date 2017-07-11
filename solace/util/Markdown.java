package solace.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for converting 'Solace flavored' markdown files into color
 * encoded plain text. Primarily used by the help system, but abstracted for
 * future use in possible notes (BBS) and mail system.
 * @author Ryan Sandor Richards
 */
class Markdown {
  private static final Pattern titlePattern = Pattern.compile("^\\s*[#]([^#].*)\\s*$");
  private static Pattern nullAnnotation = Pattern.compile("@([a-zA-Z0-9\\-]+)");
  private static Pattern valuedAnnotation = Pattern.compile("@([a-zA-Z0-9\\-]+)\\(([^)]+)\\)");

  // TODO We should really reference this from a message.
  private static final String RULE =
    "~o-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~o~\n\r";

  /**
   * Renders a markdown document so it may be correctly displayed to a user.
   * @param src Markdown document source to render.
   * @return The color encoded and rendered result.
   */
  static String render(String src) {
    String markdown = stripAnnotations(src);
    StringBuilder buf = new StringBuilder();
    Arrays.asList(markdown.split("\\n\\r?")).forEach(line -> {
      if (line.startsWith("##")) {
        String hd = line.split("[#]+\\s*")[1].trim();
        buf.append("{c}# ").append(hd).append("{x}\n\r");
        return;
      }

      if (line.startsWith("#")) {
        String h1 = line.split("#\\s*")[1].trim();
        buf.append(RULE);
        buf.append(" ){B} ").append(h1).append("{x}");

        for (int i = 80 - h1.length() - 5; i > 0; i--) {
          buf.append(" ");
        }
        buf.append("(\n\r");

        buf.append(RULE + "\n\r");
        return;
      }

      buf.append(
        line
          // Backticks should be yellow
          .replaceAll("`([^`]+)`", "{y}$1{x}")
          // Bullet points should display as green
          .replaceAll("^\\s*\\*\\s*", "{g}*{x} ")
          // Brackets should be cyan
          .replaceAll("\\[([^]]+)]", "{c}[$1]{x}")
      );

      buf.append("\n\r");
    });

    return buf.toString();
  }

  /**
   * Strips all markdown controls and returns the plain text of the given document.
   * @param markdown Markdown document to strip.
   * @return The plain text of the markdown document.
   */
  static String strip(String markdown) {
    return stripAnnotations(markdown)
      .replaceAll("^\\s*[#]+\\s*(.*)$", "$1")
      .replaceAll("`([^`]+)`", "$1")
      .replaceAll("^\\s*\\*\\s*", "")
      .replaceAll("\\[([^]]+)]", "$1");
  }

  /**
   * Builds a map of all annotation names to values.
   * @param markdown The markdown from which to get the annotation map.
   * @return The annotation map.
   */
  static Map<String, String> getAnnotationMap(String markdown) {
    Map<String, String> annotations = new Hashtable<>();

    // Check for valued annotations first, then strip them
    Matcher valuedMatcher = valuedAnnotation.matcher(markdown);
    while (valuedMatcher.find()) {
      annotations.put(valuedMatcher.group(1), valuedMatcher.group(2).trim());
    }
    markdown = valuedMatcher.replaceAll("");

    // Check for non-valued or "null" annotations
    Matcher nullMatcher = nullAnnotation.matcher(markdown);
    while(nullMatcher.find()) {
      annotations.put(valuedMatcher.group(1), "");
    }

    return annotations;
  }

  /**
   * Strips annotations from a markdown document.
   * @param markdown The markdown from which to strip annotations.
   * @return The annotation stripped markdown.
   */
  private static String stripAnnotations(String markdown) {
    Matcher valuedMatcher = valuedAnnotation.matcher(markdown);
    markdown = valuedMatcher.replaceAll("");
    Matcher nullMatcher = nullAnnotation.matcher(markdown);
    markdown = nullMatcher.replaceAll("");
    return markdown.trim();
  }

  /**
   * Finds the first title (first h1 element) of the given markdown.
   * @param markdown Markdown from which to find the title
   * @return The title text of the markdown.
   * @throws TitleNotFoundException If no title could be found.
   */
  static String getTitle(String markdown) throws TitleNotFoundException {
    String[] lines = markdown.split("\\n\\r?");
    for (String line : lines) {
      Matcher m = titlePattern.matcher(line);
      if (m.matches()) {
        return m.replaceAll("$1").trim();
      }
    }
    throw new TitleNotFoundException();
  }
}
