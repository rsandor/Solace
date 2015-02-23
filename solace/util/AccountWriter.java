package solace.util;

import java.net.*;
import java.util.*;
import java.io.*;
import solace.util.*;
import solace.game.*;
import solace.cmd.*;

/**
 * Resonisible for saving the state of characters / accounts in
 * the game periodically.
 *
 * @author Ryan Sandor Richards
 */
public class AccountWriter implements Runnable {
    List<solace.game.Character> saveQueue;
    boolean running = true;

    public AccountWriter() {
        saveQueue = Collections.synchronizedList(new LinkedList<solace.game.Character>());
    }

    /**
     * Places a specific character into the save queue.
     * @param ch Charactet to save.
     */
    public void save(solace.game.Character ch) {
        saveQueue.add(ch);
    }

    /**
     * Stops the writer and ensures each of the queued players and
     * all active players are saved.
     */
    public void stop() {
        running = false;
        saveActive();
        saveQueued();
    }

    /**
     * Saves all currently active player characters.
     */
    protected void saveActive() {
        Collection<solace.game.Character> active = World.getActiveCharacters();
        synchronized(active) {
            for (solace.game.Character ch : active) {
                try {
                    Account act = ch.getAccount();
                    act.save();
                }
                catch (IOException ioe) {
                    Log.error(
                        "Error while saving character '" +
                        ch.getName() + "': " + ioe.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Saves all characters who have been put into the save queue.
     */
    protected void saveQueued() {
        synchronized(saveQueue) {
            while (saveQueue.size() > 0) {
                solace.game.Character ch = saveQueue.remove(0);
                try {
                    Account act = ch.getAccount();
                    act.save();
                }
                catch (IOException ioe) {
                    Log.error(
                        "Error while saving character '" +
                        ch.getName() + "': " + ioe.getMessage()
                    );
                }
            }
        }
    }

    /**
     * Automatically saves all characters every 1000ms.
     */
    public void run() {
        while (running) {
            try {
                saveActive();
                saveQueued();
                Thread.sleep(1000);
            }
            catch (InterruptedException ie) {
                Log.error("AccountWriter interrupted: " + ie.getMessage());
            }
        }
    }
}
