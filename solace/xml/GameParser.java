package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;

import solace.game.*;
import solace.util.*;

/**
 * Contains helper methods for parsing various types of game XML files.
 * @author Ryan Sandor Richards
 */
public class GameParser {
  /**
   * Parses a generic XML file with a given handler and returns the result.
   * @param fileName Name of the file being parsed.
   * @param handler Handler to use for the parse.
   * @return The object result of the parse.
   */
  protected static Object parse(String fileName, Handler handler)
    throws IOException
  {
    try {
      SAXParserFactory factory = SAXParserFactory.newInstance();
      factory.setValidating(false);
      SAXParser parser = factory.newSAXParser();
      parser.parse(fileName, handler);
      return handler.getResult();
    }
    catch (IOException ioe) {
      throw ioe;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Parses a configuration file.
   * @param fileName Name of the file to parse.
   * @return The configuration hash parsed from the file.
   */
  public static Configuration parseConfiguration(String fileName)
    throws IOException {
    return (Configuration)parse(fileName, new ConfigHandler());
  }

  /**
   * Parses a user account file.
   * @param fileName Name of the user account file to parse.
   * @return The user account parsed from the file.
   */
  public static Account parseAccount(String fileName) throws IOException {
    return (Account)parse(fileName, new AccountHandler());
  }

  /**
   * Parses an area XML file and generates an <code>Area</code> game object.
   * @param filename Name of the area to parse. The file is loaded relative to
   *   the default areas path 'Areas/'.
   * @return The area generated as a result of parsing the file.
   */
  public static Area parseArea(String fileName) throws IOException {
    return (Area)parse(fileName, new AreaHandler());
  }
}
