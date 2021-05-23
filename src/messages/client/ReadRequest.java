package messages.client;

import messages.AbstractRequest;
import messages.MessageType;

/**
 * Request message for reading a data object from a server
 * @author <b>Emrah Cem</b>
 */
public class ReadRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber the sequence number of the request
	 * @param objectIndex the index of the object for whom this request is sent
	 */
	public ReadRequest(int senderId, long sequenceNumber, int objectIndex) {
		super(senderId, sequenceNumber, objectIndex);
	}

	public void setType() {
		type = MessageType.READ_REQUEST;
	}

	/* (non-Javadoc)
	 * @see messages.AbstractRequest#toString()
	 */
	public String toString() {
		return super.toString() + "," + objectIndex + "]";
	}

}
