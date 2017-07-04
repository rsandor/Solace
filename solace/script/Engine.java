package solace.script;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import solace.util.Log;

/**
 * The main scripting engine for Solace. Allows for the creation of JavaScript
 * scripts that can modify the game in a plethora of ways. This includes, but
 * is not limited to: mobile behaviors, cooldown actions, custom commands,
 * buffs, etc.
 * @author Ryan Sandor Richards
 */
public class Engine {
  private ScriptEngineManager engineManager;
  private ScriptEngine engine;

  /**
   * Path to the engine globals initialization script.
   */
  protected static final String GLOBALS_JS = "script/engine/globals.js";

  /**
   * Singleton instance of the scripting engine.
   */
  private static final Engine instance = new Engine();

  /**
   * Creates a new solace scripting engine.
   */
  Engine() {
    engineManager = new ScriptEngineManager();
    engine = engineManager.getEngineByName("nashorn");
  }

  /**
   * @return The singelton instance of the scripting engine.
   */
  public static Engine getInstance() {
    return instance;
  }

  /**
   * Starts the scripting engine.
   * @throws IOException If a required startup script fails to load.
   * @throws ScriptException If a requires startup script fails to evaluate.
   */
  public static void start() throws IOException, ScriptException {
    Log.info("Starting scripting engine...");
    instance.run(GLOBALS_JS);
  }

  /**
   * Runs the javascript file with the given path on the scripting engine.
   * @param path Path of the javascript file to run.
   * @throws IOException If an io error occurs when reading the file.
   * @throws ScriptException If an error occurs executing the script.
   */
  protected void run(String path) throws IOException, ScriptException {
    String script = new String(Files.readAllBytes(Paths.get(path)));
    engine.eval(script);
  }
}
