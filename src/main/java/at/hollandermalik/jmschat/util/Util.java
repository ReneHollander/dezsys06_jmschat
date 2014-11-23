package at.hollandermalik.jmschat.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.time.format.DateTimeFormatter;

import at.hollandermalik.jmschat.message.ChatMessage;

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
	 *             Throws an IOException if there was an error getting the ip
	 */
	public static InetAddress getIp() throws IOException {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
		return InetAddress.getByName(in.readLine());
	}

	/**
	 * Serialize a Java Serializeable into a byte[]
	 * 
	 * @param ser
	 *            Serializeable to serialize
	 * @return serialized object as byte data
	 * @throws IOException
	 *             Throws an IOException if there was an error serializing the
	 *             object
	 */
	public static byte[] serializeToByteArray(Serializable ser) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(ser);
		oos.close();
		return baos.toByteArray();
	}

	/**
	 * Deserialize a byte[] into a Serializeable
	 * 
	 * @param data
	 *            Data to deserialize
	 * @return Serializeable from the data
	 * @throws IOException
	 *             Throws an IOException if there was an error deserializing the
	 *             object
	 * @throws ClassNotFoundException
	 *             Throws an CNFE if there was an error deserializing the object
	 */
	public static Serializable deserilizeFromByteArray(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Serializable ser = (Serializable) ois.readObject();
		ois.close();
		return ser;
	}

	/**
	 * Stringify's a ChatMessage to get printed to the user
	 * 
	 * @param message
	 *            Message to Stringify
	 * @return String to print to user
	 */
	public static String stringifyMessage(ChatMessage message) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(message.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
		sb.append("] ");
		sb.append(message.getNickname());
		sb.append("@");
		sb.append(message.getSenderIp().toString().replace("/", ""));
		sb.append(": ");
		sb.append(message.getContent());
		return sb.toString();
	}
}
