package messageHandlers.server;

import client_server.server.Server;
import messageHandlers.AbstractMessageHandler;
import messages.AbstractMessage;

public abstract class ServerMessageHandler implements AbstractMessageHandler{

	Server server;
	
	public boolean handleMessage(AbstractMessage message){
		return false;
	}

	public void setServer(Server server) {
		this.server = server;
	}

}
