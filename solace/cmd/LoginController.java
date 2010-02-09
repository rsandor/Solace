package solace.cmd;

import java.io.IOException;
import java.util.regex.Pattern;
import solace.util.*;
import solace.game.Account;
import solace.game.Game;
import solace.net.*;

public class LoginController 
	implements StateController 
{
	/*
	 * Login State Constants
	 */
	public static final int COLOR = 0;
	public static final int ACCOUNT_NAME = 1;
	public static final int ACCOUNT_PASS = 2;
	public static final int NEW_ACCOUNT_NAME = 3;
	public static final int NEW_ACCOUNT_PASS = 4;
	public static final int NEW_ACCOUNT_CONFIRM = 5;
	
	// Instance Variables
	int state = COLOR;
	Connection connection;
	String newUserName = "";
	String newUserPass = "";

	
	public LoginController(Connection c)
	{
		init(c);
	}
	
	public void init(Connection c)
	{
		connection = c;
		c.setPrompt("Use ANSI Color (Y/N)? ");
	}
	
	/**
	 * Handles whether or not the user wants to use color.
	 * @param input
	 */
	protected void useColor(String input)
	{
		if (input.toLowerCase().charAt(0) == 'y')
			connection.setUseColor(true);
		else
			connection.setUseColor(false);
		
		connection.sendln( Game.getMessageManager().get("Intro") );
		connection.setPrompt("Account: ");
		
		state = ACCOUNT_NAME;
	}
	
	
	/**
	 * Login handler parses input for account name and attempts
	 * to load account associated with that name.
	 * @param input Input from the user.
	 */
	protected void accountName(String input)
	{
		String aname = input;
		
		// Check to see if they want to make a new account
		if (input.toLowerCase().equals("new"))
		{
			connection.sendln( Game.getMessageManager().get("NewAccountRules") );
			connection.setPrompt("Name for account: ");
			state = NEW_ACCOUNT_NAME;
			return;
		}
		
		// Ensure they are not attempting to login twice
		if (Game.getWorld().isLoggedIn(aname))
		{
			connection.sendln("{rAccount already logged in!{x");
			Log.info("Double login attempt for account '" + aname + "' from " + connection.getInetAddress());
			connection.close();
			return;
		}
		
		// Try to load the account.
		try 
		{
			connection.setAccount( Account.loadFromFile(aname) );
			state = ACCOUNT_PASS;
			connection.setPrompt("Password: ");
		}
		catch (IOException ioe)
		{
			connection.sendln("Account not found, enter '{ynew{x' to create a new account!");
		}
	}
	
	/**
	 * Login handler parses input for account password and attempts
	 * to verify that the user has entered the correct password.
	 * @param input Input from user.
	 */
	protected void accountPassword(String input)
	{
		String pass = input;
		
		// Ensure that an account has been loaded
		if (!connection.hasAccount())
		{
			state = ACCOUNT_NAME;
			connection.setPrompt("Account: ");
			return;
		}
		
		Account account = connection.getAccount();
		
		// Check for an incorrect password
		if (!pass.equals(account.getPassword()))
		{
			Log.info("Incorrect password given for user '" + account.getName() + "' from " + 
				connection.getInetAddress());
			connection.sendln("Incorrect password.");
			connection.close();
			return;
		}
		
		// Everything seems fine, log them in and present the game's main menu
		Game.getWorld().addAccount(connection, account);
		Log.info("Account '" + account.getName() + "' logged into from " + connection.getInetAddress());
		connection.sendln("\nWelcome " + connection.getAccount().getName() + "!");
		connection.setStateController( new MainMenu(connection) );
	}
	
	/**
	 * For the creation of new accounts, this processes an account name input.
	 * @param input Input given by user.
	 */
	protected void newAccountName(String input)
	{
		// Check the validity of the name
		if (!Pattern.matches("\\w+\\z", input))
		{
			connection.sendln("Invalid name, please use letters and numbers only!");
			return;
		}
		
		// Check to see if the name already exists
		if (Account.accountExists(input.toLowerCase()))
		{
			connection.sendln("An account with the given name already exists, please choose another.");
			return;
		}
		
		// If all is well move on to the next step
		newUserName = input.toLowerCase();
		connection.setPrompt("Password for Account: ");
		state = NEW_ACCOUNT_PASS;
	}
	
	/**
	 * For the creation of new accounts, this processes account password input.
	 * @param input Password for the account.
	 */
	protected void newAccountPassword(String input)
	{
		// Ensure the password is long enough
		if (input.length() < 6)
		{
			connection.sendln("Passwords must be at least 6 letters in length!");
			return;
		}
		
		newUserPass = input;
		connection.setPrompt("Confirm Password: ");
		state = NEW_ACCOUNT_CONFIRM;
	}
	
	/**
	 * For the creation of new accounts, this processes the password confirmation.
	 * @param input Confirmed (hopefully) password.
	 */
	protected void newAccountConfirm(String input)
	{
		// See if the passwords match
		if (!newUserPass.equals(input))
		{
			connection.sendln("Password and confirmation do not match!");
			connection.setPrompt("Password for Account: ");
			state = NEW_ACCOUNT_PASS;
			return;
		}
		
		// We are good to go, create the account and give them an update
		try
		{
			Account.createAccount(newUserName, newUserPass, Account.AT_NORMAL);
			connection.sendln("Account created! Please login using your account name and password.");
		}
		catch (IOException ioe)
		{
			connection.sendln("An error occured while trying to create your account. Please try again!");
		}
		catch (IllegalArgumentException iae)
		{
			connection.sendln("Sorry, an account with that name already exists!");
		}
		finally
		{
			state = ACCOUNT_NAME;
			connection.setPrompt("Account: ");
		}
	}

	/**
	 * Handles parsing for the login controller.
	 * @param s Input to parse.
	 */
	public void parse(String s)
	{
		s = s.toLowerCase();
		
		if (state == COLOR)
			useColor(s);
		else if (state == ACCOUNT_NAME)
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
