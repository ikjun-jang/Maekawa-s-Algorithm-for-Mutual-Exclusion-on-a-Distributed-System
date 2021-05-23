package messageHandlers.server;

import client_server.FIFOEntry;
import client_server.server.Data;

import messages.AbstractMessage;
import messages.AbstractRequest;
import messages.client.ReleaseLock;
import messages.client.WriteRequest;

/**
 * Handler class for RELEASE_LOCK message. RELEASE_LOCK message is used to let server know that it is safe to give the grant to the next request in the queue (if exists).
 * 
 * @author <b>Emrah Cem</b>
 */
public class ReleaseLockHandler extends ServerMessageHandler {

	/*
	 * (non-Javadoc)
	 * @see messageHandlers.AbstractMessageHandler#handleMessage(messages.AbstractMessage, java.io.OutputStream)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {
		// TODO Auto-generated method stub

		boolean handled = false;

		if (!super.handleMessage(message)) {
			ReleaseLock release_msg = (ReleaseLock) message;
			AbstractRequest request = (AbstractRequest) server.getRemoteHash().get(release_msg.getSenderId()).getLastRequest();

			// data object of interest
			Data<Integer> d = server.getData(request.getObjectIndex());

			if (request instanceof WriteRequest) {

				// corresponding request is a WRITE_REQUEST message
				WriteRequest write_request = (WriteRequest) request;

				if (d.getLockingRequest() != null && d.getLockingRequest().equals(write_request)) {
					d.setState(Data.State.NOT_LOCKED);
					d.setLockingRequest(null);
				} else {
					System.out.println("[UNREACHABLE]The server should be locked by " + write_request);
				}

				// if the data object is in NOT_LOCKED state, and there is a pending request, the server can grant
				// the request at the head of the queue of pending requests for the object
				if (d.getState() == Data.State.NOT_LOCKED && !d.getRequestQueue().isEmpty()) {
					System.out.println("Head of the request queue was dequeued");
					AbstractMessage head = d.getRequestQueue().poll();
					head.setPriority(0);
					server.getMessageQueue().put(new FIFOEntry<AbstractMessage>(head));
				}

			} else {
				System.out.println("[UNREACHABLE]RELEASE_LOCK will only come for WRITE_REQUEST");
			}

			handled = true;
		}

		return handled;

	}

}
