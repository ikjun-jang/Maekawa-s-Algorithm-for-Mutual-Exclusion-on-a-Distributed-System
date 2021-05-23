package messages;

/**
 * Request is a message to perform an operation on a data object
 * 
 * @author <b>Emrah Cem</b>
 */
public abstract class AbstractRequest extends AbstractMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * The index of the object this requests is for
	 */
	protected int objectIndex;

	/**
	 * Constructor specifying the sender id, sequence number, and index of the data object
	 * 
	 * @param senderId the sender id of this request
	 * @param sequenceNumber the sequence number
	 * @param objectIndex the index of the data object this requests is for
	 */
	public AbstractRequest(int senderId, long sequenceNumber, int objectIndex) {
		super(senderId, sequenceNumber);
		this.objectIndex = objectIndex;
	}

	public int getObjectIndex() {
		return objectIndex;
	}

	public void setObjectIndex(int objectIndex) {
		this.objectIndex = objectIndex;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(AbstractRequest o) {
		return this.senderId - o.senderId;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		boolean bool = false;
		if (!(obj instanceof AbstractRequest))
			bool = false;
		else {
			bool = this.senderId == ((AbstractRequest) obj).senderId && this.sequenceNumber == ((AbstractRequest) obj).sequenceNumber;
		}

		return bool;
	}

	/*
	 * (non-Javadoc)
	 * @see messages.AbstractMessage#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}

}
