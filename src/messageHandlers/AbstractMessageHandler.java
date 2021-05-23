package messageHandlers;

import messages.AbstractMessage;


/**
 * Handler class that handles incoming messages
 * @author <b>Emrah Cem</b>
 */
public interface AbstractMessageHandler {
    
	public boolean handleMessage(AbstractMessage message);


}
