package pdstore;

public class PDStoreException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PDStoreException(String message) {
		super(message);
	}
	
	public PDStoreException(String message, Exception cause) {
		super(message, cause);
	}

}
