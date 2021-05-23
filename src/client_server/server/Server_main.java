package client_server.server;

public class Server_main {

	public static void main(String[] args) {
		try {
			Server s = null;

			if (args.length == 3)
				s = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
			else if (args.length == 2)
				s = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
			else if (args.length == 1)
				s = new Server(Integer.parseInt(args[0]));
			else {
				System.err.println("Usage: java -jar server.jar id [port] [numOfDataObjects]");
				System.exit(0);
			}
			s.runServer();
		} catch (NumberFormatException e) {
			System.err.println("Invalid args: Usage: java -jar server.jar id [port] [numOfDataObjects]");
			System.exit(0);
		}
	}

}
