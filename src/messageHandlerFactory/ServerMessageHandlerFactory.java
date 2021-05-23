package messageHandlerFactory;

import client_server.server.Server;

import messageHandlers.AbstractMessageHandler;
import messageHandlers.server.ReadCommitHandler;
import messageHandlers.server.ReadRequestHandler;
import messageHandlers.server.ReleaseLockHandler;
import messageHandlers.server.ServerMessageHandler;
import messageHandlers.server.WithdrawHandler;
import messageHandlers.server.WriteCommitHandler;
import messageHandlers.server.WriteRequestHandler;
import messages.AbstractMessage;
import messages.MessageType;

import exceptions.InvalidMessageException;

/**
 * This class creates message handler for the server when a message arrives to it.
 * @author <b>Emrah Cem</b>
 */
public class ServerMessageHandlerFactory extends AbstractMessageHandlerFactory {
	
	/**
	 * The server for which this factory will create handlers
	 */
	private Server server;

	/**
	 * Constructor specifying the server for which this factory will create handlers
	 * @param server the server for which this factory will create handlers
	 */
	public ServerMessageHandlerFactory(Server server) {
		this.server = server;
		validMessageNames.put(MessageType.READ_REQUEST, new ReadRequestHandler());
		validMessageNames.put(MessageType.READ_COMMIT, new ReadCommitHandler());
		validMessageNames.put(MessageType.WRITE_REQUEST, new WriteRequestHandler());
		validMessageNames.put(MessageType.WRITE_COMMIT, new WriteCommitHandler());
		validMessageNames.put(MessageType.WITHDRAW_MESSAGE, new WithdrawHandler());
		validMessageNames.put(MessageType.RELEASE_LOCK, new ReleaseLockHandler());
	}

	
	/* (non-Javadoc)
	 * @see messageHandlerFactory.AbstractMessageHandlerFactory#createHandler(messages.AbstractMessage)
	 */
	public AbstractMessageHandler createHandler(AbstractMessage message) throws InvalidMessageException {
		AbstractMessageHandler handler = null;
		
		if (!isValidMessage(message.getType())) {
			throw new InvalidMessageException("Not a valid message");
		}

		// Here is the beauty of polymorphism...
		handler = validMessageNames.get(message.getType());
		((ServerMessageHandler) handler).setServer(server);

		return handler;
	}

}
