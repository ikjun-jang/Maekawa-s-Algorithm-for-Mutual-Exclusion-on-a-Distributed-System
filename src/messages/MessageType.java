package messages;

/**
 * Type of a message
 * 
 * @author <b>Emrah Cem</b>
 * @see AbstractMessage
 */
public enum MessageType {
	/**
	 * Read request
	 */
	READ_REQUEST,
	/**
	 * Write request
	 */
	WRITE_REQUEST,
	/**
	 * Write commit
	 */
	WRITE_COMMIT,
	/**
	 * Read commit
	 */
	READ_COMMIT,
	/**
	 * Withdraw message
	 */
	WITHDRAW_MESSAGE,
	/**
	 * Grant message
	 */
	GRANT_MESSAGE,
	/**
	 * Trigger message to initiate the protocol (sent by a server with id 1)
	 */
	TRIGGER_MESSAGE,
	/**
	 * Ack message
	 */
	ACK_MESSAGE,
	/**
	 * Release lock (introduced to achieve same order of updates in all servers)
	 */
	RELEASE_LOCK
}
