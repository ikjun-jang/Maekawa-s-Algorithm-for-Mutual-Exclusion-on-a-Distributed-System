/**
 * 
 */
package messages.client;

import messages.AbstractMessage;
import messages.MessageType;

/**
 * Message to inform the server that it can release the lock for the object and give its grant to a pending request (if exists)
 * 
 * @author <b>Emrah Cem</b>
 */
public class ReleaseLock extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber the sequence number of the corresponding request
	 */
	public ReleaseLock(int senderId, long sequenceNumber) {
		super(senderId, sequenceNumber);
	}

	/*
	 * (non-Javadoc)
	 * @see messages.AbstractMessage#setType()
	 */
	@Override
	public void setType() {
		type = MessageType.RELEASE_LOCK;

	}

	/*
	 * (non-Javadoc)
	 * @see messages.AbstractMessage#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString() + "]";
	}

}
