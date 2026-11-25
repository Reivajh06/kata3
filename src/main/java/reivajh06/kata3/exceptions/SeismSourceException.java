package reivajh06.kata3.exceptions;

public class SeismSourceException extends RuntimeException {

	public SeismSourceException(String message, Exception cause) {
		super(message, cause);
	}

	public SeismSourceException(String message) {
		super(message);
	}
}
