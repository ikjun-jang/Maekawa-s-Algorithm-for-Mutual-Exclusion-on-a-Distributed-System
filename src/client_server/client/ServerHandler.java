package client_server.client;

import java.io.IOException;

import client_server.FIFOEntry;

import remoteSite.RemoteSite;

import messages.AbstractMessage;

public class ServerHandler implements Runnable {

	/**
	 * The client which communicates to the server
	 */
	private Client client;
	/**
	 * The server which communicates to the client
	 */
	private RemoteSite server;

	/**
	 * Constructor specifying client and server
	 * @param client the client which communicates to the server
	 * @param server the server which communicates to the client
	 */
	public ServerHandler(Client client, RemoteSite server) {
		this.client = client;
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		// HANDLE MESSAGES COMING FROM SERVER

		AbstractMessage message;
		try {
			while (server.getSocket() != null) {
				message = (AbstractMessage) server.getInStream().readObject();
				client.increaseNumberOfMessagesReceived();
				client.getMessageQueue().put(new FIFOEntry<AbstractMessage>(message));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}
		client.getLock().lock();
		try {
			client.getLatch().countDown();
			System.out.print("Server " + server.getId() + " closed the connection");
			System.out.println("[Waiting for " + client.getLatch().getCount() + "more servers]");
			if (client.getLatch().getCount() == 0) {
				client.getCanStartNextAccess().signal();
			}
		} finally {
			client.getLock().unlock();
		}
	}

}
