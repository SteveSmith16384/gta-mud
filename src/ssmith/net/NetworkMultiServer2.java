package ssmith.net;

import java.net.*;
import java.io.*;
import java.util.*;

import ssmith.lang.Functions;

public abstract class NetworkMultiServer2 extends Thread {

	private int port, max_connections;
	private ServerSocket sckListener;
	public boolean debug = false;
	private volatile boolean stopNow = false;
	public volatile ArrayList connections = new ArrayList();

	public NetworkMultiServer2(int port, int max_conns) {
		super();
		this.setDaemon(true);
		this.port = port;
		this.max_connections = max_conns;
	}

	public void run() {
		try {
			sckListener = new ServerSocket(port);
			while (!stopNow) {
				while (this.connections.size() >= this.max_connections) {
					Functions.delay(10000);
				}

				if (debug)
					System.out.println("Server listening on port " + port + "...");
				Socket s = sckListener.accept();
				createConnectionPre(s);
				/*sckListener.close();
              sckListener = null;*/
			}
		}
		catch (IOException e) {
			System.err.println("IO Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Override this method when a connection is made
	 */
	public void createConnectionPre(Socket sck) throws IOException {
		this.connections.add(createConnection(sck));
		//System.out.println("Connection made from " + sck.getInetAddress().toString() + ".  There are now " + connections.size() + " players connected.");
	}

	public abstract NetworkMultiServerConn2 createConnection(Socket sck) throws IOException ;


	/*public NetworkMultiServerConn2 getConnection(int c) {
    return (NetworkMultiServerConn2)connections.get(c);
  }*/

	public void removeConnection(NetworkMultiServerConn2 conn) {
		this.connections.remove(conn);
		//System.out.println("Connection removed.  There are now " + connections.size() + " players connected.");
	}



	public void stopListening() {
		stopNow = true;
	}

}
