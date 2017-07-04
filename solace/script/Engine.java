package solace.script;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
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
   * Path to the scripts directory in the repository.
   */
  protected static final String SCRIPTS_DIR = "script/";

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

    // Initialize the global engine scope
    instance.runGlobal(GLOBALS_JS);

    // Load all scripts defined in the scripts directory
    Files.find(
      Paths.get(SCRIPTS_DIR),
      Integer.MAX_VALUE,
      (path, attr) -> attr.isRegularFile()
    ).forEach((path) -> {
      try {
        if (path.toString().startsWith("script/engine")) {
          return;
        }
        Log.debug("Running " + path.toString());
        instance.run(path);
      } catch (Throwable e) {
        Log.debug("Failed to load script");
        e.printStackTrace();
      }
    });
  }

  /**
   * Safe script evaluation that prevents global scope pollution.
   * @param script The script to evaluate.
   * @throws ScriptException If an error occurs executing the script.
   */
  public void eval(String script) throws ScriptException {
    engine.eval(
      "'use strict';\n" +
      "(function() {\n" +
      script.replaceAll("[\"']use\\s+strict[\"'][;]?", "") +
      "})();\n");
  }

  /**
   * Loads the string contents for the script with the given path.
   * @param path Path of the script to load.
   * @return The string contents of the script.
   * @throws IOException If an io error occurs when reading the file.
   * @throws ScriptException If an error occurs executing the script.
   */
  private String loadScript(String path)
    throws IOException, ScriptException
  {
    return loadScript(Paths.get(path));
  }

  /**
   * Loads the string contents for the script with the given path.
   * @param path Path of the script to load.
   * @return The string contents of the script.
   * @throws IOException If an io error occurs when reading the file.
   * @throws ScriptException If an error occurs executing the script.
   */
  private String loadScript(Path path)
    throws IOException, ScriptException
  {
    return new String(Files.readAllBytes(path));
  }

  /**
   * Runs the javascript file with the given path on the scripting engine.
   * @param path Path of the javascript file to run.
   * @throws IOException If an io error occurs when reading the file.
   * @throws ScriptException If an error occurs executing the script.
   */
  public void run(String path) throws IOException, ScriptException {
    eval(loadScript(path));
  }

  /**
   * Runs the javascript file with the given path on the scripting engine.
   * @param path Path of the javascript file to run.
   * @throws IOException If an io error occurs when reading the file.
   * @throws ScriptException If an error occurs executing the script.
   */
  public void run(Path path) throws IOException, ScriptException {
    eval(loadScript(path));
  }

  /**
   * Runs the javascript file with the given path on the scripting engine in
   * the global scope.
   * @param path Path of the javascript file to run.
   * @throws IOException If an io error occurs when reading the file.
   * @throws ScriptException If an error occurs executing the script.
   */
  protected void runGlobal(String path) throws IOException, ScriptException {
    engine.eval(loadScript(path));
  }
}
