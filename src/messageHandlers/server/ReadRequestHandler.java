package messageHandlers.server;

import java.io.IOException;
import java.io.ObjectOutputStream;

import client_server.server.Data;

import messages.AbstractMessage;
import messages.client.ReadRequest;
import messages.server.GrantMessage;

/**
 * Handler class for incoming READ_REQUEST messages
 * 
 * @author <b>Emrah Cem</b>
 */
public class ReadRequestHandler extends ServerMessageHandler {

	/*
	 * (non-Javadoc)
	 * @see server.messageHandlers.AbstractMessageHandler#handleMessage(messages.AbstractMessage)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {
		boolean handled = false;

		try {
			if (!super.handleMessage(message) && server.getNumberOfUpdateAttempts() < server.getMaxNumberOfUpdateAttempts()) {
				//received READ_REQUEST message
				ReadRequest read_msg = (ReadRequest) message;
				//set to be the last request from the sender client
				server.getRemoteHash().get(message.getSenderId()).setLastRequest(read_msg);

				Data<Integer> data = server.getData(read_msg.getObjectIndex());

				//if data is in NOT_LOCKED state, send a GRANT message, change state to READ_LOCKED, set READ_LOCK_COUNT to 1.
				if (data.getState() == Data.State.NOT_LOCKED) {

					GrantMessage grant_msg = new GrantMessage(server.getId(), read_msg.getSequenceNumber(), data.getDataObject());
					((ObjectOutputStream) server.getRemoteHash().get(read_msg.getSenderId()).getOutStream()).writeObject(grant_msg);
					System.out.println("Sent " + grant_msg + " to client " + read_msg.getSenderId());

					data.setState(Data.State.READ_LOCKED);
					data.incrementReadLockCount();
					data.setLockingRequest(read_msg);

				} 
				//if data is in READ_LOCKED state, send a GRANT message, increment READ_LOCK_COUNT by 1.
				else if (data.getState() == Data.State.READ_LOCKED) {

					GrantMessage grant_msg = new GrantMessage(server.getId(), read_msg.getSequenceNumber(), data.getDataObject());
					((ObjectOutputStream) server.getRemoteHash().get(read_msg.getSenderId()).getOutStream()).writeObject(grant_msg);
					System.out.println("Sent " + grant_msg +" to client " + read_msg.getSenderId());
					data.incrementReadLockCount();

				} 
				//if data is in WRITE_LOCKED state, put the request into the queue
				else if (data.getState() == Data.State.WRITE_LOCKED) {
					data.getRequestQueue().add(read_msg);
					System.out.println("Request has been queued");
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
