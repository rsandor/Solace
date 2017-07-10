package solace.xml;

import com.google.common.base.Joiner;
import org.xml.sax.*;
import solace.util.*;
import java.util.*;

/**
 * Handler for parsing game configuration files.
 * @author Ryan Sandor Richards.
 */
public class ConfigHandler extends Handler {
  private Configuration config;
  private Stack<String> scope = new Stack<>();

  /**
   * @return The scope prefix for an option.
   */
  private String getScope() {
    return Joiner.on(".").join(scope) + ".";
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void startElement(String uri, String localName, String name, Attributes attrs) {
    switch (name) {
      case "config":
        config = new Configuration(attrs.getValue("name"));
        break;
      case "option":
        String n = attrs.getValue("name");
        String v = attrs.getValue("value");
        config.put(getScope() + n, v);
        break;
      default:
        scope.push(name);
        break;
    }
  }

  /**
   * @see org.xml.sax.helpers.DefaultHandler
   */
  public void endElement(String uri, String localName, String name) {
    if (!name.equals("config") && !name.equals("option") && scope.size() > 0) {
      scope.pop();
    }
  }

  /**
   * @return The configuration hash generated from the file.
   */
  public Object getResult() { return config; }
}
