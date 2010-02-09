package solace.util;
import java.util.Date;


/**
 * Basic log class for logging events and errors in the system.
 * @author Ryan Sandor Richards (Gaius)
 */
public class Log 
{
	/**
	 * Basic error logging function.
	 * @param s Message.
	 */
	public static void error(String s)
	{
		Date d = new Date();
		System.out.println("[ERROR] ("+d+"): " + s);
	}
	
	/**
	 * Basic information logging function.
	 * @param s Message.
	 */
	public static void info(String s)
	{
		Date d = new Date();
		System.out.println("[INFO] ("+d+"): "+s);
	}
}
