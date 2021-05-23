/**
 * 
 */
package messages.server;

import messages.AbstractMessage;
import messages.MessageType;

public class AckMessage extends AbstractMessage{
	
	
	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber
	 */
	public AckMessage(int senderId, long sequenceNumber) {
		super(senderId, sequenceNumber);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/* (non-Javadoc)
	 * @see messages.AbstractMessage#setType()
	 */
	@Override
	public void setType() {
		type = MessageType.ACK_MESSAGE;
	}
	
	/* (non-Javadoc)
	 * @see messages.AbstractMessage#toString()
	 */
	@Override
	public String toString() {
		return super.toString()+"]";
	}
}
