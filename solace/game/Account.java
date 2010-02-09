package solace.game;

import java.io.*;

/**
 * Holds information pretaining to a game account.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Account 
{
	// Instance Variables
	String name;
	String password;
	int accountType;
	
	/*
	 * Account type constants.
	 */
	public static final int AT_NORMAL = 0;
	public static final int AT_ADMIN = 1;
	
	/*
	 * Account file location constants.
	 */
	protected static final String accountDir = "Accounts/";
	
	/**
	 * Loads an account from file on the disk and returns it.
	 * @param name Name of the account on file, note names are case insensitive.
	 * @return The fully instantiated account.
	 */
	public static Account loadFromFile(String name)
		throws IOException, FileNotFoundException
	{
		Account act = new Account();
		
		File inputFile = new File(accountDir + name.toLowerCase());
		BufferedReader in = new BufferedReader( new FileReader(inputFile) );
		
		String aname = in.readLine().trim();
		String pass = in.readLine().trim();
		int type = Integer.parseInt(in.readLine().trim());
		
		if (type < 0 || type > 1)
			type = 0;
		
		act.setName(aname);
		act.setPassword(pass);
		act.setAccountType(type);
		
		return act;
	}

	/**
	 * Determines if an account with the given name exists.
	 * @param name Name of the account.
	 * @return True if an account with the given name exists, false otherwise.
	 */
	public static boolean accountExists(String name)
	{
		return (new File(accountDir+name.toLowerCase())).exists();
	}
	
	/**
	 * Creates a new account.
	 * @param name Name for the account.
	 * @param password Password for the account.
	 * @param type Type of account.
	 * @return The newly created account.
	 * @throws IllegalArgumentException If an account with the given name already exists.
	 * @throws IOException If an i/o error occured while attempting to save the account.
	 */
	public static Account createAccount(String name, String password, int type)
		throws IllegalArgumentException, IOException
	{
		// Check to see if ana account with the given name already exists
		File accountFile = new File(accountDir+name.toLowerCase());
		if (accountFile.exists())
			throw new IllegalArgumentException("Account with given name already exists.");
		
		// Create the new account
		Account account = new Account();
		account.setName(name);
		account.setPassword(password);
		account.setAccountType(type);
	
		// Save the account to disk
		account.save();
		
		// Return the newly created account
		return account;
	}
	
	/**
	 * Saves the account's details to disk.
	 * @throws IOException if the file was unable to be written to disk.
	 */
	void save()
		throws IOException
	{
		File file = new File(accountDir+name.toLowerCase());
		PrintWriter out = new PrintWriter( new FileWriter(file) 	);
		
		out.println(name);
		out.println(password);
		out.println(accountType);
		
		out.close();
	}
	
	/**
	 * @return the accountType
	 */
	public int getAccountType() {
		return accountType;
	}

	/**
	 * @param accountType the accountType to set
	 */
	public void setAccountType(int accountType) {
		this.accountType = accountType;
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
