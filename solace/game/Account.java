package solace.game;

import solace.xml.GameParser;
import solace.util.Digest;
import java.io.*;
import java.util.*;
import java.security.MessageDigest;

import solace.game.Character;

/**
 * Holds information pretaining to a game account.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Account
{
  // Instance Variables
  String name;
  String password;
  boolean admin;
  Hashtable <String, solace.game.Character> charactersByName;
  List<solace.game.Character> characters;
  solace.game.Character activeCharacter = null;

  /*
   * Account file location constants.
   */
  protected static final String accountDir = "data/accounts/";

  /**
   * @param name Name of the account.
   * @return The path to XML file associated with the account with the given
   *   name.
   */
  protected static String accountPath(String name) {
    return accountDir + name.toLowerCase() + ".xml";
  }

  /**
   * Loads an account from file on the disk and returns it.
   * @param name Name of the account, note names are case insensitive.
   * @return The fully instantiated account.
   */
  public static Account load(String name)
    throws IOException, FileNotFoundException
  {
    return GameParser.parseAccount(accountPath(name));
  }

  /**
   * Determines if an account with the given name exists.
   * @param name Name of the account.
   * @return True if an account with the given name exists, false otherwise.
   */
  public static boolean accountExists(String name) {
    return new File(accountPath(name)).exists();
  }

  /**
   * Creates a new account.
   * @param name Name for the account.
   * @param password Password for the account.
   * @param admin Whether or not the user is an administrator.
   * @return The newly created account.
   * @throws IllegalArgumentException If an account with the given name already
   *   exists.
   * @throws IOException If an i/o error occured while attempting to save the
   *   account.
   */
  public static Account createAccount(
    String name,
    String password,
    boolean admin
  )
    throws IllegalArgumentException, IOException
  {
    // Check to see if ana account with the given name already exists
    File accountFile = new File(accountDir + name.toLowerCase());
    if (accountFile.exists())
      throw new IllegalArgumentException("Account with given name already exists.");

    Account account = new Account(name, Digest.sha256(password), admin);
    account.save();

    return account;
  }

  /**
   * Creates a new <code>Account</code> instance.
   * @param n Name of the account.
   * @param p Password for the account.
   * @param a Whether or not the account is administrative.
   */
  public Account(String n, String p, boolean a) {
    name = n;
    password = p;
    admin = a;
    charactersByName = new Hashtable<String, solace.game.Character>();
    characters = new LinkedList<solace.game.Character>();
  }

  /**
   * Sets the active character for the account.
   * @param ch New active character for the account.
   */
  public void setActiveCharacter(solace.game.Character ch) {
    activeCharacter = ch;
  }

  /**
   * Clears the current active character for the account.
   */
  public void clearActiveCharacter() {
    activeCharacter = null;
  }

  /**
   * @return <code>true</code> if the account has an actively playing character,
   *   <code>false</code> otherwise.
   */
  public boolean hasActiveCharacter() {
    return (activeCharacter != null);
  }

  /**
   * @return The active character for the account, or <code>null</code> if no
   * such character is currently being played.
   */
  public solace.game.Character getActiveCharacter() {
    return activeCharacter;
  }

  /**
   * Associates a character with the account.
   * @param c Character to associate.
   */
  public void addCharacter(solace.game.Character c) {
    charactersByName.put(c.getName(), c);
    characters.add(c);
    c.setAccount(this);
  }

  /**
   * @return The account's characters.
   */
  public List<solace.game.Character> getCharacters() {
    return characters;
  }

  public solace.game.Character getFirstCharacter() {
    return characters.get(0);
  }

  /**
   * Determines if this account has a character with the given name.
   * @param name Name of the character.
   * @param <code>true</code> if a character with the name exists on the
   *   account, <code>false</code> otherwise.
   */
  public boolean hasCharacter(String name) {
    for (solace.game.Character ch : characters)
      if (ch.getName().toLowerCase().startsWith(name.toLowerCase()))
        return true;
    return false;
  }

  /**
   * Gets a character with the given name.
   * @param name Name of the character.
   * @return The character with the given name, or null if no such character was
   *  found.
   */
  public solace.game.Character getCharacter(String name) {
    for (solace.game.Character ch : characters)
      if (ch.getName().toLowerCase().startsWith(name.toLowerCase()))
        return ch;
    return null;
  }

  /**
   * Saves the account's details to disk.
   * @throws IOException if the file was unable to be written to disk.
   */
  public void save()
    throws IOException
  {
    File file = new File(Account.accountPath(name));
    PrintWriter out = new PrintWriter(new FileWriter(file));
    out.print(getXML());
    out.close();
  }

  /**
   * @return XML representing the account.
   */
  public String getXML() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

    xml += "<user name=\"" + name +
      "\" admin=\"" + admin +
      "\" password=\"" + password + "\">\n";

    // Characters
    xml += "<characters>\n";
    for (Character c : characters)
      xml += c.getXML();
    xml += "</characters>\n";

    xml += "</user>";

    return xml;
  }

  /**
   * @return <code>true</code> if the user is an admin, <code>false</code>
   *   otherwise.
   */
  public boolean isAdmin() {
    return admin;
  }

  /**
   * @param a Whether or not to set the user as an admin.
   */
  public void setAdmin(boolean a) {
    admin = a;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * @param password the password to set
   */
  public void setPassword(String password) {
    this.password = password;
  }
}
