package client_server.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import client_server.FIFOEntry;

import remoteSite.RemoteSite;

import messages.AbstractMessage;
import messages.AbstractRequest;

/**
 * This is a client handler class. This thread is created for each connected client.
 */
public class ClientHandler implements Runnable {
	/**
	 * The server which communicates to the connected client
	 */
	private Server server;
	/**
	 *  The socket to the connected client
	 */
	private Socket sock;

	/**
	 * Constructor specifying the server and the socket to the connected client
	 * @param server the server which communicates to the connected client
	 * @param sock the socket to the conected client
	 */
	public ClientHandler(Server server, Socket sock) {
		this.server = server;
		this.sock = sock;
	}

	/**
	 * Reads messages from the socket and adds them to the priority queue. Messages in the priority queue are consumed/handled by a message consumer.
	 * @see MessageConsumer
	 */
	public void handleConnection() {

		boolean firstRequest = true;
		RemoteSite remote = new RemoteSite(sock.getInetAddress().getHostAddress(), sock.getPort());
		try {

			remote.setInStream(new ObjectInputStream(sock.getInputStream()));
			remote.setOutStream(new ObjectOutputStream(sock.getOutputStream()));
			server.getConnectedClients().add(remote);
			while (true) {
				AbstractMessage msg = (AbstractMessage) remote.getInStream().readObject();
				server.increaseNumberOfMessagesReceived();
				
				// if gets a request, saves it as the last request from the client
				if (msg instanceof AbstractRequest && firstRequest) {
					remote.setId(msg.getSenderId());
					server.getRemoteHash().put(remote.getId(), remote);
					firstRequest = false;
				}
				server.getMessageQueue().add(new FIFOEntry<AbstractMessage>(msg));
			}

		} catch (NumberFormatException e) {
			System.out.println("Number format exception occured.");
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found exception.");
		} finally {
			try {
				if (remote.getInStream() != null) {
					remote.getInStream().close();
				}
				if (remote.getOutStream() != null) {
					remote.getOutStream().close();
				}
				if (sock != null) {
					sock.close();
				}

			} catch (final IOException ex) {
				System.out.println("Could not close server socket");
				ex.printStackTrace();
			}
		}

	}

	public void run() {
		handleConnection();
	}

}
