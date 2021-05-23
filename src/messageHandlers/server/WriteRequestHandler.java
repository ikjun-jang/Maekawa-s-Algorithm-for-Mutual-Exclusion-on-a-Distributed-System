package messageHandlers.server;

import java.io.IOException;

import client_server.server.Data;

import messages.AbstractMessage;
import messages.client.WriteRequest;
import messages.server.GrantMessage;

/**
 * Handler class for WRITE_REQUEST messages
 * 
 * @author <b>Emrah Cem</b>
 */
public class WriteRequestHandler extends ServerMessageHandler {

	/*
	 * (non-Javadoc)
	 * @see messageHandlers.server.ServerMessageHandler#handleMessage(messages.AbstractMessage)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {
		boolean handled = false;

		try {
			if (!super.handleMessage(message) && server.getNumberOfUpdateAttempts() < server.getMaxNumberOfUpdateAttempts()) {

				// received WRITE_REQUEST message
				WriteRequest write_req = (WriteRequest) message;

				// set this message as the last message from the sender of this client
				server.getRemoteHash().get(message.getSenderId()).setLastRequest(write_req);

				Data<Integer> data = server.getData(write_req.getObjectIndex());

				// if the data is in NOT_LOCKED state, send the GRANT message, change state to WRITE_LOCKED
				if (data.getState() == Data.State.NOT_LOCKED) {

					// send the GRANT message
					GrantMessage grant_msg = new GrantMessage(server.getId(), write_req.getSequenceNumber(), -1);
					server.getRemoteHash().get(write_req.getSenderId()).getOutStream().writeObject(grant_msg);
					System.out.println("Sent " + grant_msg);

					data.setState(Data.State.WRITE_LOCKED);
					data.setLockingRequest(write_req);

				}
				// if the data is in READ_LOCKED state, put the request into the queue
				else if (data.getState() == Data.State.READ_LOCKED) {

					data.getRequestQueue().add(write_req);
					System.out.println("Request was added to the queue");
				}
				// if the data is in WRITE_LOCKED state, put the request into the queue
				else if (data.getState() == Data.State.WRITE_LOCKED) {

					data.getRequestQueue().add(write_req);
					System.out.println("Request was added to the queue");

				}
			} else {
				System.out.println("Ignore the request, maximum number of requests has been reached.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return handled;
	}
}
