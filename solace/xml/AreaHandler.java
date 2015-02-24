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
	static Area area = null;
	static Room room = null;
	static Exit exit = null;
	static Item item = null;
	static String propertyKey = null;

	static StringBuffer description = null;
	static String descriptionNames = null;

	static Stack<StringBuffer> buffers = new Stack<StringBuffer>();

	/**
	 * Enumeration for the basic states handled by the parser.
	 */
	private enum State {
		INIT {
			public State start(String name, Attributes attrs) {
				if (name != "area")
					return INIT;

				String id = attrs.getValue("id").trim();
				String title = attrs.getValue("title").trim();
				String author = attrs.getValue("author").trim();

				if (id == null)
					id = "";
				if (title == null)
					title = "";
				if (author == null)
					author = "";

				area = new Area(id, title, author);
				return AREA;
			}

			public State end(String name) {
				return INIT;
			}
		},

		AREA() {
			public State start(String name, Attributes attrs) {
				if (name == "room") {
					String id = attrs.getValue("id").trim();
					room = new Room(id);
					return ROOM;
				}
				else if (name == "item") {
					String id = attrs.getValue("id"),
						names = attrs.getValue("names");
					item = new Item(id, names, area);
					return ITEM;
				}

				return AREA;
			}

			public State end(String name) {
				return INIT;
			}
		},

		ROOM() {
			public State start(String name, Attributes attrs) {
				if (name == "title") {
					buffers.push(new StringBuffer());
					return TITLE;
				}
				else if (name == "exit") {
					String names = attrs.getValue("names");
					String to = attrs.getValue("to");
					exit = new Exit(names, to);
					return EXIT;
				}
				else if (name == "describe") {
					description = new StringBuffer();
					descriptionNames = attrs.getValue("names");
					return ROOM_DESCRIBE;
				}

				return ROOM;
			}

			public State end(String name) {
				// TODO Check for duplicate ids
				area.addRoom(room);
				return AREA;
			}
		},

		ROOM_DESCRIBE() {
			public State start(String name, Attributes attrs) {
				if (name == "exit") {
					String color = Config.get("world.colors.room.exit");
					description.append((color == null) ? "" : color);
					return ROOM_DESCRIBE_FEATURE;
				}
				else if (name == "look") {
					String color = Config.get("world.colors.room.look");
					description.append((color == null) ? "" : color);
					return ROOM_DESCRIBE_FEATURE;
				}

				return ROOM_DESCRIBE;
			}

			public void characters(String str) {
				description.append(str);
			}

			public State end(String name) {
				String descriptionStr = description.toString().trim().replaceAll("\\s([,.;:])", "$1");

				if (descriptionNames == null)
					room.setDescription(descriptionStr);
				else
					room.addFeature(descriptionNames, descriptionStr);

				return ROOM;
			}
		},

		ROOM_DESCRIBE_FEATURE() {
			public void characters(String str) {
				description.append(str);
			}

			public State end(String name) {
				description.append("{x");
				return ROOM_DESCRIBE;
			}
		},

		ITEM() {
			public State start(String name, Attributes attrs) {
				if (name == "property") {
					propertyKey = attrs.getValue("key");
					buffers.push(new StringBuffer());
					return PROPERTY;
				}
				return ITEM;
			}

			public State end(String name) {
				area.addItem(item);
				return AREA;
			}
		},

		EXIT() {
			public State end(String name) {
				// String desc = room.getDescription();
				// String endColor = Config.get("world.colors.room.exit") == null ? "" : "{x";
				// room.setDescription(desc.trim() + endColor);
				room.addExit(exit);
				return ROOM;
			}

			public void characters(String str) {
				exit.addToDescription(str);
			}
		},

		TITLE() {
			public State end(String name) {
				StringBuffer buffer = buffers.pop();
				room.setTitle(buffer.toString().trim());
				return ROOM;
			}

			public void characters(String str) {
				buffers.peek().append(str);
			}
		},

		PROPERTY() {
			public State end(String name) {
				StringBuffer buffer = buffers.pop();
				item.set(propertyKey, buffer.toString().trim());
				return ITEM;
			}

			public void characters(String str) {
				buffers.peek().append(str);
			}
		};

		State() {
		}

		public State start(String name, Attributes attrs) {
			return this;
		}

		public State end(String name) {
			return this;
		}

		public void characters(String str) {
		}
	}

	// Instance variables
	State state = State.INIT;

	/**
	 * @return The area as a result of the parse, or <code>null</code> if no area could be
	 *   or has yet been parsed.
	 */
	public Object getResult() {
		return AreaHandler.area;
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void startElement(String uri, String localName, String name, Attributes attrs) {
		state = state.start(name, attrs);
	}

	/**
	 * @see org.xml.sax.helpers.DefaultHandler
	 */
	public void endElement(String uri, String localName, String name) {
		state = state.end(name);
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

		state.characters(str);
	}
}