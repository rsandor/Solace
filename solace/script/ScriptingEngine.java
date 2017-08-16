package solace.script;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.nio.file.*;

import solace.io.GameFiles;
import solace.util.Log;

/**
 * The main scripting engine for Solace. Allows for the creation of JavaScript
 * scripts that can modify the game in a plethora of ways. This includes, but
 * is not limited to: mobile behaviors, cooldown actions, custom commands,
 * buffs, etc.
 * @author Ryan Sandor Richards
 */
public class ScriptingEngine {
  private ScriptEngine engine;

  /**
   * Creates a new solace scripting engine.
   */
  private ScriptingEngine() {
    ScriptEngineManager engineManager = new ScriptEngineManager();
    engine = engineManager.getEngineByName("nashorn");
  }

  /**
   * Reloads all game scripts.
   */
  public static void reload() {
    Log.info("Reloading game scripts");
    try {
      ScriptedPassives.clear();
      ScriptedCommands.clear();
      GameFiles.findEngineScripts().forEach(instance::runEngineScript);
      GameFiles.findScripts().forEach(instance::run);
    } catch (Throwable e) {
      Log.error("An error occurred when reloading game scripts.");
      e.printStackTrace();
    }
  }

  /**
   * Safe script evaluation that prevents global scope pollution.
   * @param script The script to evaluate.
   */
  @SuppressWarnings("WeakerAccess")
  public void eval(String script) {
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
   * Runs the javascript file with the given path on the scripting engine.
   * @param path Path of the javascript file to run.
   */
  public void run(Path path) {
    try {
      String script = new String(Files.readAllBytes(path));
      eval(script);
    } catch (Throwable t) {
      Log.error(String.format("Error running game script '%s': %s",
        String.valueOf(path), t.getMessage()));
    }
  }

  /**
   * Runs the the javascript file at the given path in the global scope as an
   * engine initialization script.
   * @param path Path of the javascript file to run.
   */
  private void runEngineScript(Path path) {
    try {
      String script = new String(Files.readAllBytes(path));
      engine.eval(script);
    } catch (Throwable t) {
      Log.error(String.format("Error running engine initialization script '%s': %s",
        String.valueOf(path), t.getMessage()));
    }
  }

  /**
   * Scripting engine singleton instance.
   */
  private static final ScriptingEngine instance = new ScriptingEngine();

  /**
   * @return The singelton instance of the scripting engine.
   */
  public static ScriptingEngine getInstance() {
    return instance;
  }
}
