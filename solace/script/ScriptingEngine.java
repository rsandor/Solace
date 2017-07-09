package solace.script;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.file.*;
import java.io.*;
import solace.util.Log;

/**
 * The main scripting engine for Solace. Allows for the creation of JavaScript
 * scripts that can modify the game in a plethora of ways. This includes, but
 * is not limited to: mobile behaviors, cooldown actions, custom commands,
 * buffs, etc.
 * @author Ryan Sandor Richards
 */
public class ScriptingEngine {
  private ScriptEngineManager engineManager;
  private ScriptEngine engine;

  /**
   * Path to the engine initialization scripts.
   */
  protected static final String ENGINE_JS = "script/engine/";

  /**
   * Path to the scripts directory in the repository.
   */
  protected static final String SCRIPTS_DIR = "script/";

  /**
   * Singleton instance of the scripting engine.
   */
  private static final ScriptingEngine instance = new ScriptingEngine();

  /**
   * Creates a new solace scripting engine.
   */
  ScriptingEngine() {
    engineManager = new ScriptEngineManager();
    engine = engineManager.getEngineByName("nashorn");
  }

  /**
   * @return The singelton instance of the scripting engine.
   */
  public static ScriptingEngine getInstance() {
    return instance;
  }

  /**
   * Starts the scripting engine.
   * @throws IOException If a required startup script fails to load.
   * @throws ScriptException If a requires startup script fails to evaluate.
   */
  public static void start() {
    Log.info("Starting scripting engine");
    reload();
  }

  /**
   * Reloads all game scripts.
   */
  public static void reload() {
    Log.info("Reloading game scripts");
    try {
      ScriptedCommands.clear();
      instance.runAll(Paths.get(ENGINE_JS), null, true);
      instance.runAll(Paths.get(SCRIPTS_DIR), ENGINE_JS ,false);
    } catch (Throwable e) {
      Log.error("An error ocurred when reloading game scripts.");
      e.printStackTrace();
    }
  }

  /**
   * Runs all scripts under the given path excluding those that start with the
   * given prefix.
   * @param dir Directory path of the scripts to run.
   * @param exclude Optional prefix to use when excluding files.
   * @param allowGlobals Whether or not to allow scripts to modify the global
   *   scope.
   * @throws IOException If an io error occurs when reading scripts.
   * @throws ScriptException If an error occurrs when evaluating scripts.
   */
  protected void runAll(Path dir, String exclude, boolean allowGlobals)
    throws IOException
  {
    Files.find(
      dir,
      Integer.MAX_VALUE,
      (path, attr) -> attr.isRegularFile()
    ).forEach((path) -> {
      try {
        if (exclude != null && path.toString().startsWith(exclude)) {
          return;
        }
        Log.debug("Running " + path.toString());
        if (allowGlobals) {
          instance.runGlobal(path);
        } else {
          instance.run(path);
        }
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
    String scriptFormat = "'use strict';\n(function(){\n%s\n})();";
    String original = script.replaceAll("[\"']use\\s+strict[\"'][;]?", "");
    String safeScript = String.format(scriptFormat, original);

    try {
      engine.eval(safeScript);
    } catch (ScriptException e) {
      int line = e.getLineNumber();
      String[] lines = safeScript.split("\n");
      String format = "%s\n%s";
      Log.error(String.format(format, e.getMessage(), lines[line]));
    }
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
  protected void runGlobal(Path path) throws IOException, ScriptException {
    engine.eval(loadScript(path));
  }
}
