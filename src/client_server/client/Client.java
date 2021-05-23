package client_server.client;

import helpers.CustomTimeUnit;
import helpers.MyRandom;
import helpers.TimeRange;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;
import java.net.UnknownHostException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;

import client_server.MessageConsumer;
import client_server.MessageReceiver;
import client_server.client.ClientState.WaitState;

import messageHandlerFactory.ClientMessageHandlerFactory;
import messages.AbstractRequest;
import messages.MessageType;
import messages.client.ReadRequest;
import messages.client.WithdrawMessage;
import messages.client.WriteRequest;

import remoteSite.AbstractRemoteSiteProvider;
import remoteSite.RemoteSite;
import remoteSite.RemoteSiteFromFile;

import request.factories.AbstractRequestFactory;
import request.factories.ProjectRequestFactory;
import request.serverSelector.AbstractServerSelector;
import request.serverSelector.ProjectServerSelector;

/**
 * @author <b>Emrah Cem</b>
 */
public class Client extends MessageReceiver {

	/**
	 * Number of data objects on the server
	 * 
	 * @see #getNumberOfDataObjects()
	 */
	private int numberOfDataObjects = 4;
	/**
	 * The amount of time to keep the grant for a request
	 * 
	 * @see #getHoldTime()
	 * @see AbstractRequest
	 */
	private double holdTime = 1;
	/**
	 * The amount of time to wait for grant. If grant is not received in awaitingGrant time units, then timeout occurs.
	 */
	private double awaitingGrant = 20;
	/**
	 * Time unit used in computing holdTime, awaitingGrant, and time between successive data access operations
	 * 
	 * @see CustomTimeUnit
	 */
	private CustomTimeUnit timeUnit;
	/**
	 * Time range between successive data access operations
	 * 
	 * @see TimeRange
	 */
	private TimeRange waitTimeRange;
	/**
	 * State of the client.(also keeps statistics such as number of successful read operation, write operation, and so on.
	 */
	private ClientState state;
	/**
	 * List of servers that keep the data objects
	 */
	private List<RemoteSite> servers;
	/**
	 * Mapping of server id to RemoteSite object which keeps the details of a remote computer such as socket, streams, id, and so on.
	 */
	private HashMap<Integer, RemoteSite> remoteHash;
	/**
	 * The ids of servers whose grant has been received for the last write request
	 */
	private Set<Integer> lockingServerIds;
	/**
	 * The ids of servers whose ACK has been received for the last write request
	 */
	private Set<Integer> ackedServerIds;
	/**
	 * Factory that creates requests for the client
	 * 
	 * @see AbstractRequestFactory
	 */
	private AbstractRequestFactory requestFactory;
	/**
	 * Provider that determines how to get information about servers. For instance, from a file, from console.
	 * 
	 * @see AbstractRemoteSiteProvider
	 */
	private AbstractRemoteSiteProvider serverInfoProvider;

	/**
	 * Selector that determines to which server to send a given request.
	 * 
	 * @see AbstractServerSelector
	 */
	private AbstractServerSelector serverSelector;
	/**
	 * Random number generator
	 * 
	 * @see MyRandom
	 */
	private MyRandom rand;
	/**
	 * Last request sent by this client
	 * 
	 * @see AbstractRequest
	 */
	private AbstractRequest lastRequest;
	/**
	 * Control variable to determine that all servers closed the connection
	 */
	private CountDownLatch latch;
	/**
	 * this client can end this access operation and start the next access operation
	 */
	private boolean canStartNextRound;

	private final Condition canStartNextAccess = lock.newCondition();

	/**
	 * The time difference between the midnight and the send event of last request (in milliseconds)
	 */
	protected long sendTimeInMillis;

	private Object grantLock;

	/**
	 * Constructor specifying the id of this client
	 * 
	 * @param id the id of this client
	 */
	public Client(int id) {
		this(id, 1);
	}

	/**
	 * Constructor specifying the id of this client and the seed used in random number generation
	 * 
	 * @param id the id of this client
	 * @param seed the seed used in random number generation
	 */
	public Client(int id, int seed) {
		this(id, seed, 1.0);
	}

	/**
	 * Constructor specifying the id of this client, the seed used in random number generation, and holdTime
	 * 
	 * @param id the id of this client
	 * @param seed the seed used in random number generation
	 * @param holdTime the amount of time to keep the grant for a request
	 * 
	 */
	public Client(int id, int seed, double holdTime) {
		this(id, seed, holdTime, 1);
	}

	/**
	 * Constructor specifying the id of this client, the seed used in random number generation, holdTime, and the multiplier of the timeUnit
	 * 
	 * @param id the id of this client
	 * @param seed the seed used in random number generation
	 * @param holdTime the amount of time to keep the grant for a request
	 * @param amount multiplier of the timeUnit to create custom time units
	 */
	public Client(int id, int seed, double holdTime, long amount) {
		this(id, seed, holdTime, amount, "servers.config");
	}

	/**
	 * Constructor specifying the id of this client, the seed used in random number generation, holdTime, the multiplier of the timeUnit, and the path to config file
	 * 
	 * @param id the id of this client
	 * @param seed the seed used in random number generation
	 * @param holdTime the amount of time to keep the grant for a request
	 * @param amount multiplier of the timeUnit to create custom time units
	 * @param pathToConfigFile the path to config file which keeps information about servers
	 */
	public Client(int id, int seed, double holdTime, long amount, String pathToConfigFile) {
		super(id);
		this.state = new ClientState();

		this.timeUnit = CustomTimeUnit.getInstance();
		this.timeUnit.setUnit(amount, TimeUnit.MILLISECONDS);
		this.waitTimeRange = new TimeRange(5, 10);
		this.holdTime = holdTime;
		rand = MyRandom.getInstance();
		rand.setSeed(seed);

		this.requestFactory = new ProjectRequestFactory(this.id);
		this.serverInfoProvider = new RemoteSiteFromFile(pathToConfigFile);
		this.servers = serverInfoProvider.getServerInfo();
		this.remoteHash = new HashMap<Integer, RemoteSite>();
		this.lockingServerIds = new TreeSet<Integer>();
		this.ackedServerIds = new TreeSet<Integer>();
		this.latch = new CountDownLatch(servers.size());
		for (RemoteSite remote : servers) {
			remoteHash.put(remote.getId(), remote);
		}

		this.grantLock = new Object();

		this.serverSelector = new ProjectServerSelector(servers);
		this.messageHandlerFactory = new ClientMessageHandlerFactory(this);
	}

	/**
	 * Creates connections to servers. If connections to all servers are successful, then creates a MessageConsumer to handle incoming messages and waits for a trigger from the server with id 1. After getting the trigger, starts sending requests.
	 */
	public void runClient() {
		if (createTCPConnectionsToServers()) {
			// start the message consumer which handles the incoming messages
			new Thread(new MessageConsumer(this, messageQueue)).start();
			waitForATrigger();
			sendRequests();
		} else {
			System.out.println("Could not create TCP connections to all servers. Terminating the program.");
		}
	}

	/**
	 * Waits for an arbitrary input from the main server.
	 */
	private void waitForATrigger() {
		try {
			System.out.println("Waiting for a trigger...");
			servers.get(0).getInStream().readObject();

			for (RemoteSite server : servers)
				new Thread(new ServerHandler(this, server)).start();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates TCP socket to each server and sets socket, input, and output streams.
	 */
	private boolean createTCPConnectionsToServers() {
		boolean successful = true;
		System.out.println("TCP connections to servers are being created.");
		for (RemoteSite server : servers) {
			try {
				System.out.print("Connection to server " + server.getId() + "(" + server.getIpAddress() + ":" + server.getPort() + "):");
				// create a TCP socket to the server
				Socket sock = new Socket(server.getIpAddress(), server.getPort());

				// create input and output streams
				server.setSocket(sock);
				server.setOutStream(new ObjectOutputStream(sock.getOutputStream()));
				server.setInStream(new ObjectInputStream(sock.getInputStream()));

				System.out.println("[Successful]");
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("[Unsuccessful]");
				successful = false;
				// e.printStackTrace();
			}
		}

		return successful;

	}

	/**
	 * Resets the state of granted to <code>False</code>, waitState to {@linkplain WaitState.NOT_WAIT}, grantReceived to <code>False</code> for all servers.
	 * 
	 */
	private void resetState() {
		getState().setGranted(false);
		getState().setWaitState(WaitState.NOT_WAIT);
		Iterator<RemoteSite> itr = servers.iterator();
		while (itr.hasNext()) {
			RemoteSite server = itr.next();
			server.setGrantReceived(false);
		}

		Set<Integer> granted = new TreeSet<Integer>();
		for (RemoteSite r : servers) {
			if (r.isGrantReceived())
				granted.add(r.getId());
		}
	}

	/**
	 * Sends requests to servers until an exception occurs (either because server closes a connection or interrupt occurs)
	 */
	private void sendRequests() {
		try {
			System.out.println("Start sending requests.");
			AbstractRequest request = null;
			List<RemoteSite> serversToSend = null;
			ExecutorService executor = null;
			Future<Boolean> future = null;
			executor = Executors.newSingleThreadExecutor();
			while (true) {

				lock.lock();
				try {
					// Reset the state for the next request
					resetState();

					// create a request based on the requirements
					request = requestFactory.createRequest();

					// set the sequence number of the last request so that received messages can be matched with requests
					setLastRequest(request);

					// select servers to send the request
					serversToSend = serverSelector.selectServers(servers, request);
				} finally {
					lock.unlock();
				}

				// The time between the completion (successful or unsuccessful) of the previous access and the next access
				// attempted by a client is uniformly distributed in the range "waitTimeRange" time units
				int sleepTime = waitTimeRange.getLower_bound() + MyRandom.getInstance().nextInt(1 + waitTimeRange.getUpper_bound() - waitTimeRange.getLower_bound());
				Thread.sleep(timeUnit.getTimeInMilliseconds(sleepTime));

				// sends request to serversToSend
				lock.lock();
				try {

					sendRequest(request, serversToSend);
					setSendTimeInMillis(System.currentTimeMillis());
					System.out.println("SEND time :" + System.currentTimeMillis());
					future = executor.submit(new GetGrantTask());
					System.out.println("Timer started:" + getAwaitingGrant() + " [" + timeUnit + "]");
				} finally {
					lock.unlock();
				}

				try {
					// wait for future result for at most awaitingGrant timeunit
					// if result is not ready, then timeout occurs
					future.get((long) (getAwaitingGrant() * timeUnit.getAmount()), timeUnit.getTimeUnit());

					// if grant is received for WRITE_REQUEST
					if (request.getType() == MessageType.WRITE_REQUEST) {
						// wait until all acks have been received
						lock.lock();
						System.out.println("Waiting to get all ACKS");
						while (!canStartNextRound) {
							canStartNextAccess.await();
						}
						// while waiting for ACKs servers may close the connection
						if (getLatch().getCount() != 0) {
							canStartNextRound = false;
							System.out.println("Received all ACKS.");
							System.out.println("Succesfull write has been increased to" + getState().getSuccessfullWrite());
							state.increaseSuccessfullWrite();
						}
						lock.unlock();
					}

				} catch (TimeoutException e) {

					lock.lock();
					try {
						System.out.println("Timeout occured!");
						if (getState().isGranted()) {
							System.out.println("Ignore timeout.");
						} else {
							future.cancel(true);
							Iterator<RemoteSite> itr = serversToSend.iterator();
							itr = serversToSend.iterator();

							// send WTIHDRAW message to all servers
							WithdrawMessage withdraw_msg = new WithdrawMessage(id, getLastRequest().getSequenceNumber());
							while (itr.hasNext()) {
								RemoteSite server = itr.next();
								server.getOutStream().writeObject(withdraw_msg);
								state.increaseTotalNumberOfMessagesSent();
							}

							System.out.println("Sent " + withdraw_msg + " to all servers");

							// update statistics
							if (request instanceof ReadRequest) {
								getState().increaseUnsuccessfullRead();
								System.out.println("Unsuccesfull read has been increased to" + getState().getUnsuccessfullRead());
							} else if (request instanceof WriteRequest) {
								getState().increaseUnsuccessfullWrite();
								System.out.println("Unsuccesfull write has been increased to" + getState().getUnsuccessfullWrite());
							}
							getState().setWaitState(WaitState.NOT_WAIT);
							getState().setGranted(false);
						}
					} finally {
						lock.unlock();
					}

				} catch (ExecutionException e) {
					e.printStackTrace();
				}

			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
		}

		// wait for all servers to close connection
		try {
			getLatch().await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Report the statistics
		lock.lock();
		try {
			System.out.println(state);
			System.out.println("Number of messages received:" + getNumberOfMessagesReceived());
			System.out.println("Number of messages exchanged (total):" + (getNumberOfMessagesReceived() + state.getTotalNumberOfMessagesSent()));
			System.out.println("Bye...");
			System.exit(0);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Sends a request to server(s)
	 * 
	 * @throws IOException if server had already closed the connection
	 */
	private void sendRequest(AbstractRequest request, List<RemoteSite> serversToSend) throws IOException {
		Iterator<RemoteSite> itr = serversToSend.iterator();

		// send request
		while (itr.hasNext()) {
			RemoteSite server = itr.next();
			server.getOutStream().writeObject(request);
			state.increaseTotalNumberOfMessagesSent();
		}

		// change waitState
		if (request instanceof ReadRequest) {
			state.setWaitState(WaitState.READ_WAIT);
			System.out.println("Sent " + request + " to server" + serversToSend.get(0));
		} else if (request instanceof WriteRequest) {
			state.setWaitState(WaitState.WRITE_WAIT);
			System.out.println("Sent " + request + " to all servers");
		}
	}

	/**
	 * Class that waits until grant is received. This class is used for timeout mechanism. If call method finishes on time timeout does not occur.
	 * 
	 * @author <b>Emrah Cem</b>
	 */
	class GetGrantTask implements Callable<Boolean> {

		/*
		 * (non-Javadoc)
		 * @see java.util.concurrent.Callable#call()
		 */
		// @Override
		public Boolean call() throws Exception {

			synchronized (grantLock) {
				try {
					while (!getState().isGranted()) {
						grantLock.wait();
					}
				} catch (InterruptedException e) {

				}
			}

			return true;
		}
	}

	public Condition getCanStartNextAccess() {
		return canStartNextAccess;
	}

	public long getSendTimeInMillis() {
		return sendTimeInMillis;
	}

	public void setSendTimeInMillis(long sendTimeInMillis) {
		this.sendTimeInMillis = sendTimeInMillis;
	}

	public boolean isCanStartNextRound() {
		return canStartNextRound;
	}

	public void setCanStartNextRound(boolean canStartNextRound) {
		this.canStartNextRound = canStartNextRound;
	}

	public CountDownLatch getLatch() {
		return latch;
	}

	public void setLatch(CountDownLatch latch) {
		this.latch = latch;
	}

	public AbstractRequest getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(AbstractRequest lastRequest) {
		this.lastRequest = lastRequest;
	}

	public HashMap<Integer, RemoteSite> getRemoteHash() {
		return remoteHash;
	}

	public int getNumberOfDataObjects() {
		return numberOfDataObjects;
	}

	public void setNumberOfDataObjects(int numberOfDataObjects) {
		this.numberOfDataObjects = numberOfDataObjects;
	}

	public AbstractRequestFactory getRequestFactory() {
		return requestFactory;
	}

	public void setRequestFactory(AbstractRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public AbstractRemoteSiteProvider getServerInfoProvider() {
		return serverInfoProvider;
	}

	public void setServerInfoProvider(AbstractRemoteSiteProvider serverInfoProvider) {
		this.serverInfoProvider = serverInfoProvider;
	}

	public CustomTimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(CustomTimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public double getHoldTime() {
		return holdTime;
	}

	public void setHoldTime(double holdTime) {
		this.holdTime = holdTime;
	}

	public double getAwaitingGrant() {
		return awaitingGrant;
	}

	public void setAwaitingGrant(double awaitingGrant) {
		this.awaitingGrant = awaitingGrant;
	}

	public Object getGrantLock() {
		return grantLock;
	}

	public void setGrantLock(Object grantLock) {
		this.grantLock = grantLock;
	}

	public ClientState getState() {
		return state;
	}

	public void setState(ClientState state) {
		this.state = state;
	}

	public List<RemoteSite> getServers() {
		return servers;
	}

	public TimeRange getWaitTimeRange() {
		return waitTimeRange;
	}

	public void setWaitTimeRange(TimeRange waitTimeRange) {
		this.waitTimeRange = waitTimeRange;
	}

	public Set<Integer> getLockingServers() {
		return lockingServerIds;
	}

	public void setLockingServers(Set<Integer> lockingServerIds) {
		this.lockingServerIds = lockingServerIds;
	}

	public Set<Integer> getAckedServerIds() {
		return ackedServerIds;
	}

	public void setAckedServerIds(Set<Integer> ackedServerIds) {
		this.ackedServerIds = ackedServerIds;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = "";

		return str;

	}

}
