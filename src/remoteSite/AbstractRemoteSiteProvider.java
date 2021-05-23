package remoteSite;

import java.util.List;

import exceptions.InvalidConfigFileException;

/**
 * RemoteSite provider is an object that determines how to get {@link RemoteSite} objects' ip addresses, listenining TCP ports, and ids.
 * 
 * @author <b>Emrah Cem</b>
 */

public abstract class AbstractRemoteSiteProvider {

	/**
	 * @return the list of servers
	 */
	public abstract List<RemoteSite> getServerInfo();

	/**
	 * Converts the string representation into a {@linkplain RemoteSite} object.
	 * 
	 * @param line the line that contains information about a server
	 * @return the {@linkplain RemoteSite} object with giiven information such as ip address, port, and id (optional).
	 * @throws InvalidConfigFileException if the line is not in correct form (ip:port [id])
	 */
	protected RemoteSite strToRemoteSite(String line) throws InvalidConfigFileException {
		RemoteSite site = null;

		String[] site_info = line.split("\\s+|:");
		if (site_info.length == 2) {
			site = new RemoteSite(site_info[0], Integer.parseInt(site_info[1]));
		} else {
			site = new RemoteSite(site_info[0], Integer.parseInt(site_info[1]));
			site.setId(Integer.parseInt(site_info[2]));
		}
		return site;
	}

}
