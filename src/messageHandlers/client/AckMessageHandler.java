package messageHandlers.client;

import java.io.IOException;
import java.io.ObjectOutputStream;

import messages.AbstractMessage;
import messages.MessageType;
import messages.client.ReleaseLock;
import messages.server.AckMessage;

/**
 * Handler class for incoming ACK message
 * 
 * @author <b>Emrah Cem</b>
 */
public class AckMessageHandler extends ClientMessageHandler {

	/*
	 * (non-Javadoc)
	 * @see messageHandlers.client.ClientMessageHandler#handleMessage(messages.AbstractMessage)
	 */
	@Override
	public boolean handleMessage(AbstractMessage message) {

		boolean handled = false;

		AckMessage ack_msg = (AckMessage) message;

		try {
			if (!super.handleMessage(message)) {
				if (client.getLastRequest().getType() == MessageType.WRITE_REQUEST && ack_msg.getSequenceNumber() == client.getLastRequest().getSequenceNumber()) {
					client.getAckedServerIds().add(ack_msg.getSenderId());

					// received ACK from all servers whose grant has caused to get a write grant
					if (client.getAckedServerIds().containsAll(client.getLockingServers())) {
						for (int serverId : client.getLockingServers()) {
							ReleaseLock release_msg = new ReleaseLock(client.getId(), ack_msg.getSequenceNumber());

							((ObjectOutputStream) client.getRemoteHash().get(serverId).getOutStream()).writeObject(release_msg);
							client.getState().increaseTotalNumberOfMessagesSent();
							System.out.println("Sent " + release_msg + " to server " + serverId);
						}
						client.getAckedServerIds().clear();
						client.getLockingServers().clear();

						client.setCanStartNextRound(true);
						client.getCanStartNextAccess().signal();
					}
				} else {
					System.out.println("Received an ACK for WITHDRAW, simply ignore.");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return handled;
	}
}