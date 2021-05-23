package exceptions;

/**
 * Exception that is thrown when an invalid config file is provided by the user. A valid config file can have lines including IP:port <id>.
 * <p>
 * <pre>
 * For example :<br>
 * 124.23.45.23:8080
 * 125.45.23.45:9090
 * domain.hostname:10000
 * </pre>
 * 
 * @author Emrah Cem
 */
public class InvalidConfigFileException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public InvalidConfigFileException(String message) {
		super(message);

	}
}
