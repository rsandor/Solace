package solace.cmd.deprecated.play;

import solace.net.*;
import solace.util.*;
import solace.cmd.*;

/**
 * Toggles the tick indication (shows ticks to admins).
 */
public class Tick extends AdminStateCommand {
  boolean show = false;
  Clock.Event tickEvent;
  Clock clock = Clock.getInstance();

  public Tick() {
    super("ticks");
  }

  public void run(Connection c, String []params) {
    show = !show;
    if (show) {
      tickEvent = clock.interval("admin.tick", 1, new Runnable() {
        public void run() {
          c.wrapln("-- TICK --");
        }
      });
    }
    else {
      tickEvent.cancel();
      tickEvent = null;
    }
  }
}
