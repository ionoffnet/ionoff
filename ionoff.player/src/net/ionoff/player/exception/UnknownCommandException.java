package net.ionoff.player.exception;

public class UnknownCommandException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnknownCommandException(String command) {
		super("Unknown command: " + command);
	}
}
