/**
 * 
 */
package messages.client;

import messages.AbstractMessage;
import messages.MessageType;

/**
 * The message a client sends to all servers upon receiving a write grant.
 * 
 * @author <b>Emrah Cem</b>
 */
public class WriteCommit extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * the boolean informing the server to release the lock of the object or not
	 */
	private boolean releaseLock;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber the sequence number of the corresponding request
	 * @param releaseLock the boolean informing the server to release the lock of the object or not
	 */
	public WriteCommit(int senderId, long sequenceNumber, boolean releaseLock) {
		super(senderId, sequenceNumber);
		this.releaseLock = releaseLock;
	}

	/*
	 * (non-Javadoc)
	 * @see messages.AbstractMessage#setType()
	 */
	@Override
	public void setType() {
		type = MessageType.WRITE_COMMIT;
	}

	public boolean isReleaseLock() {
		return releaseLock;
	}

	public void setReleaseLock(boolean releaseLock) {
		this.releaseLock = releaseLock;
	}

	public String toString() {
		return super.toString() + "," + releaseLock + "]";
	}

}
