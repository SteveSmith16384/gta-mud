package gta_text;

import ssmith.lang.Functions;
import ssmith.mail.MailAddress;
import ssmith.mail.SmtpMail;
import ssmith.net.NetworkMultiServer2;
import ssmith.net.NetworkMultiServerConn2;
import gta_text.admin.TOTD;

import java.net.Socket;
import java.io.IOException;
import java.util.Date;

public class Server extends NetworkMultiServer2 {

	public static final String CR = "\r\n";
	public static final boolean DEBUG = false;
	public static final String VERSION = "0.97";
	//public static final String EMAIL = "gta-mud@carlylesmith.karoo.co.uk";
	public static final int PORT = 4000;
	public static final int MAX_CONNECTIONS = 100;
	public static final String ERROR_LOG = "logs/errors.txt";

	public static boolean cheat_on = false;


	public static Game game;
	private static String error_log;
	public static TOTD totd; 

	public Server() {
		super(PORT, MAX_CONNECTIONS);
		try {
			System.out.println("GTA Server (" + VERSION + ") started at " +
					new Date().toString() + ".  Listening on port " +
					PORT + ".");
			if (DEBUG) {
				System.out.println("** DEBUG MODE ON **");
			}
			error_log = ERROR_LOG;
			totd = new TOTD(); 
			game = new Game(this);
			game.loadGame();
			startGame();
		} catch (Exception ex) {
			System.err.println("Error: "+ex.getMessage());
			ex.printStackTrace();
			Server.HandleError(ex);
		}
	}

	private void startGame() throws IOException {
		this.setPriority(Thread.MIN_PRIORITY);
		start();
		game.game_loop();
	}

	public NetworkMultiServerConn2 createConnection(Socket sck) throws IOException {
		ServerConn conn = new ServerConn(this, sck);
		write("Connection made from " + sck.getInetAddress().toString() + ".  There are now " + (connections.size()+1) + " players connected.");
		return conn;
	}

	public static synchronized void write(String s) {
		System.out.println(new Date().toString() + ": " + s);
	}

	public static synchronized void HandleError(Exception ex) {
		System.err.println("Error: " + ex.getMessage());
		ex.printStackTrace();
		Functions.LogStackTrace(ex, error_log);
	}

	public static synchronized void SendEmail(String to, String subject, String msg) {
		try {
			SmtpMail smtp = new SmtpMail();
			smtp.simpleSend("smtp.karoo.co.uk", 25, MailAddress.parseAddress("Todo: Put from address here"), MailAddress.parseAddress(to), subject, msg);
		} catch (Exception ex) {
			Server.HandleError(ex);
		}
	}

	//************************************************
	public static void main(String[] args) {
		new Server();
	}

}
