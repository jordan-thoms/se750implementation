package swiki;

public class SwikiException extends Exception {
	private static final long serialVersionUID = -2561866882542961686L;

	public SwikiException(String message) {
		super(message);
	}

	public SwikiException(String message, Exception cause) {
		super(message, cause);
	}
}
