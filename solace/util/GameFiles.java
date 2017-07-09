package solace.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Utility for fetching files from the "game/" directory.
 * @author Ryan Sandor Richards
 */
public class GameFiles {
  private static final Path gamePath = Paths.get("game/");
  private static final Path engineScriptsPath = Paths.get("scripts/");

  private static final String configExt = ".config.xml";
  private static final String emoteExt = ".emote.json";
  private static final String scriptExt = ".js";
  private static final String skillExt = ".skill.json";

  /**
   * Recursively finds all files with the given file extension that exist in
   * the game directory.
   * @param ext File extensions by which to filter.
   * @return A stream of paths for all files found.
   */
  public static Stream<Path> find(String ext) throws IOException {
    return find(gamePath, ext);
  }

  /**
   * Finds all files with the given extension in the given path.
   * @param path Path over which to search.
   * @param ext Extension of the files to find.
   * @return A stream of paths for all files found.
   */
  private static Stream<Path> find(Path path, String ext) throws IOException {
    return Files.find(
      path,
      Integer.MAX_VALUE,
      (p, attr) -> attr.isRegularFile() && String.valueOf(p).endsWith(ext)
    );
  }

  /**
   * @return A stream of paths to all configuration files in the game directory.
   * @throws IOException If an error occurs while finding configuration files.
   */
  static Stream<Path> findConfigurations() throws IOException {
    return find(configExt);
  }

  /**
   * @return A stream of paths to all emote files in the game directory.
   * @throws IOException If an error occurs while finding emote files.
   */
  static Stream<Path> findEmotes() throws IOException {
    return find(emoteExt);
  }

  /**
   * @return A stream of paths to all scripts in the game directory.
   * @throws IOException If an error occurs while finding script files.
   */
  public static Stream<Path> findScripts() throws IOException {
    return find(scriptExt);
  }

  /**
   * @return A stream of paths to all engine script files.
   * @throws IOException If an error occurs while finding engine scripts.
   */
  public static Stream<Path> findEngineScripts() throws IOException {
    return find(engineScriptsPath, scriptExt);
  }

  /**
   * @return A stream of paths to all skills in the game directory.
   * @throws IOException If an error occurs while finding skill files.
   */
  static Stream<Path> findSkills() throws IOException {
    return find(skillExt);
  }
}
