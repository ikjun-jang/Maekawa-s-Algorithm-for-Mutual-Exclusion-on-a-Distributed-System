package messages.server;

import messages.AbstractMessage;
import messages.MessageType;

public class TriggerMessage extends AbstractMessage{

	
	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber
	 */
	public TriggerMessage(int senderId, long sequenceNumber) {
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
		type = MessageType.TRIGGER_MESSAGE;
		
	}

}
