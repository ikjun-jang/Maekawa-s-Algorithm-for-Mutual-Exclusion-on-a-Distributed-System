/**
 * 
 */
package messageHandlerFactory;

import java.util.HashMap;
import java.util.Map;

import exceptions.InvalidMessageException;
import messageHandlers.AbstractMessageHandler;
import messages.AbstractMessage;
import messages.MessageType;

/**
 * Factory class that creates {@link AbstractMessageHandler} for incoming messages based on its type
 * 
 * @author <b>Emrah Cem</b>
 */
public abstract class AbstractMessageHandlerFactory {

	/**
	 * A map that keeps valid message types and corresponding message handlers for this factory.
	 */
	Map<MessageType, AbstractMessageHandler> validMessageNames = new HashMap<MessageType, AbstractMessageHandler>();

	/**
	 * Creates a message handler based on the type of the <code>message</code>
	 * 
	 * @param message the incoming message
	 * @return the handler for the incoming message
	 * @see {@link AbstractMessageHandler}
	 * @throws InvalidMessageException if <code>validMessageNAmes</code> does not have the type of incoming message
	 */
	public abstract AbstractMessageHandler createHandler(AbstractMessage message) throws InvalidMessageException;

	/**
	 * @param type type of the message
	 * @param messageHandler a handler for the <code>type</code>
	 * @return the last messageHandler for the <code>type</code> before adding it.
	 */
	protected AbstractMessageHandler addMessageHandler(MessageType type, AbstractMessageHandler messageHandler) {
		AbstractMessageHandler handler = validMessageNames.get(type);
		validMessageNames.put(type, messageHandler);
		return handler;
	}

	protected boolean isValidMessage(MessageType messageType) {
		return validMessageNames.containsKey(messageType);
	}
}
