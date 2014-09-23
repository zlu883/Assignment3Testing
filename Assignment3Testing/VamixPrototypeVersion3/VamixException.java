/**
 * An exception class that handles Vamix errors.
 */
public class VamixException extends Exception {

	private static final long serialVersionUID = 1L;
	private String _message;
	
	public VamixException(String message) {
		_message = message;
	}
	
	public String getMessage() {
		return _message;
	}

}
