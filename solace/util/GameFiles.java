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

  /**
   * Recursively finds all files with the given file extension that exist in
   * the game directory.
   * @param ext File extensions by which to filter.
   * @return A stream of paths for all files found.
   */
  public static final Stream<Path> find(String ext) throws IOException {
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
  public static final Stream<Path> findConfigurations() throws IOException {
    return find(configExt);
  }
}
