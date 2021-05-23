package exceptions;

/**
 * Exception that is thrown when an invalid request is received by either client or server<id>.
 * 
 * @author Emrah Cem
 */
public class InvalidMessageException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMessageException(String message) {
	       super(message);
	       
	}
	 
}
