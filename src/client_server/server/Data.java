package client_server.server;

import java.util.LinkedList;

import messages.AbstractRequest;

/**
 * @param <T> The type of data object. Type of the data object is generic because users may want to keep different type of data object in the server, not necessarily an integer.
 * @author <b>Emrah Cem</b>
 */
public class Data<T> {

	/**
	 * Possible states of a {@link Data} object
	 * 
	 * @author <b>Emrah Cem</b>
	 */

	public static enum State {
		/**
		 * Not locked by any client request
		 */
		NOT_LOCKED,
		/**
		 * Read locked by a client request
		 */
		READ_LOCKED,
		/**
		 * Write locked by a client request
		 */
		WRITE_LOCKED
	}

	/**
	 * Data object
	 */
	private T dataObject;

	/**
	 * State of the data object.
	 * 
	 * @see State
	 */
	private State state;

	/**
	 * The number of READ_REQUESTs that lock the data object
	 */
	private int readLockCount;

	/**
	 * The request that locks this data object
	 */
	private AbstractRequest lockingRequest;

	/**
	 * Queue that keeps pending requests for this data object
	 * 
	 * @see AbstractRequest
	 */
	LinkedList<AbstractRequest> requestQueue;

	/**
	 * Constructor specifying the data object
	 * 
	 * @param dataObject the data object
	 */
	public Data(T dataObject) {
		this.dataObject = dataObject;
		this.state = State.NOT_LOCKED;
		this.readLockCount = 0;
		this.lockingRequest = null;
		this.requestQueue = new LinkedList<AbstractRequest>();
	}

	public T getDataObject() {
		return dataObject;
	}

	public void setDataObject(T dataObject) {
		this.dataObject = dataObject;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getReadLockCount() {
		return readLockCount;
	}

	public void setReadLockCount(int readLockCount) {
		this.readLockCount = readLockCount;
	}

	public void incrementReadLockCount() {
		this.readLockCount += 1;
	}

	public void decrementReadLockCount() {
		this.readLockCount -= 1;
	}

	public AbstractRequest getLockingRequest() {
		return lockingRequest;
	}

	public void setLockingRequest(AbstractRequest lockingRequest) {
		this.lockingRequest = lockingRequest;
	}

	public LinkedList<AbstractRequest> getRequestQueue() {
		return requestQueue;
	}

	public void setRequestQueue(LinkedList<AbstractRequest> requestQueue) {
		this.requestQueue = requestQueue;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = "";
		str += "[ value:" + String.valueOf(dataObject) + ", state:" + getState() + ", readLockCount:" + getReadLockCount() + ", lockingRequest:" + getLockingRequest() + ", queue:[ ";
		for (AbstractRequest req : requestQueue) {
			str += req + ", ";
		}
		str += "] ]";
		return str;
	}

}
