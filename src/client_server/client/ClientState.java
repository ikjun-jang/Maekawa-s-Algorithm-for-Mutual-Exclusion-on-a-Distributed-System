/**
 * 
 */
package client_server.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class keeps the state of a client.
 * 
 * @author <b>Emrah Cem</b>
 * @see Client
 */
public class ClientState {

	/**
	 * State to keep whether the client is waiting for specific grant
	 * 
	 * @author <b>Emrah Cem</b>
	 * 
	 */
	public enum WaitState {
		NOT_WAIT, READ_WAIT, WRITE_WAIT
	};

	/**
	 * Wait state of the client
	 * 
	 * @see WaitState
	 */
	private WaitState waitState;
	/**
	 * Whether grant is received
	 */
	private boolean granted;
	/**
	 * Number of successful read operations
	 */
	private int successfullRead;
	/**
	 * Number of successful write operations
	 */
	private int successfullWrite;
	/**
	 * Number of unsuccessful read operations
	 */
	private int unsuccessfullRead;
	/**
	 * Number of unsuccessful write operations
	 */
	private int unsuccessfullWrite;
	/**
	 * Map of data object index to list of received data object values
	 */
	private Map<Integer, List<Integer>> receivedValues;
	/**
	 * For the successful READ accesses, keeps the time between issuing a READ_REQUEST and receiving grant for that request
	 */
	private List<Long> readTimes;
	/**
	 * For the successful WRITE accesses, keeps the time between issuing a WRITE_REQUEST and receiving grant for that request
	 */
	private List<Long> writeTimes;
	/**
	 * Total number of messages sent by the client
	 */
	private long totalNumberOfMessagesSent;

	/**
	 * Default Constructor
	 */
	public ClientState() {
		this.receivedValues = new HashMap<Integer, List<Integer>>();
		this.waitState = WaitState.NOT_WAIT;
		this.readTimes = new LinkedList<Long>();
		this.writeTimes = new LinkedList<Long>();
	}

	public List<Long> getWriteTimes() {
		return writeTimes;
	}

	public void setWriteTimes(List<Long> writeTimes) {
		this.writeTimes = writeTimes;
	}

	public List<Long> getReadTimes() {
		return readTimes;
	}

	public void setReadTimes(List<Long> readTimes) {
		this.readTimes = readTimes;
	}

	public int getSuccessfullRead() {
		return successfullRead;
	}

	public void setSuccessfullRead(int successfullRead) {
		this.successfullRead = successfullRead;
	}

	public void increaseSuccessfullRead() {
		this.successfullRead += 1;
	}

	public int getSuccessfullWrite() {
		return successfullWrite;
	}

	public void setSuccessfullWrite(int successfullWrite) {
		this.successfullWrite = successfullWrite;
	}

	public void increaseSuccessfullWrite() {
		this.successfullWrite += 1;
	}

	public int getUnsuccessfullRead() {
		return unsuccessfullRead;
	}

	public void setUnsuccessfullRead(int unsuccessfullRead) {
		this.unsuccessfullRead = unsuccessfullRead;
	}

	public void increaseUnsuccessfullRead() {
		this.unsuccessfullRead += 1;
	}

	public int getUnsuccessfullWrite() {
		return unsuccessfullWrite;
	}

	public void setUnsuccessfullWrite(int unsuccessfullWrite) {
		this.unsuccessfullWrite = unsuccessfullWrite;
	}

	public void increaseUnsuccessfullWrite() {
		this.unsuccessfullWrite += 1;
	}

	public WaitState getWaitState() {
		return waitState;
	}

	public void setWaitState(WaitState waitState) {
		this.waitState = waitState;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}

	public Map<Integer, List<Integer>> getReceivedValues() {
		return receivedValues;
	}

	public void setReceivedValues(HashMap<Integer, List<Integer>> receivedValues) {
		this.receivedValues = receivedValues;
	}

	public long getTotalNumberOfMessagesSent() {
		return totalNumberOfMessagesSent;
	}

	public void setTotalNumberOfMessagesSent(long totalNumberOfMessagesExchanged) {
		this.totalNumberOfMessagesSent = totalNumberOfMessagesExchanged;
	}

	public void increaseTotalNumberOfMessagesSent() {
		this.totalNumberOfMessagesSent += 1;
	}

	/**
	 * Gives summary statistics of a list as a string
	 * 
	 * @param list the list whose summary statistics will be returned as string
	 * @return the summary statistics as a string
	 */
	private String summaryStatsInStr(List<Long> list) {
		String str = "";
		if (!list.isEmpty()) {
			double avg = 0;
			for (long element : list) {
				avg += element;
			}
			avg = avg / list.size();
			System.out.println(list);
			str += "min=" + Collections.min(list) + ", max=" + Collections.max(list) + ", avg=" + avg;
		} else {
			str += "No succesful operation of this type.";
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = "";
		str += "# of successful read:" + successfullRead + "\r\n";
		str += "# of unsuccessful read:" + unsuccessfullRead + "\r\n";
		str += "# of successful write:" + successfullWrite + "\r\n";
		str += "# of unsuccessful write:" + unsuccessfullWrite + "\r\n";
		str += "Time to get read grant (summary statistics in ms):" + summaryStatsInStr(readTimes) + "\r\n";
		str += "Time to get write grant (summary statistics in ms):" + summaryStatsInStr(writeTimes) + "\r\n";
		str += "Number of messages sent:" + totalNumberOfMessagesSent;
		return str;
	}

}
