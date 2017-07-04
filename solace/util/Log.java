package solace.util;
import java.util.Date;


/**
 * Basic log class for logging events and errors in the system.
 * @author Ryan Sandor Richards
 */
public class Log {
  static String level = System.getenv("LOG_LEVEL");

  /**
   * @return The numeric value of the environment log level.
   */
  static int getLevel() {
    if (level == null) {
      level = "info";
    }
    return getLevel(level);
  }

  /**
   * @param l Level to to convert.
   * @return The numeric value for the log level.
   */
  static int getLevel(String l) {
    if (l.equals("trace")) {
      return 10;
    }
    if (l.equals("debug")) {
      return 20;
    }
    if (l.equals("info")) {
      return 40;
    }
    if (l.equals("warn")) {
      return 50;
    }
    if (l.equals("error")) {
      return 60;
    }
    if (l.equals("fatal")) {
      return 70;
    }
    return 0;
  }

  /**
   * @return true if logs of this level should be shown, false otherwise.
   */
  static boolean show(String l) {
    return getLevel() <= getLevel(l);
  }

  /**
   * Fatal level error logging.
   * @param s Message.
   */
  public static void fatal(String s) {
    Date d = new Date();
    System.out.println(Color.format("{R[FATAL]{x ("+d+"): " + s));
  }

  /**
   * Basic error logging function.
   * @param s Message.
   */
  public static void error(String s) {
    if (!show("error")) { return; }
    Date d = new Date();
    System.out.println(Color.format("{R[ERROR  ]{x ("+d+"): " + s));
  }

  /**
   * Warning logger.
   * @param s Message.
   */
  public static void warn(String s) {
    if (!show("warn")) { return; }
    Date d = new Date();
    System.out.println(Color.format("{y[WARNING]{x ("+d+"): " + s));
  }

  /**
   * Basic information logging function.
   * @param s Message.
   */
  public static void info(String s) {
    if (!show("info")) { return; }
    Date d = new Date();
    System.out.println("[INFO   ] ("+d+"): "+s);
  }

  /**
   * Debug level logging.
   * @param s Message.
   */
  public static void debug(String s) {
    if (!show("debug")) { return; }
    Date d = new Date();
    System.out.println(Color.format("{g[DEBUG  ]{x ("+d+"): "+s));
  }

  /**
   * Trace level log information.
   * @param s Message.
   */
  public static void trace(String s) {
    if (!show("trace")) { return; }
    Date d = new Date();
    System.out.println("[TRACE  ] ("+d+"): "+s);
  }
}
