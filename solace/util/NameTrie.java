package solace.util;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import java.util.*;

/**
 * Trie data structure used to search for items by name.
 * @author Ryan Sandor Richards
 */
public class NameTrie<T> {
  private Trie<String, T> trie = new PatriciaTrie<>(new LinkedHashMap<String, T>());;
  private T defaultValue;
  private Comparator<T> comparator = (a, b) -> -1;

  /**
   * Creates a new name trie with the given default value.
   * @param def Default value for the name trie.
   */
  public NameTrie(T def) {
    defaultValue = def;
  }

  /**
   * Creates a new name trie with the given default value and post match comparator.
   * @param def Default value for the name trie.
   * @param cmp Post match comparator used to sort find results.
   */
  public NameTrie(T def, Comparator<T> cmp) {
    defaultValue = def;
    comparator = cmp;
  }

  /**
   * Clears al keys and values from the trie.
   */
  public void clear() {
    trie.clear();
  }

  /**
   * Puts an item into the trie.
   * @param name Name of the item.
   * @param item Item to add.
   */
  public void put(String name, T item) {
    trie.put(name, item);
  }

  /**
   * Determines if an item with the given name has been added to this trie.
   * @param name Name for the item.
   * @return True if an item with the given name exists, false otherwise.
   */
  public boolean containsName(String name) {
    return trie.keySet().contains(name);
  }

  /**
   * Finds the highest ranked item matching the given prefix. Ranking of items is done
   * via the NameTrie's comparator.
   * @param prefix Name prefix to search for.
   * @return The best match for the given prefix or the default value if no items match.
   */
  public T find(String prefix) {
    T bestMatch = trie.prefixMap(prefix).values().stream()
      .reduce(null, (best, item) -> (best == null || comparator.compare(best, item) > 0) ? item : best);
    return bestMatch == null ? defaultValue : bestMatch;
  }
}
