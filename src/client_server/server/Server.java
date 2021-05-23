package client_server.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;

import client_server.MessageConsumer;
import client_server.MessageReceiver;

import remoteSite.RemoteSite;

import messageHandlerFactory.AbstractMessageHandlerFactory;
import messageHandlerFactory.ServerMessageHandlerFactory;
import messages.server.TriggerMessage;

/**
 * Server class for the tree-based voting protocol for replica consistency. Let there be seven servers, S(1), S(2), ..., S(7) that maintain replicas of data objects. The seven servers are logically arranged as a balanced binary tree with S(1) being the root, and servers S(2i) and S(2i+1) being the
 * left and right children, respectively, of server S(i). There are four data objects, D(0), D(1), D(2), and D(3) of type integer. Each server maintains copies of the four data objects. Initially, all the replicas of an object are consistent, with D(j) initialized to j, where j is between 0 and 3
 * (inclusive). All communication channels are FIFO.
 * 
 * @author <b>Emrah Cem</b>
 */

public class Server extends MessageReceiver {
	private ServerSocket serverSocket;
	private ArrayList<RemoteSite> connectedClients;
	private HashMap<Integer, RemoteSite> remoteHash;

	/**
	 * The port at which the server will accept requests
	 * 
	 * @see #getPort()
	 */
	private int port;

	/**
	 * Current number of updateAttempts to all data objects
	 * 
	 * @see #getNumberOfUpdateAttempts()
	 */
	private int numberOfUpdateAttempts = 0;

	/**
	 * The array that keeps data objects
	 * 
	 * @see #getData(int);
	 */
	private Data<Integer>[] data;

	/**
	 * Maximum number of update attempts to data objects (total of updates to each data object)
	 * 
	 * @see #getMaxNumberOfUpdateAttempts()
	 */
	private int maxNumberOfUpdateAttempts = 50;

	private FileWriter logFileWriter;

	/**
	 * Class constructor specifying server id
	 * 
	 * @param id the server id
	 */
	public Server(int id) {
		this(id, 4);
	}

	/**
	 * Class constructor specifying server id and number of data objects
	 * 
	 * @param id the server id
	 * @param numOfDataObjects the number of data objects
	 */
	public Server(int id, int port) {
		this(id, port, 4);
	}

	/**
	 * Class constructor specifying server id, number of data objects, and listening TCP port number
	 * 
	 * @param id the server id
	 * @param numOfDataObjects the number of data objects
	 * @param port the port at which the server listens for TCP connections
	 */
	@SuppressWarnings("unchecked")
	public Server(int id, int port, int numOfDataObjects) {
		super(id);
		this.data = ((Data<Integer>[]) new Data[numOfDataObjects]);
		for (int i = 0; i < numOfDataObjects; i++)
			data[i] = new Data<Integer>(i);

		this.port = port;
		this.remoteHash = new HashMap<Integer, RemoteSite>();
		this.connectedClients = new ArrayList<RemoteSite>();
		this.messageHandlerFactory = new ServerMessageHandlerFactory(this);
		createLogFile(id + ".log");
	}

	private void createLogFile(String fileName) {
		try {
			this.logFileWriter = new FileWriter(new File(id + ".log"));
		} catch (IOException e) {
			System.err.println("Log file could not be created.");
		}

	}

	/**
	 * Initiates the server. More specifically, creates server socket, starts the {@link MessageConsumer} thread (and {@link TriggerRequestSend} thread if server {@link #id} is <code>1</code>. Lastly, starts listening for incoming messages. )
	 */
	public void runServer() {

		try {

			createServerSocket(port);

			// add a shutdownhook in case of Ctrl+c is entered in the middle of the program execution.
			handleServerSocketClosing();

			// start the message consumer which handles the incoming messages
			new Thread(new MessageConsumer(this, messageQueue)).start();

			// send trigger to clients
			if (id == 1) {
				Thread th = new TriggerRequestSend(id);
				th.start();
			}

			// starts listening and creates a handling thread for each connected client
			while (true) {
				Socket socket = serverSocket.accept();
				ClientHandler handler = new ClientHandler(this, socket);
				new Thread(handler).start(); // create a thread to handle the client
			}

		} catch (IOException e) {
			System.exit(0);
		}
	}

	/**
	 * Checks if all the connected {@link Client} are handled and @see {@link #maxNumberOfUpdateAttempts} is reached.
	 * 
	 * @return whether all the connected {@link Client} are handled and @see {@link #maxNumberOfUpdateAttempts} is reached.
	 */
	public boolean allFinished() {
		boolean result = true;
		result &= getNumberOfUpdateAttempts() == getMaxNumberOfUpdateAttempts();
		for (Data<Integer> d : data) {
			result &= d.getLockingRequest() == null;
			result &= d.getRequestQueue().isEmpty();
		}
		return result;
	}

	/**
	 * @param port the port at which this server is listening for TCP connections
	 * @throws IOException if socket can not be opened
	 */
	private void createServerSocket(int port) throws IOException {
		this.serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(port));
		System.out.println("Server is up and waiting for connections on port " + port);
	}

	/**
	 * Handles closing the server socket(e.g. when Ctrl+c is entered, or System.exit() is called
	 */
	private void handleServerSocketClosing() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

				try {
					if (serverSocket != null) {
						serverSocket.close();
						System.out.println("\nClosed server socket.");
					}
					if(logFileWriter !=null){
						logFileWriter.close();
						System.out.println("Closed log file.");
					}
				} catch (IOException e1) {
					System.out.println("Exception occured when closing the socket");
				}
			}
		});
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getNumberOfUpdateAttempts() {
		return numberOfUpdateAttempts;
	}

	public void setNumberOfUpdateAttempts(int numberOfUpdateAttempts) {
		this.numberOfUpdateAttempts = numberOfUpdateAttempts;
	}

	public void increaseNumberOfUpdateAttempts() {
		this.numberOfUpdateAttempts += 1;
	}

	public HashMap<Integer, RemoteSite> getRemoteHash() {
		return remoteHash;
	}

	public Data<Integer> getData(int index) {
		return data[index];
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public ArrayList<RemoteSite> getConnectedClients() {
		return connectedClients;
	}

	public int getMaxNumberOfUpdateAttempts() {
		return maxNumberOfUpdateAttempts;
	}

	public void setMaxNumberOfUpdateAttempts(int maxNumberOfUpdateAttempts) {
		this.maxNumberOfUpdateAttempts = maxNumberOfUpdateAttempts;
	}

	public AbstractMessageHandlerFactory getMessageHandlerFactory() {
		return messageHandlerFactory;
	}

	public void setMessageHandlerFactory(AbstractMessageHandlerFactory messageHandlerFactory) {
		this.messageHandlerFactory = messageHandlerFactory;
	}

	public FileWriter getLogFileWriter() {
		return logFileWriter;
	}

	public void setLogFileWriter(FileWriter logFileWriter) {
		this.logFileWriter = logFileWriter;
	}

	/*
	 * Prints the state of the server by writing the state of data objects
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		String str = "";

		int i = 0;
		for (Data<Integer> d : data) {
			if (d.getReadLockCount() < 0) {
				System.out.println("Impossible");
				System.exit(0);
			}
			str += "D" + i + d.toString() + "\n";
			i++;
		}
		return str;
	}

	/**
	 * A thread that waits for user input and sends a {@link TriggerMessage} to all connected clients.
	 * 
	 * @author Emrah Cem
	 */
	class TriggerRequestSend extends Thread {

		private int serverId;

		/**
		 * @param serverId the id of the server who sends the trigger
		 */
		public TriggerRequestSend(int serverId) {
			this.serverId = serverId;
		}

		public void run() {
			try {
				System.out.println("Please enter any key to send a trigger to clients");
				BufferedInputStream input = new BufferedInputStream(System.in);
				input.read();

				ArrayList<RemoteSite> connectedClients = getConnectedClients();
				for (RemoteSite r : connectedClients) {
					r.getOutStream().writeObject(new TriggerMessage(serverId, -1));
				}

				Thread.sleep(1000);

			} catch (InterruptedIOException exc) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				e.printStackTrace();
				if (!isInterrupted()) {
					e.printStackTrace();
				} else {
					System.out.println("Interrupted");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
