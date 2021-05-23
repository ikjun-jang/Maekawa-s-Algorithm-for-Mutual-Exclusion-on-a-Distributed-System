/**
 * 
 */
package messageHandlerFactory;

import client_server.client.Client;

import messageHandlers.AbstractMessageHandler;
import messageHandlers.client.AckMessageHandler;
import messageHandlers.client.ClientMessageHandler;
import messageHandlers.client.GrantMessageHandler;
import messages.AbstractMessage;
import messages.MessageType;
import exceptions.InvalidMessageException;

/**
 * This class creates message handler for the client when a message arrives to it.
 * @author <b>Emrah Cem</b>
 */
public class ClientMessageHandlerFactory extends AbstractMessageHandlerFactory {

	/**
	 * The client for which this factory will create handlers
	 */
	private Client client;

	/**
	 * Constructor specifying the client for which this factory will create handlers
	 * @param client the client for which this factory will create handlers
	 */
	public ClientMessageHandlerFactory(Client client) {
		this.client = client;
		validMessageNames.put(MessageType.GRANT_MESSAGE, new GrantMessageHandler());
		validMessageNames.put(MessageType.ACK_MESSAGE, new AckMessageHandler());
	}


	/* (non-Javadoc)
	 * @see messageHandlerFactory.AbstractMessageHandlerFactory#createHandler(messages.AbstractMessage)
	 */
	public AbstractMessageHandler createHandler(AbstractMessage message) throws InvalidMessageException {

		AbstractMessageHandler handler = null;

		if (!isValidMessage(message.getType())) {
			throw new InvalidMessageException("Not a valid message.");
		}
		// Here is the beauty of polymorphism...
		handler = validMessageNames.get(message.getType());
		((ClientMessageHandler) handler).setClient(client);

		return handler;

	}
}
