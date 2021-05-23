package client_server;

import java.util.concurrent.BlockingQueue;

import exceptions.InvalidMessageException;

import messageHandlers.AbstractMessageHandler;
import messages.AbstractMessage;

/**
 * This class is responsible for handling incoming messages. All incoming messages are put into <code>{@link #queue}</code> and this class
 * removes them from the queue to process   
 * @author <b>Emrah Cem</b>
 */
public class MessageConsumer implements Runnable {
	/**
	 * Queue from which messages are removed to be processed
	 * @see BlockingQueue
	 */
	private BlockingQueue<FIFOEntry<AbstractMessage>> queue;
	/**
	 * An entity whose state will be updated based on incoming messages
	 */
	private MessageReceiver msg_receiver;

	/**
	 * Constructor specifying the message receiver entity and queue from which messages are removed to be processed
	 * @param msg_receiver the entity whose state will be updated based on incoming messages
	 * @param queue the queue from which messages are removed to be processed
	 */
	public MessageConsumer(MessageReceiver msg_receiver, final BlockingQueue<FIFOEntry<AbstractMessage>> queue) {
		this.queue = queue;
		this.msg_receiver = msg_receiver;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while (true) {
			try {
				final AbstractMessage message = queue.take().getEntry();
				msg_receiver.getLock().lock();
				try {
					System.out.println(message + " received from " + message.getSenderId());
					msg_receiver.increaseNumberOfMessagesReceived();
					if (message != null) {
						AbstractMessageHandler msg_handler = msg_receiver.getMessageHandlerFactory().createHandler(message);
						msg_handler.handleMessage(message);
						System.out.println(msg_receiver);
					}
				} finally {
					msg_receiver.getLock().unlock();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvalidMessageException e) {
				e.printStackTrace();
			}

		}
	}
}