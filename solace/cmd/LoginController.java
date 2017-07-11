package solace.cmd;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.*;

import solace.io.Messages;
import solace.util.*;
import solace.game.*;
import solace.net.*;

public class LoginController
  implements Controller
{
  /*
   * Login State Constants
   */
  public static final int ACCOUNT_NAME = 1;
  public static final int ACCOUNT_PASS = 2;
  public static final int NEW_ACCOUNT_NAME = 3;
  public static final int NEW_ACCOUNT_PASS = 4;
  public static final int NEW_ACCOUNT_CONFIRM = 5;

  // Instance Variables
  int state = ACCOUNT_NAME;
  Connection connection;
  String newUserName = "";
  String newUserPass = "";

  public LoginController(Connection c) {
    init(c);
  }

  public void init(Connection c) {
    connection = c;
    connection.sendln( Messages.get("Intro") );
  }

  public String getPrompt() {
    switch (state) {
      case ACCOUNT_NAME: return "Account: ";
      case ACCOUNT_PASS: return "Password: ";
      case NEW_ACCOUNT_NAME: return "Name for account: ";
      case NEW_ACCOUNT_PASS: return "Password for account: ";
      case NEW_ACCOUNT_CONFIRM: return "Confirm password: ";
    }
    Log.error("Login controller in unknown state: " + state);
    return "Uknown state: ";
  }

  /**
   * Login handler parses input for account name and attempts
   * to load account associated with that name.
   * @param input Input from the user.
   */
  protected void accountName(String input) {
    String aname = input.trim();

    // Check to see if they want to make a new account
    if (input.toLowerCase().equals("new")) {
      connection.sendln( Messages.get("NewAccountRules") );
      state = NEW_ACCOUNT_NAME;
      return;
    }

    // Ensure they are not attempting to login twice
    if (World.isLoggedIn(aname)) {
      connection.sendln("{r}Account already logged in!{x}");
      Log.info(
        "Double login attempt for account '" + aname + "' from " +
        connection.getInetAddress()
      );
      connection.close();
      return;
    }

    // Try to load the account.
    try {
      connection.setAccount( Account.load(aname) );
      state = ACCOUNT_PASS;
      connection.echoOff();
    }
    catch (IOException ioe) {
      connection.sendln(
        "Account not found, " +
        "enter '{y}new{x}' to create a new account!"
      );
    }
  }

  /**
   * Login handler parses input for account password and attempts
   * to verify that the user has entered the correct password.
   * @param input Input from user.
   */
  protected void accountPassword(String input) {
    String pass = input;

    // Ensure that an account has been loaded
    if (!connection.hasAccount()) {
      state = ACCOUNT_NAME;
      return;
    }

    Account account = connection.getAccount();

    // Check for an incorrect password
    if (!Digest.sha256(pass).equals(account.getPassword())) {
      Log.info(
        "Incorrect password given for user '" + account.getName() +
        "' from " + connection.getInetAddress()
      );

      connection.sendln("\n\rIncorrect password.");
      Collection connections = Collections.synchronizedCollection(
        World.getConnections()
      );
      synchronized (connections) {
        connections.remove(connection);
      }
      connection.close();

      return;
    }

    // Everything seems fine, log them in and present the game's main menu
    World.addAccount(connection, account);
    connection.setAccount(account);
    Log.info(
      "Account '" + account.getName() +
      "' logged into from " + connection.getInetAddress()
    );
    connection.sendln("\n\rWelcome " + connection.getAccount().getName() + "!");
    connection.setStateController( new MainMenuController(connection) );
    connection.echoOn();
  }

  /**
   * For the creation of new accounts, this processes an account name input.
   * @param input Input given by user.
   */
  protected void newAccountName(String input) {
    // Check the validity of the name
    if (!Pattern.matches("\\w+\\z", input)) {
      connection.sendln("Invalid name, please use letters and numbers only!");
      return;
    }

    // Check to see if the name already exists
    if (Account.accountExists(input.toLowerCase())) {
      connection.sendln(
        "An account with the given name already exists, " +
        "please choose another."
      );
      return;
    }

    // If all is well move on to the next step
    newUserName = input.toLowerCase();
    state = NEW_ACCOUNT_PASS;
    connection.echoOff();
  }

  /**
   * For the creation of new accounts, this processes account password input.
   * @param input Password for the account.
   */
  protected void newAccountPassword(String input) {
    // Ensure the password is long enough
    if (input.length() < 6) {
      connection.sendln("\n\rPasswords must be at least 6 letters in length!");
      return;
    }
    newUserPass = input;
    state = NEW_ACCOUNT_CONFIRM;
  }

  /**
   * For the creation of new accounts, this processes the password confirmation.
   * @param input Confirmed (hopefully) password.
   */
  protected void newAccountConfirm(String input) {
    // See if the passwords match
    if (!newUserPass.equals(input)) {
      connection.sendln("\n\rPassword and confirmation do not match!");
      connection.send("\r");
      connection.echoOff();
      state = NEW_ACCOUNT_PASS;
      return;
    }

    // We are good to go, create the account and give them an update
    try {
      Account.createAccount(newUserName, newUserPass, false);
      connection.sendln(
        "\n\rAccount created! " +
        "Please login using your account name and password."
      );
      connection.echoOn();
    }
    catch (IOException ioe) {
      connection.sendln(
        "\n\rAn error occured while trying to create your account. " +
        "Please try again!"
      );
    }
    catch (IllegalArgumentException iae) {
      connection.sendln("\n\rSorry, an account with that name already exists!");
    }
    finally {
      state = ACCOUNT_NAME;
    }
  }

  /**
   * @see Controller.force()
   */
  public void force(String c) {
    parse(c);
  }

  /**
   * Handles parsing for the login controller.
   * @param s Input to parse.
   */
  public void parse(String s) {
    s = s.toLowerCase();

    if (state == ACCOUNT_NAME)
      accountName(s);
    else if (state == ACCOUNT_PASS)
      accountPassword(s);
    else if (state == NEW_ACCOUNT_NAME)
      newAccountName(s);
    else if (state == NEW_ACCOUNT_PASS)
      newAccountPassword(s);
    else if (state == NEW_ACCOUNT_CONFIRM)
      newAccountConfirm(s);
  }
}
