/**
 * 
 */
package messages.server;

import messages.AbstractMessage;
import messages.MessageType;

public class GrantMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	public int value = -1;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber
	 * @param value
	 */
	public GrantMessage(int senderId, long sequenceNumber, int value) {
		super(senderId, sequenceNumber);
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * @see messages.AbstractMessage#setType()
	 */
	@Override
	public void setType() {
		type = MessageType.GRANT_MESSAGE;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String toString() {
		return super.toString()+","+value+"]";
	}

}
