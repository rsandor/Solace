package solace.util;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class for converting 'Solace flavored' markdown files into color
 * encoded plain text. Primarily used by the help system, but abstracted for
 * future use in possible notes (BBS) and mail system.
 * @author Ryan Sandor Richards
 */
public class Markdown {
  static final String RULE = "~o-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~-~o~\n\r";

  /**
   * Converts a list of lines representing markown into plain text with colors.
   * @param lines Lines of the markdown to convert.
   * @return The plain text parse of the given markdown lines.
   */
  public static String convert(List<String> lines) {
    StringBuffer buf = new StringBuffer();
    for (String line : lines) {

      if (line.startsWith("##")) {
        String hd = line.split("[#]+\\s*")[1].trim();
        buf.append("{c# " + hd + "{x\n\r");
        continue;
      }

      if (line.startsWith("#")) {
        String h1 = line.split("#\\s*")[1].trim();
        buf.append(RULE);
        buf.append(" ){B " + h1 + "{x");

        for (int i = 80 - h1.length() - 5; i > 0; i--) {
          buf.append(" ");
        }
        buf.append("(\n\r");

        buf.append(RULE + "\n\r");
        continue;
      }

      buf.append(
        line
          // Backticks should be yellow
          .replaceAll("`([^`]+)`", "{y$1{x")
          // Bullet points should display as green
          .replaceAll("^\\s*\\*\\s*", "{g*{x ")
          // Brackets should be cyan
          .replaceAll("\\[([^\\]]+)\\]", "{c[$1]{x")
      );

      buf.append("\n\r");
    }

    return buf.toString();
  }

  /**
   * Reads the file at the given path and converts the contained markdown into
   * plain text with colors.
   * @param path Path to the file to convert.
   * @return The plain text parse of the markdown in the given file.
   */
  public static String convertFile(String path)
    throws IOException
  {
    return convert(Files.readAllLines(Paths.get(path)));
  }
}
