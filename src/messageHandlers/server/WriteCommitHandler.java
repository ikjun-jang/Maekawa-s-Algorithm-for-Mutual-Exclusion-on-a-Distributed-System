package messageHandlers.server;

import java.io.IOException;

import client_server.FIFOEntry;
import client_server.server.Data;

import messages.AbstractMessage;
import messages.client.WriteCommit;
import messages.client.WriteRequest;
import messages.server.AckMessage;

/**
 * Handler clas for WRITE_COMMIT messages
 * 
 * @author <b>Emrah Cem</b>
 */
public class WriteCommitHandler extends ServerMessageHandler {

	/*
	 * (non-Javadoc)
	 * @see messageHandlers.server.ServerMessageHandler#handleMessage(messages.AbstractMessage)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {
		boolean handled = false;

		try {

			if (!super.handleMessage(message) && server.getNumberOfUpdateAttempts() < server.getMaxNumberOfUpdateAttempts()) {
				WriteCommit write_commit = (WriteCommit) message;

				// Corresponding request (from the senderId, the client object is found and its lastRequest message is retreived)
				WriteRequest write_request = (WriteRequest) server.getRemoteHash().get(write_commit.getSenderId()).getLastRequest();

				// update the data object
				Data<Integer> d = server.getData(write_request.getObjectIndex());
				d.setDataObject(d.getDataObject() + write_request.getV());
				System.out.println("D" + write_request.getObjectIndex() + " was updated to " + (d.getDataObject()));

				if (server.getLogFileWriter() != null)
					server.getLogFileWriter().write("D" + write_request.getObjectIndex() + " --> " + d.getDataObject() + " (by client "+write_commit.getSenderId() +")\r\n");

				server.increaseNumberOfUpdateAttempts();
				System.out.println("NUMBER OF WRITE ATTEMPTS IS INCREASED TO " + server.getNumberOfUpdateAttempts());

				// send ACK message
				AckMessage ack_msg = new AckMessage(server.getId(), write_request.getSequenceNumber());
				server.getRemoteHash().get(write_commit.getSenderId()).getOutStream().writeObject(ack_msg);
				System.out.println("Sent " + ack_msg);

				if (server.getNumberOfUpdateAttempts() == server.getMaxNumberOfUpdateAttempts())
					server.getServerSocket().close();

				if (d.getLockingRequest() != null && d.getLockingRequest().equals(write_request)) {

					if (write_commit.isReleaseLock()) {
						// Server can remove the lock
						d.setState(Data.State.NOT_LOCKED);
						d.setLockingRequest(null);

						if (!d.getRequestQueue().isEmpty()) {
							System.out.println("Head of the request queue was dequeued");
							AbstractMessage head = d.getRequestQueue().poll();
							head.setPriority(0);
							server.getMessageQueue().put(new FIFOEntry<AbstractMessage>(head));
						}

					} else {
						// Do not remove the lock until getting a RELEASE LOCK message from the client
						System.out.println("Does not remove the lock. Waiting for RELEASE_LOCK");
					}

				} else if (d.getRequestQueue().remove(write_request)) {
					System.out.println("Removed request from the queue");
				} else {
					System.out.println("Could not find the request!!!!! It should have already been granted");
				}

			} else {
				System.out.println("Limit reached , this commit is ignored.");
			}

			handled = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return handled;
	}

}
