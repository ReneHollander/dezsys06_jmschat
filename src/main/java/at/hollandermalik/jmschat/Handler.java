package at.hollandermalik.jmschat;

@FunctionalInterface
public interface Handler<P> {

	public void handle(P param);

}
