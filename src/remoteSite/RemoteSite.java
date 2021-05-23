package remoteSite;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import messages.AbstractRequest;

/**
 * A class for representing a remote machine
 * 
 * @author <b>Emrah Cem</b>
 */
/**
 * @author <b>Emrah Cem</b>
 */
/**
 * @author <b>Emrah Cem</b>
 */
public class RemoteSite {

	/**
	 * The ip address of this site
	 * 
	 * @see #getIpAddress()
	 */
	private String ipAddress;

	/**
	 * The port number of this site
	 * 
	 * @see #getPort()
	 */
	private int port;

	/**
	 * The {@linkplain OutputStream} to this site
	 * 
	 * @see #getOutStream()
	 */
	private ObjectOutputStream outStream;

	/**
	 * The {@linkplain InputStream} to this site
	 * 
	 * @see #getInStream()
	 */
	private ObjectInputStream inStream;

	/**
	 * The socket to this site
	 * 
	 * @see #getSocket()
	 */
	private Socket socket;

	/**
	 * The id of this site
	 * 
	 * @see #getId()
	 */
	private int id = -1;

	/**
	 * The state keeping whether a grant is received from this site
	 */
	private boolean grantReceived;

	/**
	 * The last request received from this site
	 */
	private AbstractRequest lastRequest;

	/**
	 * Constructor specifying the ip address and port number of a site
	 * 
	 * @param ipAddress the ip address of the site
	 * @param port the port number of the site
	 */
	public RemoteSite(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public AbstractRequest getLastRequest() {
		return lastRequest;
	}

	public void setLastRequest(AbstractRequest lastRequest) {
		this.lastRequest = lastRequest;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectOutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(ObjectOutputStream outStream) {
		this.outStream = outStream;
	}

	public ObjectInputStream getInStream() {
		return inStream;
	}

	public void setInStream(ObjectInputStream inStream) {
		this.inStream = inStream;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isGrantReceived() {
		return grantReceived;
	}

	public void setGrantReceived(boolean grantReceived) {
		this.grantReceived = grantReceived;
	}

	/* 
	 * Returns the string representation of a remote machine : id :( ip address, port ) 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return id + ":(" + ipAddress + ":" + port + ")";

	}

}
