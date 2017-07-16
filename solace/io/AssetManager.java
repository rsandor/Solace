package solace.io;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * Describes common behaviors for all game asset managers.
 * @author Ryan Sandor Richards
 */
public interface AssetManager<T> {
  /**
   * Reloads all assets under the auspices of the manager.
   */
  void reload();

  /**
   * @return The number of assets managed.
   */
  int size();

  /**
   * Determines if an asset with the given name is under management.
   * @param name Name for the asset to find.
   * @return `true` if the manager has an asset with the given name, `false` otherwise.
   */
  boolean has(String name);

  /**
   * Gets an asset with the given name.
   * @param name Name of the asset to get.
   * @return The asset with the given name.
   * @throws AssetNotFoundException If an asset with the given name could not be found.
   */
  T get(String name) throws AssetNotFoundException;

  /**
   * Performs an action over all assets under management.
   * @param action Action to perform on each asset.
   */
  void forEach(Consumer<T> action);

  /**
   * @return A collection of all assets under management.
   */
  Collection<T> getAll();
}
