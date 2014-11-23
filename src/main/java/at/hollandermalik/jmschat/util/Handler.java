package at.hollandermalik.jmschat.util;

@FunctionalInterface
public interface Handler<P> {

	public void handle(P param);

}
