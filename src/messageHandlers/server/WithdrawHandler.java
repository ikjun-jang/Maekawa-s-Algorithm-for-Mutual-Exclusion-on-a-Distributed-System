package messageHandlers.server;

import java.io.IOException;

import client_server.FIFOEntry;
import client_server.server.Data;

import messages.AbstractMessage;
import messages.AbstractRequest;
import messages.client.ReadRequest;
import messages.client.WithdrawMessage;
import messages.client.WriteRequest;
import messages.server.AckMessage;

/**
 * Handler class for WITHDRAW messages
 * 
 * @author <b>Emrah Cem</b>
 */
public class WithdrawHandler extends ServerMessageHandler {

	/* (non-Javadoc)
	 * @see messageHandlers.server.ServerMessageHandler#handleMessage(messages.AbstractMessage)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {
		boolean handled = false;

		// received WITHDRAW message
		WithdrawMessage withdraw = (WithdrawMessage) message;

		// Corresponding request (from the senderId, the client object is found and its lastRequest message is retreived)
		AbstractRequest request = (AbstractRequest) server.getRemoteHash().get(withdraw.getSenderId()).getLastRequest();

		try {
			if (!super.handleMessage(message)) {
				if (request instanceof ReadRequest) {

					// corresponding request is a READ_REQUEST message
					ReadRequest read_request = (ReadRequest) request;

					// data object of interest
					Data<Integer> d = server.getData(read_request.getObjectIndex());

					// if the data object is in READ_LOCKED state
					if (d.getState() == Data.State.READ_LOCKED) {

						// decrement the READ_LOCK_COUNT
						d.decrementReadLockCount();

						// IF as a result of above decrease, READ_LOCK_COUNT becomes zero, the LOCK for that object is set to NOT_LOCKED
						if (d.getReadLockCount() == 0) {
							d.setState(Data.State.NOT_LOCKED);
							d.setLockingRequest(null);

							// and the server can grant the request at the head of the queue of pending requests for the object
							if (!d.getRequestQueue().isEmpty()) {
								System.out.println("Head of the request queue was dequeued");
								AbstractMessage head = d.getRequestQueue().poll();
								head.setPriority(0);
								server.getMessageQueue().put(new FIFOEntry<AbstractMessage>(head));

							}
						}
					}
					// if the data object is in WRITE_LOCKED state
					else if (d.getState() == Data.State.WRITE_LOCKED) {

						// if READ_REQUEST is in the queue, remove it
						if (d.getRequestQueue().remove(read_request)) {
							System.out.println("Removed request from the queue");
						} else {
							System.out.println("Could not find the request!!! It should have already been granted");
						}

					}
					// if the data object is in NOT_LOCKED state, and there is no pending request,
					// GRANT message should already have been sent, Do nothing.
					else if (d.getState() == Data.State.NOT_LOCKED && d.getRequestQueue().isEmpty()) {
						System.out.println("Could not find the request!!! It should have already been granted");
					}

					// if the data object is in NOT_LOCKED state, and there is a pending request, the server can grant
					// the request at the head of the queue of pending requests for the object
					if (d.getState() == Data.State.NOT_LOCKED && !d.getRequestQueue().isEmpty()) {
						System.out.println("Head of the request queue was dequeued");
						AbstractMessage head = d.getRequestQueue().poll();
						head.setPriority(0);
						server.getMessageQueue().put(new FIFOEntry<AbstractMessage>(head));
					}

				} else if (request instanceof WriteRequest) {

					// corresponding request is a WRITE_REQUEST message
					WriteRequest write_request = (WriteRequest) request;

					// data object of interest
					Data<Integer> d = server.getData(write_request.getObjectIndex());

					server.increaseNumberOfUpdateAttempts();
					System.out.println("NUMBER OF WRITE ATTEMPTS IS INCREASED TO " + server.getNumberOfUpdateAttempts());

					AckMessage ack_msg = new AckMessage(server.getId(), write_request.getSequenceNumber());
					server.getRemoteHash().get(write_request.getSenderId()).getOutStream().writeObject(ack_msg);
					System.out.println("Sent " + ack_msg);

					if (server.getNumberOfUpdateAttempts() == server.getMaxNumberOfUpdateAttempts())
						server.getServerSocket().close();

					// if corresponding WRITE_REQUEST is the locking request, state for the data object of interest is switched to NOT_LOCKED
					if (d.getLockingRequest() != null && d.getLockingRequest().equals(write_request)) {
						d.setState(Data.State.NOT_LOCKED);
						d.setLockingRequest(null);

						if (!d.getRequestQueue().isEmpty()) {
							System.out.println("Head of the request queue was dequeued");
							AbstractMessage head = d.getRequestQueue().poll();
							head.setPriority(0);
							server.getMessageQueue().put(new FIFOEntry<AbstractMessage>(head));
						}

					}
					// if the corresponding request is pending in the queue remove it
					else if (d.getRequestQueue().remove(write_request)) {
						System.out.println("Removed request from the queue.");
					}
					// if it is not locking the object and not in the queue, then GRANT has already been sent. Do NOT do anything.
					else {
						System.out.println("Could not find the request!!!!! It should have already been granted");
					}
				}
			} else {
				System.out.println("This is a witdhraw message for ignored request, so ignoring this also.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return handled;
	}
}
