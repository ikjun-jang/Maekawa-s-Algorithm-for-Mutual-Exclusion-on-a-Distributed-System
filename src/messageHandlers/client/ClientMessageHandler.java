package messageHandlers.client;

import client_server.client.Client;

import messageHandlers.AbstractMessageHandler;
import messages.AbstractMessage;

/**
 * Message handler for the client 
 * @author <b>Emrah Cem</b>
 */
public abstract class ClientMessageHandler implements AbstractMessageHandler{

	protected Client client;
	protected AbstractMessage message;
	
	public boolean handleMessage(AbstractMessage message){
		return false;
	}
	
	public void setClient(Client client) {
		this.client = client;
	}
}
