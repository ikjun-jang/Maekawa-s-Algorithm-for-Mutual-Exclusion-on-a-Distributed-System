package messages.client;

import messages.AbstractMessage;
import messages.MessageType;

/**
 * Message that is sent to all servers when timeout occurs (grant is not received within time)
 * 
 * @author <b>Emrah Cem</b>
 */
public class WithdrawMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber the sequence number of the corresponding request
	 */
	public WithdrawMessage(int senderId, long sequenceNumber) {
		super(senderId, sequenceNumber);
	}

	/*
	 * (non-Javadoc)
	 * @see messages.AbstractMessage#setType()
	 */
	@Override
	public void setType() {
		type = MessageType.WITHDRAW_MESSAGE;

	}

	public String toString() {
		return super.toString() + "]";
	}

}
