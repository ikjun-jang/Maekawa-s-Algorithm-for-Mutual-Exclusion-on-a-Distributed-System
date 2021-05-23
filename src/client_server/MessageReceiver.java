package client_server;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import messageHandlerFactory.AbstractMessageHandlerFactory;
import messages.AbstractMessage;

/**
 * An abstract class representing an entity that receives messages.  
 * @author <b>Emrah Cem</b>
 * @see AbstractMessage
 * @see AbstractMessageHandlerFactory
 */
public abstract class MessageReceiver {


	/**
	 * The unique id of the messageReceiver
	 * 
	 * @see #getId()
	 */
	protected final int id;

	/**
	 * Lock used for mutual exclusion
	 */
	protected ReentrantLock lock;
	
	/**
	 * Number of messages received by this message receiver
	 */
	protected long numberOfMessagesReceived;
	
	/**
	 * The queue at which incoming messages come in to be processed
	 * 
	 * @see #getMessageQueue()
	 */
	protected PriorityBlockingQueue<FIFOEntry<AbstractMessage>> messageQueue;

	/**
	 * Factory that creates handlers for incoming messages
	 */
	protected AbstractMessageHandlerFactory messageHandlerFactory;

	/**
	 * Constructor specifying the id of this message receiver
	 */
	public MessageReceiver(int id) {
		this.messageQueue = new PriorityBlockingQueue<FIFOEntry<AbstractMessage>>();
		this.id = id;
		this.lock = new ReentrantLock();
	}


	public PriorityBlockingQueue<FIFOEntry<AbstractMessage>> getMessageQueue() {
		return messageQueue;
	}

	public void setMessageQueue(PriorityBlockingQueue<FIFOEntry<AbstractMessage>> messageQueue) {
		this.messageQueue = messageQueue;
	}


	public AbstractMessageHandlerFactory getMessageHandlerFactory() {
		return messageHandlerFactory;
	}

	public void setMessageHandlerFactory(AbstractMessageHandlerFactory messageHandlerFactory) {
		this.messageHandlerFactory = messageHandlerFactory;
	}

	public int getId() {
		return id;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public void setLock(ReentrantLock lock) {
		this.lock = lock;
	}
	

	public long getNumberOfMessagesReceived() {
		return numberOfMessagesReceived;
	}


	public void setNumberOfMessagesReceived(long numberOfMessagesReceived) {
		this.numberOfMessagesReceived = numberOfMessagesReceived;
	}
	
	public void increaseNumberOfMessagesReceived() {
		this.numberOfMessagesReceived += 1;
	}


}
