package solace.cmd.play;

import solace.net.*;
import solace.util.*;
import solace.game.*;
import solace.cmd.*;

/**
 * Toggles the tick indication (shows ticks to admins).
 */
public class Tick extends AdminCommand {
  boolean show = false;
  Clock.Event tickEvent;
  Clock clock = Clock.getInstance();

  public Tick() {
    super("ticks");
  }

  public boolean run(Connection c, String []params) {
    show = !show;
    if (show) {
      tickEvent = clock.interval(1, new Runnable() {
        public void run() {
          c.wrapln("-- TICK --");
        }
      });
    }
    else {
      tickEvent.cancel();
      tickEvent = null;
    }
    return true;
  }
}
