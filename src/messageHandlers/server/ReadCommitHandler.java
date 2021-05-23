package messageHandlers.server;

import client_server.FIFOEntry;
import client_server.server.Data;

import messages.AbstractMessage;
import messages.client.ReadCommit;
import messages.client.ReadRequest;

/**
 * Handler class for incoming READ_COMMIT messages
 * 
 * @author <b>Emrah Cem</b>
 */
public class ReadCommitHandler extends ServerMessageHandler {

	/*
	 * (non-Javadoc)
	 * @see messageHandlers.server.ServerMessageHandler#handleMessage(messages.AbstractMessage)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {
		boolean handled = false;

		if (!super.handleMessage(message)) {
			// received read commit
			ReadCommit read_commit = (ReadCommit) message;

			// corresponding read request
			ReadRequest read_request = (ReadRequest) server.getRemoteHash().get(read_commit.getSenderId()).getLastRequest();

			Data<Integer> d = server.getData(read_request.getObjectIndex());
			d.decrementReadLockCount();

			// if READ_LOCK_COUNT is 0 change state to NOT_LOCKED
			if (d.getReadLockCount() == 0) {
				d.setState(Data.State.NOT_LOCKED);
				d.setLockingRequest(null);

				// get the request at the top of the queue to send a grant
				if (!d.getRequestQueue().isEmpty()) {
					System.out.println("Head of the request queue was dequeued");
					AbstractMessage head = d.getRequestQueue().poll();
					head.setPriority(0);
					server.getMessageQueue().put(new FIFOEntry<AbstractMessage>(head));
				}
			}
		}

		return handled;
	}

}