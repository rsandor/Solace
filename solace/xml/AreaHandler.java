package solace.xml;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import java.io.*;
import solace.game.*;
import solace.util.Config;
import java.util.*;

/**
 * Handles the parsing of area XML files.
 * @author Ryan Sandor Richards.
 */
public class AreaHandler extends Handler {
	/**
	 * Enumeration for the basic states handled by the parser.
	 */
	private enum State { INIT, AREA, ROOM, EXIT, TITLE }
	
	// Instance variables
	Area area = null;
	Room currentRoom = null;
	Exit currentExit = null;
	State state = State.INIT;
	Stack<StringBuffer> buffers = new Stack<StringBuffer>();
	
	/**
	 * @return The area as a result of the parse, or <code>null</code> if no area could be 
	 *   or has yet been parsed.
	 */
	public Object getResult() {
		return area;
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void startElement(String uri, String localName, String name, Attributes attrs) {
		if (name == "area" && state == State.INIT) {
			String id = attrs.getValue("id");
			String title = attrs.getValue("title");
			String author = attrs.getValue("author");

			if (id == null)
				id = "";
			if (title == null) 
				title = "";
			if (author == null)
				author = "";

			area = new Area(id, title, author);
			state = State.AREA;
		}
		else if (name == "room" && state == State.AREA) {
			String id = attrs.getValue("id");
			currentRoom = new Room(id);
			state = State.ROOM;
		}
		else if (name == "title" && state == State.ROOM) {
			state = State.TITLE;
			buffers.push(new StringBuffer());
		}
		else if (name == "exit" && state == State.ROOM) {
			String names = attrs.getValue("names");
			String to = attrs.getValue("to");	
			currentExit = new Exit(names, to);

			String exitColor = Config.get("world.colors.room.exit");
			if (exitColor == null)
				exitColor = "";
			currentRoom.addToDescription(exitColor);

			state = State.EXIT;
		}
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, String localName, String name) 
	{
		if (name == "area") {
			state = State.INIT;
		}
		else if (name == "room") {
			// TODO Check for duplicate ids
			area.addRoom(currentRoom);
			state = State.AREA;
		}
		else if (name == "title") {
			StringBuffer buffer = buffers.pop();
			currentRoom.setTitle(buffer.toString());
			state = State.ROOM;
		}
		else if (name == "exit") {
			String desc = currentRoom.getDescription();
			String endColor = Config.get("world.colors.room.exit") == null ? "" : "{x"; 
			currentRoom.setDescription(desc.trim() + endColor);
			currentRoom.addExit(currentExit);
			state = State.ROOM;
		}
	}
	
	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void characters(char[] ch, int start, int length) 
	{
		StringBuffer desc = new StringBuffer(length);
		boolean space = false;
		
		for (int i = start; i < start+length; i++) {
			char a = ch[i];
			
			if (a == '\n' && i > 0 && ch[i-1] == '\n')
				a = '\n';
			else if (a == '\n' || a == ' ' || a == '\t') {
				if (space)
					continue;
				a = ' ';
				space = true;
			}
			else
				space = false;
			desc.append(a);
		}
		
		if (desc.toString() == null)
			return;
			
		String str = desc.toString().trim();
		
		if (str == "" || str == null || str.length() == 0)
			return;
			
		str = " " + str;
		
		if (state == State.ROOM || state == State.EXIT)
			currentRoom.addToDescription(str);
		if (state == State.EXIT)
			currentExit.addToDescription(str);
		if (state == State.TITLE)
			buffers.peek().append(str);
	}
}