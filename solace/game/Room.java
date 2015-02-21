package solace.game;

import java.util.*;

/**
 * Basic room class for the engine.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Room
{
    String id;
    String title = "";
    String desc = "";
    Area area = null;
    LinkedList<Exit> exits = new LinkedList<Exit>();

    List<solace.game.Character> characters;


    /**
     * Creates a new room with the given id, title, and description.
     * @param i Id for the room.
     */
    public Room(String i) {
        id = i;
        characters = Collections.synchronizedList(new LinkedList<solace.game.Character>());
    }

    /**
     * Sends a message to all of the characters in a room.
     * @param message Message to send.
     */
    public void sendMessage(String message) {
        synchronized(characters) {
            for (solace.game.Character ch : characters)
                ch.sendMessage(message);
        }
    }

    /**
     * Sends a message to all of the characters in a room. Excluding
     * the given player character (useful for messages sent as a result
     * of a character's actions).
     * @param message Message to send.
     * @param exclude Player to exclude when sending the message.
     */
    public void sendMessage(String message, solace.game.Character exclude) {
        synchronized(characters) {
            for (solace.game.Character ch : characters) {
                if (ch == exclude)
                    continue;
                ch.sendMessage(message);
            }
        }
    }

    /**
     * Returns the list of users in the room.
     */
    public List<solace.game.Character> getCharacters() {
        return characters;
    }

    /**
     * @param exclude Character to exclude.
     * @return a list of characters excluding the given character.
     */
    public List<solace.game.Character> getOtherCharacters(solace.game.Character exclude) {
        List<solace.game.Character> others = new LinkedList<solace.game.Character>();
        synchronized (characters) {
            for (solace.game.Character ch : characters) {
                if (ch == exclude)
                    continue;
                others.add(ch);
            }
        }
        return others;
    }

    /**
     * @return the area the room belongs to.
     */
    public Area getArea() {
        return area;
    }

    /**
     * @param a area for the room.
     */
    public void setArea(Area a) {
        area = a;
    }

    /**
     * @return The room's id.
     */
    public String getId() {
        return id;
    }

    /**
     * Adds an exit for the room.
     * @param e Exit to add to the room.
     */
    public void addExit(Exit e) {
        exits.add(e);
    }

    /**
     * Performs a prefix search that looks for an exit based off a direction fragment.
     * @param fragment Fragment by which to perform the search.
     * @return The exit if an exit matching the fragment was found, <code>null</code> otherwise.
     */
    public Exit findExit(String fragment) {
        for (Exit e : exits)
            if (e.matches(fragment))
                return e;

        return null;
    }

    /**
     * @return the desc
     */
    public String getDescription() {
        return desc.trim();
    }

    /**
     * @param desc the desc to set
     */
    public void setDescription(String d) {
        desc = d;
    }

    /**
     * Appends more information to the description.
     * @param d Information to append.
     */
    public void addToDescription(String d) {
        desc += d;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
