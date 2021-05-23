package messageHandlers.client;

import helpers.CustomTimeUnit;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import client_server.client.ClientState.WaitState;

import remoteSite.RemoteSite;

import messages.AbstractMessage;
import messages.client.ReadCommit;
import messages.client.WriteCommit;
import messages.server.GrantMessage;

/**
 * Handler class for incoming GRANT message
 * 
 * @author <b>Emrah Cem</b>
 */
public class GrantMessageHandler extends ClientMessageHandler {

	@Override
	public boolean handleMessage(AbstractMessage message) {

		boolean handled = false;
		try {
			long receiveTime = System.currentTimeMillis();
			// received GRANT message
			GrantMessage grant_msg = (GrantMessage) message;

			if (!super.handleMessage(message) && client.getLastRequest().getSequenceNumber() == grant_msg.getSequenceNumber()) {

				if (client.getState().getWaitState() == WaitState.READ_WAIT) {
					// grant hase been received, save the time difference between the request issue time
					client.getState().getReadTimes().add(System.currentTimeMillis() - client.getSendTimeInMillis());

					client.getState().increaseSuccessfullRead();

					// inform getGrantTask about grant so that timeout does not happen
					synchronized (client.getGrantLock()) {
						client.getState().setGranted(true);
						client.getGrantLock().notify();
					}

					// waits for a period of time referred to as the HOLD_TIME
					Thread.sleep(CustomTimeUnit.getInstance().getTimeInMilliseconds(client.getHoldTime()));

					// sends a READ_COMMIT message to the granting server
					ReadCommit commit = new ReadCommit(client.getId(), client.getLastRequest().getSequenceNumber());
					System.out.println("Sent " + commit);
					((ObjectOutputStream) client.getRemoteHash().get(grant_msg.getSenderId()).getOutStream()).writeObject(commit);
					client.getState().increaseTotalNumberOfMessagesSent();

					// locally records the value received from the server
					if (client.getState().getReceivedValues().get(client.getLastRequest().getObjectIndex()) == null) {
						List<Integer> l = new ArrayList<Integer>();
						l.add(grant_msg.getValue());
						client.getState().getReceivedValues().put(client.getLastRequest().getObjectIndex(), l);
					} else {
						List<Integer> l = client.getState().getReceivedValues().get(client.getLastRequest().getObjectIndex());
						l.add(grant_msg.getValue());
					}

					client.getState().setWaitState(WaitState.NOT_WAIT);
					handled = true;
				}

				else if (client.getState().getWaitState() == WaitState.WRITE_WAIT) {

					// CHECK IF THE WRITE GRANT IS RECEIVED WITH THIS GRANT. If so, wait HOLD_TIME and then send WRITE_COMMIT to all servers.
					client.getRemoteHash().get(grant_msg.getSenderId()).setGrantReceived(true);
					Set<Integer> validSet = writeGrantReceived(client.getServers());
					client.setLockingServers(validSet);
					if (!validSet.isEmpty()) {
						client.getState().getWriteTimes().add(receiveTime - client.getSendTimeInMillis());
						System.out.println("RECEIVE time (for write)" + System.currentTimeMillis());

						// inform getGrantTask about grant so that timeout does not happen
						synchronized (client.getGrantLock()) {
							client.getState().setGranted(true);
							client.getGrantLock().notify();
						}

						// waits for a period of time referred to as the HOLD_TIME
						Thread.sleep(CustomTimeUnit.getInstance().getTimeInMilliseconds(client.getHoldTime()));

						// sends WRITE_COMMIT message to all servers
						Iterator<RemoteSite> itr = client.getServers().iterator();
						while (itr.hasNext()) {
							RemoteSite server = itr.next();
							WriteCommit commit = new WriteCommit(client.getId(), client.getLastRequest().getSequenceNumber(), !validSet.contains(server.getId()));
							server.getOutStream().writeObject(commit);
							client.getState().increaseTotalNumberOfMessagesSent();
							System.out.println("Sent " + commit + " to " + server.getId());
						}
						client.getState().setWaitState(WaitState.NOT_WAIT);
						handled = true;
					} else {
						client.getRemoteHash().get(grant_msg.getSenderId()).setGrantReceived(true);
					}

				} else if (client.getState().getWaitState() == WaitState.NOT_WAIT) {
					System.out.println("Client is not waiting for a GRANT message, ignore GRANT message!!!!");
				}

			} else {
				System.out.println("This is a grant message for the previous request, ignore it!!!");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return handled;
	}

	public Set<Integer> writeGrantReceived(List<RemoteSite> servers) {
		List<Set<Integer>> validGrantList = new ArrayList<Set<Integer>>();
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(1, 2, 4)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(1, 2, 5)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(1, 4, 5)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(1, 3, 6)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(1, 3, 7)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(1, 6, 7)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(2, 3, 4, 6)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(7, 4, 3, 2)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(2, 4, 6, 7)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(2, 5, 3, 6)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(2, 5, 3, 7)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(2, 5, 6, 7)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(4, 5, 3, 6)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(4, 5, 3, 7)));
		validGrantList.add(new TreeSet<Integer>(Arrays.asList(4, 5, 6, 7)));

		Set<Integer> granted = new TreeSet<Integer>();
		for (RemoteSite r : servers) {
			if (r.isGrantReceived())
				granted.add(r.getId());
		}

		System.out.print("Have following grants:[ ");
		for (RemoteSite server : client.getServers()) {
			if (server.isGrantReceived()) {
				System.out.print(server.getId() + " ");
			}
		}
		System.out.println("]");

		for (Set<Integer> valid : validGrantList) {
			if (granted.containsAll(valid))
				return valid;
		}
		granted.clear();

		return granted;
	}

}
