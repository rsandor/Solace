package solace.util;
import java.util.StringTokenizer;
import javax.xml.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

/**
 * Contains various helper methods for handling input and output
 * @author Ryan Sandor Richards.
 */
public class Strings {
  public static final String RULE =
    "o------------------------------------------------------------------------------o\n\r";

  /**
   * Formats a string to a fixed width number of characters, useful for
   * paragraphs.
   * @param s String to format.
   * @param w Maximum column width.
   * @return The formatted string.
   */
  public static String toFixedWidth(String s, int w) {
    StringTokenizer tokenizer = new StringTokenizer(s);
    StringBuffer buf = new StringBuffer();
    int col = 0;

    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (col + 1 + token.length() > w) {
        buf.append("\n" + token);
        col = token.length();
        continue;
      }
      else if (col > 0) {
        buf.append(" ");
        col++;
      }
      buf.append(token);
      col += token.length();
    }

    return buf.toString();
  }

  /**
   * Formats a string to a fixed width of 80 characters.
   * @param s String to format.
   * @return The formatted string.
   */
  public static String toFixedWidth(String s) {
    return toFixedWidth(s, 80);
  }

  /**
   * Returns a string containing the given number of space characters.
   * @param n Number of spaces for the string.
   * @return A string with exactly the given number of spaces.
   */
  public static String spaces(int n) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < n; i++) {
      b.append(' ');
    }
    return b.toString();
  }

  /**
   * Creates a centered title surrounded by rules.
   * @param text Text for the centered title.
   * @return The ascii art title.
   */
  public static String centerTitle(String text) {
    StringBuffer b = new StringBuffer();
    b.append(RULE);

    int s = 80 - 2 - text.length();
    String left;
    String right;

    if (s % 2 == 0) {
      left = right = spaces(s/2);
    }
    else {
      left = spaces(s/2);
      right = spaces(s/2) + " ";
    }

    b.append("|");
    b.append(left);
    b.append(text);
    b.append(right);
    b.append("|\n\r");
    b.append(RULE);

    return b.toString();
  }

  /**
   * Compresses spaces in XML character data.
   *
   */
  public static String xmlCharacters(char[] ch, int start, int length) {
    StringBuffer desc = new StringBuffer(length);
    boolean space = false;

    for (int i = start; i < start+length; i++) {
      char a = ch[i];

      if (a == '\n' && i > 0 && ch[i-1] == '\n') {
        a = '\n';
      }
      else if (a == '\n' || a == ' ' || a == '\t') {
        if (space)
          continue;
        a = ' ';
        space = true;
      }
      else {
        space = false;
      }
      desc.append(a);
    }

    if (desc.toString() == null)
      return "";

    String str = desc.toString().trim();

    if (str.equals("") || str == null || str.length() == 0)
      return "";

    str = " " + str;

    return str;
  }

  /**
   * Reformats given xml to make it human readable.
   * @param xml XML to format.
   */
  public static String prettyXML(String xml) {
    try {
      Source xmlInput = new StreamSource(new StringReader(xml));
      StringWriter stringWriter = new StringWriter();
      StreamResult xmlOutput = new StreamResult(stringWriter);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      transformerFactory.setAttribute("indent-number", 2);
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.transform(xmlInput, xmlOutput);
      return xmlOutput.getWriter().toString();
    } catch (Exception e) {
      Log.error("Error generating prettyXML:");
      e.printStackTrace();
    }
    return "";
  }
}
