package client_server.client;

public class Client_main {

	public static void main(String[] args) {
		try {
			Client client = null;

			if (args.length == 5)
				client = new Client(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), Long.parseLong(args[3]), args[4]);
			else if (args.length == 4)
				client = new Client(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]), Long.parseLong(args[3]));
			else if (args.length == 3)
				client = new Client(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Double.parseDouble(args[2]));
			else if (args.length == 2)
				client = new Client(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			else if (args.length == 1)
				client = new Client(Integer.parseInt(args[0]));
			else {
				System.err.println("Usage: java -jar client.jar id [seed] [HOLD_TIME] [TIME_UNIT (in ms)] [pathToConfigFile (default : \"servers.config\")]");
				System.exit(0);
			}

			// c.setHoldTime(1, TimeUnit.SECONDS);
			// c.setNumberOfDataObjects(4);
			// c.setServerInfoProvider(new RemoteSiteFromFile("server.config"));
			// c.setServerInfoProvider(new RemoteSiteFromConsole());
			// AbstractRequestSender reqSender = new RequestSender();
			// reqSender.setServers(c.getServers());
			// c.setRequestSender(reqSender);

			client.runClient();
		} catch (NumberFormatException e) {
			System.err.println("Invalid args: Usage: java -jar client.jar id [seed] [HOLD_TIME] [TIME_UNIT (in ms)] [pathToConfigFile (default : \"servers.config\")]");
			System.exit(0);
		}

	}

}
