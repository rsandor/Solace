package solace.io;

/**
 * Thrown when an asset with the given name and type could not be found.
 */
public class AssetNotFoundException extends Exception {
  public AssetNotFoundException(String type, String name) {
    super(String.format("Asset of type '%s' with name '%s' could not be found.", type, name));
  }
}
