package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;

/**
 * XML File handler for the Solace engine.
 * @author Ryan Sandor Richards
 */
public abstract class Handler extends DefaultHandler {
	String fileName;
	
	/**
	 * Creates a new handler.
	 */
	public Handler() {
		fileName = "";
	}
	
	/**
	 * Creates a new handler.
	 * @param fn Name of the file being parsed.
	 */
	public Handler(String fn) {
		fileName = fn;
	}
	
	/**
	 * @return The object resulting from the parse.
	 */
	public abstract Object getResult();
}