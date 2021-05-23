package messages.client;

import messages.AbstractRequest;
import messages.MessageType;

/**
 * Request message that is sent to all servers to update a data object.
 * 
 * @author <b>Emrah Cem</b>
 */
public class WriteRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	private int v;

	/**
	 * @param senderId the id of the sender of this message
	 * @param sequenceNumber the sequence number of the request
	 * @param objectIndex the index of the object for whom this request is sent
	 * @param v the amount of increment for the selected data object
	 */
	public WriteRequest(int senderId, long sequenceNumber, int objectIndex, int v) {
		super(senderId, sequenceNumber, objectIndex);
		this.v = v;
	}

	public int getV() {
		return v;
	}

	public void setV(int v) {
		this.v = v;
	}

	@Override
	public void setType() {
		type = MessageType.WRITE_REQUEST;
	}

	public String toString() {
		super.toString();
		return super.toString() + "," + objectIndex + "," + v + "]";
	}

}
