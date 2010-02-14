package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import solace.game.*;
import java.util.*;

/**
 * Handles the parsing of area XML files.
 * @author Ryan Sandor Richards.
 */
public class AccountHandler extends Handler {
	/**
	 * Enumeration for the basic states handled by the parser.
	 */
	private enum State { INIT, USER, CHARACTERS, CHARACTER }
	
	// Instance variables
	Account account;
	solace.game.Character character;
	
	/**
	 * @return The area as a result of the parse, or <code>null</code> if no area could be 
	 *   or has yet been parsed.
	 */
	public Object getResult() {
		return account;
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void startElement(String uri, String localName, String name, Attributes attrs) {
		if (name == "user") {
			String accountName = attrs.getValue("name");
			String admin = attrs.getValue("admin");
			String password = attrs.getValue("password");
			account = new Account(accountName, password, Boolean.parseBoolean(admin));
		}
		else if (name == "character") {
			String characterName = attrs.getValue("name");
			character = new solace.game.Character(characterName);
		}
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, String localName, String name) {
		if (name == "character") {
			account.addCharacter(character);
		}
	}
}