package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import solace.util.*;
import java.util.*;

/**
 * Handler for parsing game configuration files.
 * @author Ryan Sandor Richards.
 */
public class ConfigHandler extends Handler {
	// Instance variables
	Configuration config;
	Stack<String> scope = new Stack<String>();
	
	/** 
	 * @return The scope prefix for an option.
	 */
	protected String getScope() {
		StringBuffer buf = new StringBuffer("");
		for (String s : scope) 
			buf.append(s).append('.');
		return buf.toString();
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void startElement(String uri, String localName, String name, Attributes attrs) {
		if (name == "config") {
			config = new Configuration(attrs.getValue("name"));
		}
		else if (name == "option") {
			String n = attrs.getValue("name");
			String v = attrs.getValue("value");
			config.put(getScope()+n, v);
		}
		else {
			scope.push(name);
		}
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, String localName, String name) {
		if (name != "config" && name != "option" && scope.size() > 0)
			scope.pop();
	}
	
	/**
	 * @return The configuration hash generated from the file.
	 */
	public Object getResult() {
		return config;
	}
}