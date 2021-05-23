package messages;

import java.io.Serializable;

/**
 * Message is an abstraction for communication between clients and servers
 * @author <b>Emrah Cem</b>
 */
public abstract class AbstractMessage implements Serializable, Comparable<AbstractMessage> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Type of this message
	 */
	protected MessageType type;

	/**
	 * The sender id of this message
	 */
	protected int senderId;

	/**
	 * The sequence number of this message
	 */
	protected long sequenceNumber;

	protected int priority=1; 

	/**
	 * Constructor specifying the sender id and sequence number
	 * 
	 * @param senderId the sender id
	 * @param sequenceNumber the sequence number
	 */
	public AbstractMessage(int senderId, long sequenceNumber) {
		setType();
		this.senderId = senderId;
		this.sequenceNumber = sequenceNumber;
	}

	public abstract void setType();

	public MessageType getType() {
		return type;
	}

	public int getSenderId() {
		return senderId;
	}

	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int compareTo(AbstractMessage other){
		
		return priority - other.priority;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getType().toString() + "[" + senderId + "," + sequenceNumber;
	}

}
