package messages.client;

import messages.AbstractMessage;
import messages.MessageType;

/**
 * The message a client sends to the server who sent a grant for a {@linkplain ReadRequest}.
 * 
 * @author <b>Emrah Cem</b>
 * @see ReadRequest
 */
public class ReadCommit extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber the sequence number of the corresponding request
	 */
	public ReadCommit(int senderId, long sequenceNumber) {
		super(senderId, sequenceNumber);
	}

	
	public void setType() {
		type = MessageType.READ_COMMIT;

	}

	/* (non-Javadoc)
	 * @see messages.AbstractMessage#toString()
	 */
	public String toString() {
		return super.toString() + "]";
	}

}
