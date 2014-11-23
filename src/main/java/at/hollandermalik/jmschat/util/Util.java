package at.hollandermalik.jmschat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

/**
 * Simple Util class
 * 
 * @author Rene Hollander
 */
public class Util {

	/**
	 * Gets the current WAN IP
	 * 
	 * @return WAN IP
	 * @throws IOException
	 */
	public static InetAddress getIp() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		return InetAddress.getByName(in.readLine());
	}
}
