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
  private static final String configExt = ".config.xml";
  private static final String emoteExt = ".emote.json";

  /**
   * Recursively finds all files with the given file extension that exist in
   * the game directory.
   * @param ext File extensions by which to filter.
   * @return A stream of paths for all files found.
   */
  public static Stream<Path> find(String ext) throws IOException {
    return Files.find(
      gamePath,
      Integer.MAX_VALUE,
      (path, attr) -> attr.isRegularFile() && String.valueOf(path).endsWith(ext)
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
}
