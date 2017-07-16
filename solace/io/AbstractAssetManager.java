package solace.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Hashtable;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Abstract base class for common functionality shared by all asset managers.
 * @author Ryan Sandor Richards
 */
public abstract class AbstractAssetManager<T> implements AssetManager<T> {
  private final Hashtable<String, T> assets = new Hashtable<>();
  private String assetType = "";
  private String extension = "";

  /**
   * Creates a new abstract asset manager with the given asset type name and common file extension.
   * @param type The asset type name for the manager.
   */
  AbstractAssetManager(String type, String ext) {
    assetType = type;
    extension = ext;
  }

  /**
   * Adds an asset to the manager.
   * @param name Name of the asset.
   * @param asset Asset to add.
   */
  protected void add(String name, T asset) { assets.put(name, asset); }

  /**
   * Clears all assets from the manager.
   */
  protected void clear() { assets.clear(); }


  /**
   * Helper function that loads all game files with the file extension for the asset manager.
   * @return A stream of paths to each file.
   * @throws IOException If an error occurs finding the paths.
   */
  protected Stream<Path> load() throws IOException {
    return GameFiles.find(extension);
  }

  @Override
  public abstract void reload();

  @Override
  public int size() { return assets.values().size(); }

  @Override
  public boolean has(String name) { return assets.keySet().contains(name); }

  @Override
  public T get(String name) throws AssetNotFoundException {
    if (!has(name)) {
      throw new AssetNotFoundException(assetType, name);
    }
    return assets.get(name);
  }

  @Override
  public void forEach(Consumer<T> action) { assets.values().forEach(action); }

  @Override
  public Collection<T> getAll() { return assets.values(); }
}
