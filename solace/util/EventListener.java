package solace.util;

import java.util.*;

/**
 * Interface for event listeners. This interface is primarily used by the
 * <code>solace.util.EventEmitter</code> class.
 * @author Ryan Sandor Richards
 */
public interface EventListener {
  public void run(Object[] args);
}
