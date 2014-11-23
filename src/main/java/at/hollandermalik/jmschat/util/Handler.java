package at.hollandermalik.jmschat.util;

/**
 * Handle a parameter without a return value
 * 
 * @author Rene Hollander
 *
 * @param <P>
 *            Type of the Parameter
 */
@FunctionalInterface
public interface Handler<P> {

	/**
	 * Handle for example async data without a result
	 * 
	 * @param param
	 *            Parameter to handle
	 */
	public void handle(P param);

}
