package gta_text;

import ssmith.io.LogFile;
import ssmith.io.TextFile;
import ssmith.lang.Functions;
import ssmith.net.NetworkMultiServer2;
import ssmith.net.NetworkMultiServerConn2;
import ssmith.util.CSVString;
import gta_text.admin.SavedGames;
import gta_text.items.Item;
import gta_text.Server;
import gta_text.npcs.GameCharacter;
import gta_text.npcs.PlayersCharacter;
import java.net.Socket;
import java.util.Date;
import java.io.IOException;
import gta_text.locations.*;
import java.util.Enumeration;
import gta_text.items.AK47;
import gta_text.items.Corpse;
import gta_text.items.RangedWeapon;
import gta_text.npcs.CanBeRobbed;

public class ServerConn extends NetworkMultiServerConn2 implements Runnable {

    //private static final String EMAIL = "stephen.carlylesmith@googlemail.com";
    private static final boolean DEBUG_COMMS = false;
	public static final boolean DEBUG_OBJS = true;

	public static final String RESET_COL = "[0m";
	public static final String NORMAL_COL = "[30m";
	public static final String SPEECH_COL = "[36m";
	public static final String ENTER_EXIT_COL = "[32m";
	public static final String PROMPT_COL = "[37m";
	public static final String COMBAT_COL = "[31m";
	public static final String ACTION_COL = "[33m";
	public static final String ERROR_COL = "[35m";

	private static final String ADMIN_MSG = "messages.txt";
	private static final String MASTER_LOG = "master.log";
	private static final int MAX_INPUT = 1000;
	private static final int TIMEOUT = 30*60*1000;

	private Thread t = new Thread(this);
	private StringBuffer users_input = new StringBuffer();
	private PlayersCharacter character;
	public volatile boolean quitting = false;
	private LogFile log;
	private long last_input_time = System.currentTimeMillis();
	public String playername = "No_Name_Yet", pwd, prev_command = "";
	public boolean rcvd_blank_line;
	public volatile String next_input = "";
	private boolean use_colours = false;

	public ServerConn(NetworkMultiServer2 svr, Socket sck) throws IOException {
		super(svr, sck);
		sck.setSoTimeout(TIMEOUT);
		t.setDaemon(true);
		t.start();
	}

	public void run() {
		try {
			sendOutput("GTA-MUD version " + Server.VERSION);

			cmdShowFile("welcome_msg.txt");

			LogFile master = new LogFile("./logs/" + MASTER_LOG);
			master.append("Connected," + sck.getInetAddress().toString().replaceFirst("/", "") + "," + new Date().toString() + "," + server.connections.size() + "," + System.currentTimeMillis());

			log = new LogFile("./logs/" + System.currentTimeMillis() + "-" + Server.VERSION + "-" + sck.getInetAddress().toString().replaceFirst("/", "") + ".log");
			log.append(sck.getInetAddress().toString().replaceFirst("/", "") + " connected at " + new Date().toString());

			// Flush input
			byte b[] = new byte[this.getInput().available()];
			this.getInput().readFully(b);

			sendOutput("Are you a new player or do you have a saved character? (n/s)");
			SavedGames games = new SavedGames();
			if (getBooleanInput("n", "s")) {
				this.sendOutput("New Character");
				// New user
				boolean original = false;
				while (!original) {
					playername = "";
					while (playername.length() == 0) {
						sendOutput("What is your name?");
						playername = this.getUsersInput(true);
						this.sendPrompt(playername);
						this.sendOutput("Your name is '" + playername + "'.  Is that right? (y/n)");
						if (this.getBooleanInput("y", "n") == false) {
							playername = "";
						} else if (playername.equalsIgnoreCase("CJ")) {
							this.sendOutput("Okay, but it's not very original!");
						}
					}

					pwd = "";
					while (pwd.length() == 0) {
						sendOutput("What is your password?");
						pwd = this.getUsersInput(true);
						this.sendPrompt(pwd);
						this.sendOutput("Your password is '" + pwd + "'.  Is that right? (y/n)");
						if (this.getBooleanInput("y", "n") == false) {
							pwd = "";
						}
					}

					if (games.savedGameExists(playername, pwd)) {
						// That username/pwd already exists!
						this.sendOutput("Sorry, that is an invalid password.");
					} else {
						original = true;
					}
				}

				String name = "";
				sendOutput("What is your character's career?  This can be anything - for example 'cop', 'militant', anything.  Please don't have spaces in it though.");
				while (name.length() == 0) {
					name = this.getUsersInput(true);
					this.sendPrompt(name);
					if (name.indexOf(" ") >= 0) {
						sendOutput("Please try again without spaces.");
						name = "";
					} else if (Server.game.containsCharacter(name)) {
						sendOutput(
								"I'm afraid that career choice has already been taken.");
						name = "";
					} else {
						this.sendOutput("Your career is '" + name + "'.  Is that right? (y/n)");
						if (this.getBooleanInput("y", "n") == false) {
							name = "";
							sendOutput("What is your character's career?");
						}
					}
				}

				sendOutput("Is your character male or female? (m/f)");
				boolean male = this.getBooleanInput("m", "f");

				sendOutput("Summary");
				sendOutput("Name: " + playername);
				sendOutput("Password: " + pwd);
				sendOutput("Career: " + name);
				sendOutput("Male: " + male);
				sendOutput("");

				sendOutput("Tip of the Day: " + Server.totd.getTip());

				character = new PlayersCharacter(this, playername, name, male, true);
				Server.write("Character " + name + " created. (" + male + ")");
			} else {
				this.sendOutput("Load existing character");
				boolean found = false;
				while (!found) {
					playername = "";
					sendOutput("What is your name?");
					playername = this.getUsersInput(true);
					this.sendPrompt(playername);

					pwd = "";
					sendOutput("What is your password?");
					pwd = this.getUsersInput(true);
					this.sendPrompt(pwd);

					found = games.savedGameExists(playername, pwd);
					if (!found) {
						this.sendOutput("Sorry, no saved game can be found under that username/password combination.  Are you sure you went to the hotel to save your game?");
					}
				}
				character = new PlayersCharacter(this, playername, "", true, false);
				try {
					games.loadCharactersDetails(character);
					//this.sendOutput(character.name + " loaded successfully.");
				} catch (Exception e) {
					Server.HandleError(e);
					this.sendOutput("Sorry, there was a problem loading your character.  The GTA-MUD admin have been informed.");
				}
				Server.write("Character " + character.name + " reloaded. (" + character.male + ")");
			}

			//log.append("PLAYERS NAME: " + playername);
			//log.append("NAME: " + character.name);

			//sendOutput("Hello " + playername + ".");
			sendOutput("The time is " + Server.game.game_time.getTimeAsString() + ".");

			while (quitting == false) {
				if (this.next_input.length() == 0) {
					next_input = this.getUsersInput(false);
					if (this.next_input.length() > 0) {
						if (checkForImmediateResponse(this.next_input)) {
							next_input = "";
						}
					}
				}
				Functions.delay(100);
			}
		} catch (TimeoutException ex) {
			// Do nothing - go straight to finally().
		} catch (IOException ex) {
			if (ex.getMessage().equalsIgnoreCase("Stream closed.")) {
				// Do nothing - they've disconnected
			} else {
				Server.HandleError(ex);
			}
		} finally {
			try {
				if (character != null) {
					character.removeFromGame();
				}
				LogFile master = new LogFile("./logs/" + MASTER_LOG);
				master.append("Disconnected," + sck.getInetAddress().toString().replaceFirst("/", "") + "," + new Date().toString() + "," + server.connections.size() + "," + System.currentTimeMillis());

				this.close(); // Also removes connection from list!

				if (log != null) {
					log.append("Finished at " + new Date().toString());
				}
				Server.write("Connection removed.  There are now " + server.connections.size() + " players connected.");
			} catch (IOException ex1) {
				System.err.println("Check Error: " + ex1.getMessage());
			}
		}
	}

	private boolean checkForImmediateResponse(String i) throws IOException {
		if (i.equalsIgnoreCase("!")) {
			i = this.prev_command;
		}

		String i_uc = i.toUpperCase();

		if (i.equalsIgnoreCase("QUIT")) {
			this.sendPrompt(i);
			quitting = true;
			this.sendOutput("Goodbye.  Visit San Torino again soon!");
		} else if (i.equalsIgnoreCase("Q")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to leave the game, type QUIT.");
		} else if (i.equalsIgnoreCase("CLOSE")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to leave the game, type QUIT.");
		} else if (i.equalsIgnoreCase("RELOAD")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to star again from a saved position, please quit and reconnect.");
		} else if (i.equalsIgnoreCase("BYE")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to leave the game, please enter 'QUIT'.  If you wish to change location, please enter the direction you wish to travel in, e.g. 'N' for north.");
		} else if (i.equalsIgnoreCase("END")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to leave the game, please enter 'QUIT'.  If you wish to change location, please enter the direction you wish to travel in, e.g. 'N' for north.");
		} else if (i.equalsIgnoreCase("WHOAMI")) {
			this.sendPrompt(i);
			this.sendOutput("You are " + this.character.name + ".");
		} else if (i.equalsIgnoreCase("INV")) {
			this.sendPrompt(i);
			cmdInv();
		} else if (i.equalsIgnoreCase("ITEMS")) {
			this.sendPrompt(i);
			cmdInv();
		} else if (i.equalsIgnoreCase("IN")) {
			this.sendPrompt(i);
			cmdInv();
		} else if (i.equalsIgnoreCase("I")) {
			this.sendPrompt(i);
			cmdInv();
		} else if (i.equalsIgnoreCase("INVENTORY")) {
			this.sendPrompt(i);
			cmdInv();
		} else if (i_uc.startsWith("EQ")) {
			this.sendPrompt(i);
			cmdInv();
		} else if (i.equalsIgnoreCase("CASH")) {
			this.sendPrompt(i);
			cmdCash();
		} else if (i.equalsIgnoreCase("QUID")) {
			this.sendPrompt(i);
			cmdCash();
		} else if (i.equalsIgnoreCase("DOLLARS")) {
			this.sendPrompt(i);
			cmdCash();
		} else if (i.equalsIgnoreCase("MONEY")) {
			this.sendPrompt(i);
			cmdCash();
		} else if (i.equalsIgnoreCase("GOLD")) {
			this.sendPrompt(i);
			cmdGold();
		} else if (i.equalsIgnoreCase("DRINK")) {
			this.sendPrompt(i);
			this.sendOutput(ERROR_COL, "Please specify what you'd like to drink.");
		} else if (i.equalsIgnoreCase("EAT")) {
			this.sendPrompt(i);
			this.sendOutput(ERROR_COL, "Please specify what you'd like to eat.");
		} else if (i.equalsIgnoreCase("FLUSH")) {
			this.sendPrompt(i);
			this.sendOutput(ERROR_COL, "Please specify what you'd like to flush.");
		} else if (i.equalsIgnoreCase("WIELD")) {
			this.sendPrompt(i);
			this.sendOutput(ERROR_COL, "Please specify what you'd like to wield.");
		} else if (i.equalsIgnoreCase("UNWIELD")) {
			this.sendPrompt(i);
			this.sendOutput(ERROR_COL, "Please specify what you'd like to unwield.");
		} else if (i.equalsIgnoreCase("BURY") || i_uc.startsWith("BURY ")) {
			this.sendPrompt(i);
			this.sendOutput(ERROR_COL, "The ground is too hard to bury anything.");
		} else if (i_uc.startsWith("COLO") || i_uc.equalsIgnoreCase("ANSI")) {
			this.sendPrompt(i);
			this.use_colours = !this.use_colours;
			if (this.use_colours) {
				this.sendOutput(ACTION_COL, "Colours are now ON.");
			} else {
				this.sendOutput("Colours are now OFF.");
			}
		} else if (i.equalsIgnoreCase("PROMPT")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, this MUD does not currently support any 'prompt' commands.  Enter 'HELP' for a list of commands.");
		} else if (i.equalsIgnoreCase("CONFIG")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, this MUD does not currently support any 'config' commands.  Enter 'HELP' for a list of commands.");
		} else if (i_uc.startsWith("WIZ")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, this MUD does not have any 'wiz' commands.");
		} else if (i_uc.startsWith("BRIEF")) {
			this.sendPrompt(i);
			this.sendOutput("The output couldn't be any briefer if I tried!");
		} else if (i_uc.startsWith("ENTER ") || i.equalsIgnoreCase("ENTER")) {
			this.sendPrompt(i);
			this.sendOutput("If you want to enter a building, please type in a direction.");
		} else if (i.equalsIgnoreCase("WALK")) {
			this.sendPrompt(i);
			this.sendOutput("Please specify a direction.");
		} else if (i.equalsIgnoreCase("GREET")) {
			this.sendPrompt(i);
			this.sendOutput("Who would you like to greet?");
		} else if (i.equalsIgnoreCase("SET DESC")) {
			this.sendPrompt(i);
			this.sendOutput("Please enter 'SET DESC [NEW DESCRIPTION]'.");
		} else if (i_uc.startsWith("SET DESC ")) {
			this.sendPrompt(i);
			this.cmdSetDesc(i.substring(9, i.length()));
		} else if (i.equalsIgnoreCase("CHEAT")) {
			try {
				if (this.isGM()) {
					if (this.character.current_item instanceof AK47 == false) {
						this.character.addItem(new AK47());
					}
					this.character.increaseHealth(100, null);
					this.character.cash += 100;
					this.character.incWantedLevel(-5);
				}
			} catch (Exception ex1) {
				Server.HandleError(ex1);
			}
		} else if (i.equalsIgnoreCase("GOD ON")) {
			if (this.isGM()) {
				Server.cheat_on = true;
			}
		} else if (i.equalsIgnoreCase("GOD OFF")) {
			Server.cheat_on = false;
		} else if (i.equalsIgnoreCase("GOD CANCEL THIRST")) {
			if (this.isGM()) {
				this.character.reduceThirst(1000);
				this.character.reduceHunger(1000);
			}
		} else if (i.equalsIgnoreCase("GOD CASH")) {
			if (this.isGM()) {
				this.character.cash += 100;
			}
		} else if (i.equalsIgnoreCase("GOD GAME STATS")) {
			if (this.isGM()) {
				this.sendOutput("Total characters: " + Server.game.characters.size() + ".");
				this.sendOutput("Total items: " + Server.game.items.size() + ".");
			}
		} else if (i_uc.startsWith("GOD NEW ITEM")) {
			if (this.isGM()) {
				//Item item = (Item) (Class.forName().newInstance());
				//character.addItem(item);
			}
		} else if (i.equalsIgnoreCase("REST")) {
			this.sendPrompt(i);
			this.sendOutput("You don't need to rest.");
		} else if (i.equalsIgnoreCase("RELAX")) {
			this.sendPrompt(i);
			this.sendOutput("You don't need to relax.");
		} else if (i.equalsIgnoreCase("SLEEP")) {
			this.sendPrompt(i);
			this.sendOutput("You don't need to sleep.");
		} else if (i.equalsIgnoreCase("JOB") || i.equalsIgnoreCase("WORK")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to find work, try reading notices and talking to people.");
		} else if (i.equalsIgnoreCase("DIE")) {
			this.sendPrompt(i);
			this.sendOutput("You or someone else?  If you wish to quit, type QUIT.");
		} else if (i_uc.startsWith("HEAL ") && i.equalsIgnoreCase("HEAL")) {
			this.sendPrompt(i);
			this.sendOutput("You could always try asking the doctor at the hospital.");
		} else if (i.equalsIgnoreCase("GIVE UP")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to quit, type QUIT.  Why do you wish to give up?");
		} else if (i.equalsIgnoreCase("GIVE")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, I don't quite understand.  Please enter something like 'GIVE [item] TO [character]'.");
		} else if (i.equalsIgnoreCase("KISS")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, I don't quite understand.  Please enter something like 'KISS [character]'.");
		} else if (i.equalsIgnoreCase("COMMIT SUICIDE")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to quit, type QUIT.  Why do you wish to commit suicide?");
		} else if (i.equalsIgnoreCase("SUICIDE")) {
			this.sendPrompt(i);
			this.sendOutput("If you want to end the game, type 'quit'.");
		} else if (i.equalsIgnoreCase("MISSION")) {
			this.sendPrompt(i);
			this.sendOutput("To find a mission, try talking to people and examining things.");
		} else if (i.equalsIgnoreCase("RESET")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, there is no 'reset' command.  If you want to go back to the start, you'll have to walk!");
		} else if (i.equalsIgnoreCase("SCAN")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, there is no 'scan' command.  You can only look at the current location.");
		} else if (i.equalsIgnoreCase("MAP")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, there is no automap facility.");
		} else if (i.equalsIgnoreCase("CHANNEL")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, there is no 'channel' command.  Use 'CHAT' to communicate globally.");
		} else if (i.equalsIgnoreCase("IMMS")) {
			this.sendPrompt(i);
			this.sendOutput("Imms come and go.  Enter WHO to see who's currently on-line.");
		} else if (i.equalsIgnoreCase("EMOTE")) {
			this.sendPrompt(i);
			this.sendOutput("Please specify what you would like to emote.  Enter 'HELP EMOTE' for more help.");
		} else if (i.equalsIgnoreCase("HELP")) {
			this.sendPrompt(i);
			cmdHelp();
		} else if (i.equalsIgnoreCase("HELP COMMANDS")) {
			this.sendPrompt(i);
			cmdHelp();
		} else if (i.equalsIgnoreCase("HELP THE PEOPLE")) {
			this.sendPrompt(i);
			this.sendOutput("You are not Mother Teresa.");
		} else if (i_uc.startsWith("HELP ")) {
			this.sendPrompt(i);
			cmdSpecificHelp(i.substring(5, i.length()));
		} else if (i_uc.startsWith("MANUAL")) {
			this.sendPrompt(i);
			cmdHelp();
		} else if (i.equalsIgnoreCase("?")) {
			this.sendPrompt(i);
			cmdHelp();
		} else if (i_uc.startsWith("COMMAND")) {
			this.sendPrompt(i);
			cmdHelp();
		} else if (i.equalsIgnoreCase("SAVE") && this.character.getCurrentLocation() != Server.game.hotel_reception) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, you can only save the game by taking a room at the hotel.");
		} else if (i.equalsIgnoreCase("WAIT")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, there is no 'wait' command.  You'll have to sit it out.");
		} else if (i.equalsIgnoreCase("NEWS ARCHIVE")) {
			this.sendPrompt(i);
			cmdShowFile("old_news.txt");
		} else if (i.equalsIgnoreCase("NEWS")) {
			this.sendPrompt(i);
			cmdShowFile("news.txt");
		} else if (i.equalsIgnoreCase("CHANGES")) {
			this.sendPrompt(i);
			cmdShowFile("news.txt");
		} else if (i.equalsIgnoreCase("CHANGELOG")) {
			this.sendPrompt(i);
			cmdShowFile("news.txt");
		} else if (i_uc.startsWith("MSGTOADMIN ")) {
			this.sendPrompt(i);
			cmdSendMsgToAdmin(i.substring(11, i.length()));
		} else if (i_uc.startsWith("GNOTE ")) {
			this.sendPrompt(i);
			cmdSendMsgToAdmin(i.substring(6, i.length()));
		} else if (i.equalsIgnoreCase("GNOTE")) {
			this.sendPrompt(i);
			this.sendOutput("Please enter something like 'GNOTE [message].");
		} else if (i.equalsIgnoreCase("CREDITS")) {
			this.sendPrompt(i);
			cmdCredits();
		} else if (i_uc.startsWith("WRITE ")) {
			this.sendPrompt(i);
			cmdWrite(i.substring(6, i.length()));
		} else if (i_uc.startsWith("HIRE ") || i.equalsIgnoreCase("HIRE")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to hire someone, try giving them some money.");
		} else if (i.equalsIgnoreCase("STATS")) {
			this.sendPrompt(i);
			cmdStats();
		} else if (i.equalsIgnoreCase("STATUS")) {
			this.sendPrompt(i);
			cmdStats();
		} else if (i.equalsIgnoreCase("STAT")) {
			this.sendPrompt(i);
			cmdStats();
		} else if (i.equalsIgnoreCase("HEALTH")) {
			this.sendPrompt(i);
			cmdStats();
		} else if (i.equalsIgnoreCase("INFO")) {
			this.sendPrompt(i);
			cmdStats();
		} else if (i.equalsIgnoreCase("INF")) {
			this.sendPrompt(i);
			cmdStats();
		} else if (i.equalsIgnoreCase("WHO")) {
			this.sendPrompt(i);
			cmdWho();
		} else if (i.equalsIgnoreCase("WHOIS")) {
			this.sendPrompt(i);
			cmdWho();
		} else if (i.equalsIgnoreCase("ALIVE")) {
			this.sendPrompt(i);
			cmdWho();
		} else if (i.equalsIgnoreCase("ONLINE")) {
			this.sendPrompt(i);
			cmdWho();
		} else if (i.equalsIgnoreCase("WHERE")) {
			this.sendPrompt(i);
			this.sendOutput("You are in San Torino.  All other players will receive messages sent with the CHAT command.");
		} else if (i.equalsIgnoreCase("PKILL")) {
			this.sendPrompt(i);
			this.sendOutput("Player-killing is encouraged, if required to get to the top!");
		} else if (i.equalsIgnoreCase("USERS")) {
			this.sendPrompt(i);
			cmdWho();
		} else if (i.equalsIgnoreCase("PLAYERS")) {
			this.sendPrompt(i);
			cmdWho();
		} else if (i.equalsIgnoreCase("LOOK")) {
			this.sendPrompt(i);
			cmdLook();
		} else if (i.equalsIgnoreCase("LOOK AROUND")) {
			this.sendPrompt(i);
			cmdLook();
		} else if (i.equalsIgnoreCase("SEE")) {
			this.sendPrompt(i);
			cmdLook();
		} else if (i.equalsIgnoreCase("EXITS")) {
			this.sendPrompt(i);
			this.sendOutput(this.character.getCurrentLocation().getVisibleExits());
		} else if (i.equalsIgnoreCase("L")) {
			this.sendPrompt(i);
			cmdLook();
		} else if (i.equalsIgnoreCase("DESC")) {
			this.sendPrompt(i);
			cmdLook();
		} else if (i.equalsIgnoreCase("TIME")) {
			this.sendPrompt(i);
			cmdTime();
		} else if (i.equalsIgnoreCase("SCORE")) {
			this.sendPrompt(i);
			this.cmdInv();
		} else if (i.equalsIgnoreCase("SC")) {
			this.sendPrompt(i);
			this.cmdInv();
		} else if (i.equalsIgnoreCase("SCO")) {
			this.sendPrompt(i);
			this.cmdInv();
		} else if (i.equalsIgnoreCase("WEATHER")) {
			this.sendPrompt(i);
			this.sendOutput(Server.game.game_weather.getDesc());
		} else if (i.equalsIgnoreCase("CHAT")) {
			this.sendPrompt(i);
			this.sendOutput("Try 'Chat [your message here]'");
		} else if (i_uc.startsWith("CHAT ")) {
			this.sendPrompt(i);
			cmdChat(i.substring(5, i.length()));
		} else if (i_uc.startsWith("NARR ")) {
			this.sendPrompt(i);
			cmdChat(i.substring(5, i.length()));
		} else if (i_uc.startsWith("CHA ")) {
			this.sendPrompt(i);
			cmdChat(i.substring(4, i.length()));
		} else if (i_uc.startsWith("OOC ")) {
			this.sendPrompt(i);
			cmdChat(i.substring(4, i.length()));
		} else if (i_uc.startsWith("CHAT:")) {
			this.sendPrompt(i);
			cmdChat(i.substring(5, i.length()));
		} else if (i.equalsIgnoreCase("SHOUT")) {
			this.sendPrompt(i);
			this.sendOutput("Try 'Shout [your message here]'");
		} else if (i_uc.startsWith("SHOUT ")) {
			this.sendPrompt(i);
			cmdChat(i.substring(6, i.length()));
		} else if (i.equalsIgnoreCase("GOSSIP")) {
			this.sendPrompt(i);
			this.sendOutput("Try 'Gossip [your message here]'");
		} else if (i_uc.startsWith("GOSSIP ")) {
			this.sendPrompt(i);
			cmdChat(i.substring(7, i.length()));
		} else if (i.equalsIgnoreCase("YES") || i.equalsIgnoreCase("Y")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to speak, enter something like 'SAY YES'.");
		} else if (i.equalsIgnoreCase("NO")) {
			this.sendPrompt(i);
			this.sendOutput("If you wish to speak, enter something like 'SAY NO'.");
		} else if (i.equalsIgnoreCase("SAY")) {
			this.sendPrompt(i);
			this.sendOutput("Say what?  Enter 'HELP SAY' for more info.");
		} else if (i_uc.startsWith("TALK")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, I don't quite understand.  Try entering something like 'SAY [words]'.  Enter 'HELP SAY' for more info.");
		} else if (i.equalsIgnoreCase("ASK")) {
			this.sendPrompt(i);
			this.sendOutput("Sorry, I don't quite understand.  Try entering something like 'ASK [character] [question]'.  Enter HELP for more info.");
		} else if (i_uc.startsWith("MSGFROMADMIN ")) {
			this.sendPrompt(i);
			cmdMsgFromGM(i.substring(13, i.length()));
		} else if (i.equalsIgnoreCase("GET")) {
			this.sendPrompt(i);
			this.sendOutput("Please specify what you would like to get.");
		} else if (i.equalsIgnoreCase("TAKE")) {
			this.sendPrompt(i);
			this.sendOutput("Please specify what you would like to take.");
		} else if (i_uc.startsWith("LOOK IN ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(8, i.length()));
		} else if (i_uc.startsWith("L IN ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(5, i.length()));
		} else if (i_uc.startsWith("LOOK AT ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(8, i.length()));
		} else if (i_uc.startsWith("LOOK ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(5, i.length()));
		} else if (i_uc.startsWith("LOO ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(4, i.length()));
		} else if (i_uc.startsWith("L ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(2, i.length()));
		} else if (i_uc.startsWith("EXAM ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(5, i.length()));
		} else if (i_uc.startsWith("INFO ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(5, i.length()));
		} else if (i_uc.startsWith("EXAMINE ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(8, i.length()));
		} else if (i_uc.startsWith("EXAMIN ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(7, i.length()));
		} else if (i_uc.startsWith("SEARCH ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(7, i.length()));
		} else if (i_uc.startsWith("X ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(2, i.length()));
		} else if (i_uc.startsWith("EXA ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(4, i.length()));
		} else if (i_uc.startsWith("EX ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(3, i.length()));
		} else if (i.equalsIgnoreCase("CON")) {
			this.sendPrompt(i);
			this.sendOutput("Consider who?");
		} else if (i_uc.startsWith("CON ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(4, i.length()));
		} else if (i_uc.startsWith("CONS ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(5, i.length()));
		} else if (i.equalsIgnoreCase("CONSIDER")) {
			this.sendPrompt(i);
			this.sendOutput("Consider who?");
		} else if (i_uc.startsWith("CONSIDER ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(9, i.length()));
		} else if (i_uc.startsWith("SEE ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(4, i.length()));
		} else if (i_uc.startsWith("L AT ")) {
			this.sendPrompt(i);
			cmdExamine(i.substring(5, i.length()));
		} else if (i.equalsIgnoreCase("READ")) {
			this.sendPrompt(i);
			this.sendOutput("What do you want to read?");
		} else if (i_uc.startsWith("VALUE ") || i_uc.startsWith("COST") || i_uc.startsWith("LIST ") || i_uc.startsWith("PRICE ")) {
			this.sendPrompt(i);
			CSVString csv = new CSVString(i, " ");
			getPriceOf(csv.getSection(1));
		} else {
			return false;
		}
		this.prev_command = i;
		return true;
	}


	/**
	 * This returns true if the first param is entered by the user, else false.
	 */
	public boolean getBooleanInput(String s1, String s2) throws IOException, TimeoutException {
		while (true) {
			Functions.delay(500);
			String conf = this.getUsersInput(true);
			this.sendPrompt(conf);
			if (conf.toUpperCase().startsWith(s1.toUpperCase()) || conf.toUpperCase().startsWith(s2.toUpperCase())) {
				if (conf.toUpperCase().startsWith(s1.toUpperCase())) {
					return true;
				} else {
					return false;
				}
			} else {
				this.sendOutput("Sorry, I didn't quite understand that.  Please enter '" + s1 + "' or '" + s2 + "'.");
			}
		}
	}

	public synchronized void sendOutput(String str) throws IOException {
		sendOutput("", str);
	}

	public synchronized void sendOutput(String col, String str) throws IOException {
		try {
			if(this.rcvd_blank_line == false) {
				this.rcvd_blank_line = true;
				this.sendOutput("");
			}
			if (log != null) {
				log.append(str);
			}
			if (use_colours) {
				if (col.length() > 0) {
					this.getOutput().write(27);
					this.getOutput().writeBytes(col);
				}
			}
			this.getOutput().writeBytes(str);
			this.getOutput().write(13);
			this.getOutput().write(10);

			if (use_colours) {
				if (col.length() > 0) {
					this.getOutput().write(27);
					this.getOutput().writeBytes(RESET_COL);
				}
			}
		} catch (java.net.SocketException ex) {
			this.close();
		}
	}

	public String getUsersInput(boolean block) throws IOException, TimeoutException { // Don't have synchronized here!
		// Check they're not trying to overload the server
		if (users_input.length() > MAX_INPUT) {
			this.sendOutput("Input limit exceeded.");
			users_input = new StringBuffer();
		}

		hasTimeoutExpired();

		// Get any more input
		StringBuffer sb = new StringBuffer();
		byte b = 0;
		while (this.getInput().available() > 0 && b != 10) {
			b = this.getInput().readByte();
			if (b >= 32 || b == 13 || b == 10) {
				if (DEBUG_COMMS) {
					System.out.println("Received: " + b);
				}
				sb.append((char)b);
			} else {
				if (b == 8 || b == 27) { // Delete or left
					try {
						if (sb.length() > 0) {
							sb.delete(sb.length() - 1, sb.length());
						}
					} catch (Exception ex) {
						Server.HandleError(ex);
					}
				} else {
					System.out.println("Unknown char: " + b);
				}
			}
		}
		String new_text = sb.toString();
		users_input.append(new_text);

		int pos = users_input.indexOf("\n");
		if (pos > 0) {
			last_input_time = System.currentTimeMillis();
			String text = users_input.substring(0, pos);
			users_input.delete(0, text.length() + 2); // Remove the text entered plus CRLF
			text = text.replaceAll("  ", " ").trim(); // In case entered extra spaces
			if (block && text.replaceAll(" ", "").length() == 0) {
				Functions.delay(500);
				return getUsersInput(block);
			}
			return text;
		} else {
			if (block) {
				Functions.delay(500);
				return getUsersInput(block);
			} else {
				return "";
			}
		}
	}

	private void hasTimeoutExpired() throws TimeoutException, IOException {
		if (System.currentTimeMillis() - this.last_input_time > TIMEOUT && this.isGM() == false) {
			if (log != null) {
				log.append("Timeout expired");
			}
			this.sendOutput("Timeout expired.");
			throw new TimeoutException();
		}
	}

	private void sendPrompt(String cmd) throws IOException {
		this.sendOutput("");
		this.sendOutput(PROMPT_COL, "==> " + cmd);

	}

	public void parseInput(String i) throws IOException {
		String i_uc = i.toUpperCase();
		if (i.length() > 0) {
			sendPrompt(i);
			try {
				if (i.equalsIgnoreCase("!")) {
					this.sendOutput(this.prev_command);
					this.parseInput(prev_command);
					return;
				} else {
					this.prev_command = i;
				}

				if (i.equalsIgnoreCase("TESTERROR")) {
					new Integer(i);
				} else if (i.equalsIgnoreCase("EXIT") || i.equalsIgnoreCase("AUTOEXIT") || i.equalsIgnoreCase("OUT") || i.equalsIgnoreCase("LEAVE")) {
					if (this.character.exit() == false) {
						this.sendOutput(
								"Please specify a direction.  If you wish to leave the game, type QUIT.");
					}
				} else if (i.equalsIgnoreCase("N") || i.equalsIgnoreCase("S") ||
						i.equalsIgnoreCase("E") || i.equalsIgnoreCase("W") ||
						i.equalsIgnoreCase("U") || i.equalsIgnoreCase("D")) {
					this.character.move(i);
				} else if (i.equalsIgnoreCase("NORTH") ||
						i.equalsIgnoreCase("SOUTH") || i.equalsIgnoreCase("EAST") ||
						i.equalsIgnoreCase("WEST") ||
						i.equalsIgnoreCase("UP") || i.equalsIgnoreCase("DOWN")) {
					this.character.move(i.substring(0, 1));
				} else if (i_uc.startsWith("RUN N") ||
						i_uc.startsWith("RUN S") || i_uc.startsWith("RUN E") ||
						i_uc.startsWith("RUN W")) {
					this.character.move(i.substring(4, 5));
				} else if (i_uc.startsWith("WALK N") ||
						i_uc.startsWith("WALK S") || i_uc.startsWith("WALK E") ||
						i_uc.startsWith("WALK W")) {
					this.character.move(i.substring(5, 6));
				} else if (i_uc.startsWith("FLEE N") ||
						i_uc.startsWith("FLEE S") || i_uc.startsWith("FLEE E") ||
						i_uc.startsWith("FLEE W")) {
					this.character.move(i.substring(5, 6));
				} else if (i_uc.startsWith("GO N") ||
						i_uc.startsWith("GO S") || i_uc.startsWith("GO E") ||
						i_uc.startsWith("GO W")) {
					this.character.move(i.substring(3, 4));
				} else if (i.equalsIgnoreCase("LEFT") || i.equalsIgnoreCase("RIGHT")) {
					this.sendOutput("If you wish to change location, please enter the direction you wish to travel in, e.g. 'N' for north.");
				} else if (i.equalsIgnoreCase("FLEE")) {
					this.sendOutput("You flee in a random direction.");
					this.character.move();
				} else if (i.equalsIgnoreCase("RUN")) {
					this.sendOutput("You run in a random direction.");
					this.character.move();
				} else if (i.equalsIgnoreCase("ESCAPE")) {
					this.sendOutput("You escape in a random direction.");
					this.character.move();
				} else if (i_uc.startsWith("GREET ")) {
					cmdGreet(i.substring(6, i.length()));
				} else if (i.equalsIgnoreCase("TOUCH")) {
					this.sendOutput("Who do you want to touch?  Try entering 'TOUCH [character]'.");
				} else if (i_uc.startsWith("TOUCH ")) {
					cmdTouch(i.substring(6, i.length()));
				} else if (i_uc.startsWith("SAY ")) {
					cmdSay(i.substring(4, i.length()), false);
				} else if (i_uc.startsWith("SAY:")) {
					cmdSay(i.substring(4, i.length()), false);
				} else if (i_uc.startsWith("WHISPER ")) {
					cmdSay(i.substring(8, i.length()), true);
				} else if (i_uc.startsWith("\"")) {
					cmdSay(i.substring(1, i.length()), false);
				} else if (i_uc.startsWith("'")) {
					cmdSay(i.substring(1, i.length()), false);
				} else if (i_uc.startsWith("TELL ")) {
					this.parseInput("SAY TO " + i.substring(5, i.length()));
				} else if (i_uc.startsWith("ASK ")) {
					this.parseInput("SAY TO " + i.substring(4, i.length()));
				} else if (i_uc.startsWith("EMOTE ")) {
					cmdEmote(i.substring(6, i.length()));
				} else if (i_uc.startsWith("EMOTION ")) {
					cmdEmote(i.substring(8, i.length()));
				} else if (i_uc.startsWith(":")) {
					cmdEmote(i.substring(1, i.length()));
				} else if (i_uc.startsWith("/")) {
					cmdEmote(i.substring(1, i.length()));
				} else if (i_uc.startsWith("GASP")) {
					cmdGasp();
				} else if (i_uc.startsWith("NOD")) {
					cmdSimpleEmote("nod");
				} else if (i_uc.startsWith("SHAKE")) {
					cmdSimpleEmote("shake");
				} else if (i_uc.startsWith("GRIN")) {
					cmdSimpleEmote("grin");
				} else if (i_uc.startsWith("CHUCKLE")) {
					cmdSimpleEmote("chuckle");
				} else if (i_uc.startsWith("PONDER")) {
					cmdSimpleEmote("ponder");
				} else if (i_uc.startsWith("FROWN")) {
					cmdSimpleEmote("frown");
				} else if (i_uc.startsWith("JUMP")) {
					cmdSimpleEmote("jump");
				} else if (i_uc.startsWith("SIT")) {
					cmdSimpleEmote("sit");
				} else if (i_uc.startsWith("STAND")) {
					cmdSimpleEmote("stand");
				} else if (i_uc.startsWith("SMIRK")) {
					cmdSimpleEmote("smirk");
				} else if (i_uc.startsWith("SMILE")) {
					cmdSimpleEmote("smile");
				} else if (i_uc.startsWith("SHIT")) {
					cmdSimpleEmote("shit");
				} else if (i_uc.startsWith("WANK")) {
					cmdSimpleEmote("wank");
				} else if (i_uc.startsWith("FART")) {
					cmdSimpleEmote("fart");
				} else if (i_uc.startsWith("LAUGH")) {
					cmdSimpleEmote("laugh");
				} else if (i_uc.startsWith("WAVE")) {
					cmdSimpleEmote("wave");
				} else if (i_uc.startsWith("HIRE ")) {
					this.sendOutput("If you wish to hire someone, try giving them some money.");
				} else if (i_uc.startsWith("ROB ") || i.equalsIgnoreCase("ROB")) {
					cmdRob();
				} else if (i_uc.startsWith("BUY ")) {
					cmdBuy(i.substring(4, i.length()).trim());
				} else if (i_uc.startsWith("ORDER ")) {
					cmdBuy(i.substring(6, i.length()).trim());
				} else if (i.equalsIgnoreCase("BUY")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("BROWSE")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("ORDER")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("LIS")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("LIST")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("SHOP")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("BUY LIST")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("WARES")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("APPRAISE")) {
					this.character.browse();
				} else if (i.equalsIgnoreCase("RECALL")) {
					this.sendOutput("You got yourself into this mess, you can get out of it!");
				} else if (i_uc.startsWith("GIVE ")) {
					cmdGive(i.substring(5, i.length()));
				} else if (i_uc.startsWith("PAY ")) {
					this.sendOutput("Sorry, I don't quite understand.  Try something like 'GIVE [amount] TO [whoever]'.  Enter 'HELP GIVE' for more help.");
				} else if (i_uc.startsWith("EAT ")) {
					this.character.eat(i.substring(4, i.length()), "eat");
				} else if (i.equalsIgnoreCase("FOOD")) {
					this.sendOutput("Sorry, I don't quite understand.  If you wish to buy something, try entering 'BUY [name of food]'.");
				} else if (i.equalsIgnoreCase("WATER")) {
					this.sendOutput("Sorry, I don't quite understand.  If you wish to buy something, try entering 'BUY [name of drink]'.");
				} else if (i_uc.startsWith("DRINK ")) {
					this.character.eat(i.substring(6, i.length()), "drink");
				} else if (i_uc.startsWith("DRI ")) {
					this.character.eat(i.substring(4, i.length()), "drink");
				} else if (i_uc.startsWith("CONSUME ")) {
					this.character.eat(i.substring(8, i.length()), "consume");
				} else if (i_uc.startsWith("READ ")) {
					this.character.read(i.substring(5, i.length()));
				} else if (i_uc.startsWith("GET ")) {
					cmdGet(i.substring(4, i.length()));
				} else if (i_uc.startsWith("G ")) {
					cmdGet(i.substring(2, i.length()));
				} else if (i_uc.startsWith("TAKE ")) {
					cmdGet(i.substring(5, i.length()));
				} else if (i_uc.startsWith("PICKUP ")) {
					cmdGet(i.substring(7, i.length()));
				} else if (i_uc.startsWith("PICK UP ")) {
					cmdGet(i.substring(8, i.length()));
				} else if (i_uc.startsWith("PICK ")) {
					cmdGet(i.substring(5, i.length()));
				} else if (i_uc.startsWith("STEAL ")) {
					cmdGet(i.substring(6, i.length()));
				} else if (i_uc.startsWith("LOOT ")) {
					this.sendOutput("If you wish to take something, please specify it.  Note that corpses do not contain anything; all previously owned objects are put on the floor.");
				} else if (i.equalsIgnoreCase("AUTOLOOT")) {
					this.sendOutput("If you wish to take something, please specify it.  Note that corpses do not contain anything; all previously owned objects are put on the floor.");
				} else if (i_uc.startsWith("REM ")) {
					this.character.drop(i.substring(4, i.length()));
				} else if (i_uc.startsWith("DROP ")) {
					this.character.drop(i.substring(5, i.length()));
				} else if (i_uc.startsWith("DR ")) {
					this.character.drop(i.substring(3, i.length()));
				} else if (i_uc.startsWith("JUNK ")) {
					this.character.junkItem(i.substring(5, i.length()));
				} else if (i.equalsIgnoreCase("USE")) {
					this.sendOutput("Please specify what you would like to use.");
				} else if (i_uc.startsWith("USE ")) {
					cmdUse(i.substring(4, i.length()));
				} else if (i.equalsIgnoreCase("DEAL")) {
					this.sendOutput("Please specify what you would like to deal.");
				} else if (i_uc.startsWith("DEAL ")) {
					cmdUse(i.substring(5, i.length()));
				} else if (i_uc.startsWith("WEAR ")) {
					cmdUse(i.substring(5, i.length()));
				} else if (i_uc.startsWith("LIGHT ")) {
					cmdUse(i.substring(6, i.length()));
				} else if (i.equalsIgnoreCase("SMOKE")) {
					this.sendOutput(ERROR_COL, "What would you like to smoke?");
				} else if (i_uc.startsWith("SMOKE ")) {
					cmdUse(i.substring(6, i.length()));
				} else if (i_uc.startsWith("FLUSH ")) {
					cmdUse(i.substring(6, i.length()));
				} else if (i_uc.startsWith("RELOAD ")) {
					this.sendOutput("You don't have any spare magazine rounds.");
				} else if (i_uc.startsWith("WIELD ")) {
					cmdUse(i.substring(6, i.length()));
				} else if (i_uc.startsWith("WIE ")) {
					cmdUse(i.substring(4, i.length()));
				} else if (i_uc.startsWith("EQUIP ")) {
					cmdUse(i.substring(6, i.length()));
				} else if (i_uc.startsWith("HOLD ")) {
					cmdUse(i.substring(5, i.length()));
				} else if (i_uc.startsWith("REMOVE ")) {
					cmdRemove(i.substring(7, i.length()));
				} else if (i_uc.startsWith("UNWIELD ")) {
					cmdRemove(i.substring(8, i.length()));
				} else if (i_uc.startsWith("FOLLOW ")) {
					this.sendOutput("Sorry, there is no 'follow' command.  If you want to follow someone, do it the old-fashioned way and walk after them.");
				} else if (i_uc.startsWith("MUG ")) {
					cmdMug(i.substring(4, i.length()));
				} else if (i_uc.startsWith("SHAG ")) {
					cmdShag(i.substring(5, i.length()));
				} else if (i_uc.startsWith("BONE ")) {
					cmdShag(i.substring(5, i.length()));
				} else if (i_uc.startsWith("FUCK ")) {
					cmdShag(i.substring(5, i.length()));
				} else if (i_uc.startsWith("BUM ")) {
					cmdShag(i.substring(4, i.length()));
				} else if (i.equalsIgnoreCase("FUCK")) {
					this.sendOutput("Who do you want to fuck?");
				} else if (i_uc.startsWith("BANG ")) {
					cmdShag(i.substring(5, i.length()));
				} else if (i_uc.startsWith("KISS ")) {
					cmdKiss(i.substring(5, i.length()));
				} else if (i.equalsIgnoreCase("DANCE") || i_uc.startsWith("DANCE ")) {
					this.character.dance();
				} else if (i.equalsIgnoreCase("BANG")) {
					this.sendOutput("Who do you want to bang?");
				} else if (i_uc.startsWith("ATTACK ")) {
					cmdAttack(i.substring(7, i.length()), "attack");
				} else if (i_uc.startsWith("ATT ")) {
					cmdAttack(i.substring(4, i.length()), "attack");
				} else if (i_uc.startsWith("MURDER ")) {
					cmdAttack(i.substring(7, i.length()), "try to murder");
				} else if (i_uc.startsWith("SLICE ")) {
					cmdAttack(i.substring(6, i.length()), "slice");
				} else if (i_uc.startsWith("FIGHT ")) {
					cmdAttack(i.substring(6, i.length()), "fight");
				} else if (i_uc.startsWith("STAB ")) {
					cmdAttack(i.substring(5, i.length()), "stab");
				} else if (i_uc.startsWith("CUT ")) {
					cmdAttack(i.substring(5, i.length()), "cut");
				} else if (i.equalsIgnoreCase("ATTACK")) {
					cmdActionWho("attack");
				} else if (i.equalsIgnoreCase("STAB")) {
					cmdActionWho("stab");
				} else if (i.equalsIgnoreCase("CUT")) {
					cmdActionWho("cut");
				} else if (i_uc.startsWith("PUNCH ")) {
					cmdAttack(i.substring(6, i.length()), "punch");
				} else if (i.equalsIgnoreCase("PUNCH")) {
					cmdActionWho("punch");
				} else if (i_uc.startsWith("SLAP ")) {
					cmdAttack(i.substring(5, i.length()), "slap");
				} else if (i.equalsIgnoreCase("SLAP")) {
					cmdActionWho("slap");
				} else if (i_uc.startsWith("HIT ")) {
					cmdAttack(i.substring(4, i.length()), "hit");
				} else if (i_uc.startsWith("HEADBUTT ")) {
					cmdAttack(i.substring(9, i.length()), "headbutt");
				} else if (i.equalsIgnoreCase("HIT")) {
					cmdActionWho("hit");
				} else if (i_uc.startsWith("KICK ")) {
					cmdAttack(i.substring(5, i.length()), "kick");
				} else if (i_uc.startsWith("BASH ")) {
					cmdAttack(i.substring(5, i.length()), "kick");
				} else if (i.equalsIgnoreCase("KICK")) {
					cmdActionWho("kick");
				} else if (i.equalsIgnoreCase("BASH")) {
					cmdActionWho("bash");
				} else if (i_uc.startsWith("KILL ")) {
					cmdAttack(i.substring(5, i.length()), "try to kill");
				} else if (i_uc.startsWith("KIL ")) {
					cmdAttack(i.substring(4, i.length()), "try to kill");
				} else if (i_uc.startsWith("K ")) {
					cmdAttack(i.substring(2, i.length()), "try to kill");
				} else if (i.equalsIgnoreCase("KILL")) {
					this.sendOutput("Who do you want to kill?");
				} else if (i_uc.startsWith("SHOOT ")) {
					cmdShoot(i.substring(6, i.length()));
				} else if (i.equalsIgnoreCase("SHOOT")) {
					cmdActionWho("shoot");
				} else if (i.equalsIgnoreCase("STAMINA")) {
					this.sendOutput("Stamina is not a stat in this game.  Type 'stats' to see your current stats.");
				} else if (i.equalsIgnoreCase("SKILLS")) {
					this.sendOutput("This game has no specific skill system.");
				} else if (i.equalsIgnoreCase("PRACTISE") || i.equalsIgnoreCase("PRAC")) {
					this.sendOutput("This game has no specific skill system.  You do not need to practise.");
				} else if (i.equalsIgnoreCase("TRAIN")) {
					this.sendOutput("This game has no specific skill system.  You do not need to train.");
				} else if (i_uc.startsWith("RAC")) {
					this.sendOutput("You are human.");
				} else if (i.equalsIgnoreCase("SAC CORPSE")) {
					this.sendOutput("You look in the corpse but there is nothing of value in it.");
				} else {
					if (this.character.getCurrentLocation().bespokeCommand(this.character, i) == false) {
						this.sendOutput("Sorry, I don't understand '" + i + "'.");
						this.users_input = new StringBuffer();
					}
				}
			} catch (IOException ex) {
				throw ex;
			} catch (Exception ex) {
				Server.HandleError(ex);
				System.err.println("Error: " + ex.getMessage());
				ex.printStackTrace();
//				this.sendOutput("Sorry, there was a problem parsing that command.");
			}
		}
	}

	private void cmdShag(String name) throws IOException {
		Location loc = this.character.getCurrentLocation();
		if (loc.containsCharacter(name)) {
			GameCharacter charac = (GameCharacter) loc.getCharacter(name);
			if (charac != this.character) {
				this.sendOutput("You attempt to shag the " + charac.name + ".");
				Server.game.informOthersOfMiscAction(this.character, charac, "The " + this.character.name +
						" attempts to shag the " + charac.name + ".");
				charac.shagAttemptedBy(this.character);
			} else {
				this.sendOutput("You want to shag yourself?");
			}
		} else if (name.equalsIgnoreCase("CORPSE")) {
			this.sendOutput("Sorry, necrophelia is illegal in San Torino.");
			Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " eye's up the corpse with the intention of making love to it.");
		} else {
			this.sendOutput("They are not here.");
		}
	}

	private void cmdMug(String name) throws IOException {
		Location loc = this.character.getCurrentLocation();
		if (loc.containsCharacter(name)) {
			GameCharacter charac = (GameCharacter) loc.getCharacter(name);
			if (charac != this.character) {
				this.sendOutput("You attempt to mug the " + charac.name + ".");
				Server.game.informOthersOfMiscAction(this.character, charac, "The " + this.character.name +
						" attempts to mug the " + charac.name + ".");
				charac.mugged(this.character);
				this.character.ensureWantedLevel(1);
			} else {
				this.sendOutput("You want to mug yourself?");
			}
		} else {
			this.sendOutput("Sorry, they are not here.");
		}
	}

	private void cmdKiss(String name) throws IOException {
		Location loc = this.character.getCurrentLocation();
		if (loc.containsCharacter(name)) {
			GameCharacter charac = (GameCharacter) loc.getCharacter(name);
			this.character.kiss(charac);
		} else if (name.equalsIgnoreCase(Corpse.NAME) && this.character.getCurrentLocation().containsItem(Corpse.NAME)) {
			this.sendOutput("You give the corpse a deep passionate kiss.");
			Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " gives the corpse a deep passionate kiss.");
		} else {
			this.sendOutput("They don't seem to be here.");
		}
	}

	private void cmdAttack(String name, String method) throws IOException {
		// Remove THE if they put it
		if (name.toUpperCase().startsWith("THE ")) {
			name = name.substring(4, name.length());
		}

		// Is it a door?
		if (name.equalsIgnoreCase("DOOR")) {
			this.sendOutput(ServerConn.ERROR_COL, "The door is solid.");
			Server.game.informOthersOfMiscAction(this.character, null, "The " + character.name + " tries to kick the door.");
			return;
		}

		// Is it a door?
		if (name.equalsIgnoreCase("CORPSE")) {
			this.sendOutput(ServerConn.ACTION_COL, "You stamp on the corpse.  It doesn't react.");
			Server.game.informOthersOfMiscAction(this.character, null, "The " + character.name + " stamps on the corpse.");
			return;
		}

		Location loc = this.character.getCurrentLocation();
		if (loc.containsCharacter(name)) {
			GameCharacter charac = loc.getCharacter(name);
			if (charac != this.character) {
				this.character.fight(charac, method);
			} else {
				this.sendOutput("You want to " + method + " yourself?");
			}
		} else if (name.toUpperCase().indexOf("SELF") >= 0) {
			this.sendOutput("Are you a masochist?  If you want to quit, enter QUIT.");
		} else {
			this.sendOutput("The " + name + " is not here.");
		}
	}

	private void cmdActionWho(String method) throws IOException {
		// See if there is only one other character here.
		if (this.character.getCurrentLocation().getNoOfCharacters() == 2) {
			Enumeration enumr = this.character.getCurrentLocation().getCharacterEnum();
			while (enumr.hasMoreElements()) {
				GameCharacter charac = (GameCharacter)enumr.nextElement();
				if (charac != this.character) {
					this.cmdAttack(charac.name, method);
					return;
				}
			}
		} else if (this.character.getCurrentLocation().getNoOfCharacters() > 2) {
			this.sendOutput("There is more than one person here.  Please specify who you would like to " + method + ".");
		} else {
			this.sendOutput("There is no-one else here.");
		}
	}

	private void cmdGreet(String charac_name) throws IOException {
		if (this.character.getCurrentLocation().containsCharacter(charac_name)) {
			GameCharacter charac = this.character.getCurrentLocation().getCharacter(charac_name);
			this.character.greet(charac);
		} else {
			this.sendOutput("The " + charac_name + " does not seem to be here.");
		}
	}

	private void cmdTouch(String charac_name) throws IOException {
		if (this.character.getCurrentLocation().containsCharacter(charac_name)) {
			GameCharacter charac = this.character.getCurrentLocation().getCharacter(charac_name);
			this.character.touch(charac);
		} else {
			this.sendOutput("The " + charac_name + " does not seem to be here.");
		}
	}

	private void cmdSay(String txt, boolean whisper) throws IOException {
		CSVString csv = new CSVString(txt, " ");
		String to_char = "";
		String msg = txt;
		// Are they talking to someone in particular?
		if (csv.getNoOfSections() > 2) {
			if (csv.getSection(0).equalsIgnoreCase("TO")) {
				to_char = csv.getSection(1);
				msg = csv.getSections(2, csv.getNoOfSections());
			} else if (csv.getNoOfSections() > 2 &&
					csv.getSection(csv.getNoOfSections() -
							2).equalsIgnoreCase("TO")) {
				to_char = csv.getSection(csv.getNoOfSections() - 1);
				msg = csv.getSections(0, csv.getNoOfSections() - 2);
			} else {
				msg = txt;
			}
		}
		if (msg.length() >= 0) {
			// If there's only one other char here, talk to them.
			if (to_char.length() == 0) {
				if (this.character.getCurrentLocation().getNoOfCharacters() == 2) {
					Enumeration enumr = this.character.getCurrentLocation().getCharacterEnum();
					while (enumr.hasMoreElements()) {
						GameCharacter other = (GameCharacter)enumr.nextElement();
						if (other != this.character) {
							to_char = other.name;
							break;
						}
					}
				}
			}

			if (to_char.length() > 0) {
				Location loc = this.character.getCurrentLocation();
				if (loc.containsCharacter(to_char)) {
					GameCharacter charac = loc.getCharacter(to_char);
					if (charac != this.character) {
						this.character.sayTo(charac, msg, whisper);
					} else {
						this.sendOutput("You talkin' to yourself?");
					}
				} else {
					this.sendOutput("You can't see the " + to_char + " here.");
				}
			} else { // No-one in particular
				this.character.say(msg);
				// See if any NPC's respond.  Only want to do this if it's an player talking!
				Enumeration enumr = this.character.getCurrentLocation().getCharacterEnum();
				while (enumr.hasMoreElements()) {
					GameCharacter charac = (GameCharacter)enumr.nextElement();
					if (charac.spokenTo(this.character, msg, false)) {
						return;
					}
				}
			}
		} else {
			this.sendOutput("What are you tryin' to say?");
		}
	}

	private void cmdChat(String txt) throws IOException {
		if (txt.length() > 0) {
			Enumeration enumr = Server.game.getCharacterEnum();
			while (enumr.hasMoreElements()) {
				GameCharacter charac = (GameCharacter)enumr.nextElement();
				if (charac instanceof PlayersCharacter) {
					PlayersCharacter pc = (PlayersCharacter) charac;
					pc.sendMsgToPlayer(ServerConn.NORMAL_COL, "CHAT: " + this.playername + ": " + txt);
				}
			}
		}
	}

	private void cmdMsgFromGM(String txt) throws IOException {
		if (txt.length() > 0) {
			if (this.isGM()) {
				for (int p=0 ; p<server.connections.size() ; p++) {
					ServerConn conn = (ServerConn)server.connections.get(p);
					conn.sendOutput("MSG FROM ADMIN: " + txt);
				}
			} else {
				this.sendOutput("You are not a administrator.");
			}
		}
	}

	/**
	 * give gun hench
	 * give hench gun
	 *
	 * @param txt
	 * @throws IOException
	 */
	private void cmdGive(String txt) throws IOException {
		CSVString csv = new CSVString(txt, " ");
		if (csv.getNoOfSections() == 2) {
			// Convert to correct phrase
			csv = new CSVString(csv.getSection(1) + " TO " + csv.getSection(0), " ");
		}

		if (csv.getNoOfSections() != 3) {
			this.sendOutput("Sorry, I don't quite understand.  Try something like 'Give [item or amount] to [person]'.  Enter 'HELP GIVE' for more help.");
		} else {
			String to_char = "";
			String item_name = "";
			if (this.character.getCurrentLocation().containsCharacter(csv.getSection(2))) {
				to_char = csv.getSection(2);
				item_name = csv.getSection(0);
			} else {
				to_char = csv.getSection(0);
				item_name = csv.getSection(2);
			}

			Location loc = this.character.getCurrentLocation();
			if (loc.containsCharacter(to_char)) {
				GameCharacter charac = loc.getCharacter(to_char);
				if (charac != this.character) {
					this.character.give(item_name, charac);
				} else {
					this.sendOutput("You want to give it to yourself?");
				}
			} else {
				this.sendOutput("You can't see the " + to_char + " here.");
			}
		}
	}

	private void cmdShoot(String name) throws IOException {
		Location loc = this.character.getCurrentLocation();
		if (loc.containsCharacter(name)) {
			GameCharacter charac = loc.getCharacter(name);
			if (charac != this.character) {
				this.character.shoot(charac);
			} else {
				this.sendOutput("You want to shoot yourself?");
			}
		} else {
			this.sendOutput("You can't see the "+name+" here.");
		}
	}

	private boolean cmdGet(String item_name) throws IOException {
		if (this.character.blinded == false) {
			Location loc = this.character.getCurrentLocation();
			if (item_name.equalsIgnoreCase("ALL") || item_name.equalsIgnoreCase("ALL CORPSE") || item_name.equalsIgnoreCase("ALL FROM CORPSE")) {
				if (this.character.getCurrentLocation().cash > 0) {
					this.cmdGet("CASH");
				}
				if (this.character.getCurrentLocation().getNoOfItems() > 0) {
					for (int i = 0;
					i < this.character.getCurrentLocation().getNoOfItems();
					i++) {
						Item item = (Item)this.character.getCurrentLocation().
						getItem(i);
						//if (item.carryable) {
							if (cmdGet(item.name)) {
								i--;
							}
							//}
					}
				} else {
					this.sendOutput("There are no items to pick up.");
				}
				return true;
			} else if (item_name.equalsIgnoreCase("CASH") ||
					item_name.equalsIgnoreCase("MONEY")) {
				this.character.getCash();
				return true;
			} else if (loc.containsItem(item_name)) {
				return this.character.pickupItem(item_name);
				/*Item item = (Item) loc.getItem(item_name);
              if (item.carryable) {
                  loc.removeItem(item);
                  this.character.addItem(item);
                  if (this.character.current_item == null) {
                      this.character.current_item = item;
                  }
                  this.sendOutput("You pick up the " + item.name + ".");
                  Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name +
                          " picks up the " + item.name + ".");
                  return true;
              } else {
                  this.sendOutput("You cannot take the " + item.name + ".");
                  Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name +
                          " tries and fails to take the " + item.name + ".");
                  return false;
              }*/
			} else if (loc.containsCharacter(item_name)) {
				GameCharacter charac = loc.getCharacter(item_name);
				this.sendOutput("The " + charac.name + " is a person!");
				return false;
			} else {
				this.sendOutput("I can't see a " + item_name + " here.");
				return false;
			}
		} else {
			this.sendOutput("You can't see anything.");
			return false;
		}
	}

	private void cmdBuy(String item_name) throws IOException {
		Server.game.informOthersOfMiscAction(this.character, null, this.character.name + " attempts to buy a "+ item_name+".");

		// See if any NPC's respond.
		Enumeration enumr = this.character.getCurrentLocation().getCharacterEnum();
		while (enumr.hasMoreElements()) {
			GameCharacter charac = (GameCharacter)enumr.nextElement();
			if (charac.attemptBuyFrom(this.character, item_name)) {
				return;
			}
		}

		// If we get here there's nothing to buy
		if (this.character.getCurrentLocation().containsCharacter(item_name)) {
			this.sendOutput("If you wish to hire someone, try giving them some money.");
		} else {
			this.sendOutput("No-one's selling one of those.");
		}
	}

	private void cmdRob() throws IOException {
		// See if any NPC's respond.
		Enumeration enumr = this.character.getCurrentLocation().getCharacterEnum();
		while (enumr.hasMoreElements()) {
			GameCharacter charac = (GameCharacter)enumr.nextElement();
			if (charac instanceof CanBeRobbed) {
				Server.game.informOthersOfMiscAction(this.character, null, this.character.name + " robs the " + this.character.getCurrentLocation().getName() + ".");
				CanBeRobbed cbr = (CanBeRobbed)charac;
				cbr.robbed(this.character);
				return;
			}
		}

		// If we get here there's nothing to rob
		Server.game.informOthersOfMiscAction(this.character, null, this.character.name + " tries and fails to rob the " + this.character.getCurrentLocation().getName() + ".");
		this.sendOutput("Sorry, there is nothing to rob here.");
	}

	private void cmdExamine(String item_name) throws IOException {
		if (this.character.blinded == false) {
			if (item_name.equalsIgnoreCase("ME") || item_name.equalsIgnoreCase("SELF")) {
				this.sendOutput(character.getDesc());
				this.sendOutput("Hint: You can change your description with the SET DESC command.");
			} else {
				Location loc = this.character.getCurrentLocation();
				// Is it an item on the floor?
				if (loc.containsItem(item_name)) {
					Item item = loc.getItem(item_name);
					this.sendOutput(item.getDesc());
					Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name +
							" examines the " + item.name + ".");
					return;
				}
				// Is it one we are carrying?
				if (this.character.hasItem(item_name.toUpperCase())) {
					Item item = (Item)this.character.getCharactersItem(item_name.
							toUpperCase());
					this.sendOutput(item.getDesc());
					Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name +
							" examines their " + item.name + ".");
					return;
				}
				// Is it a person here?
				if (loc.containsCharacter(item_name)) {
					GameCharacter charac = loc.getCharacter(item_name);
					this.sendOutput(charac.getDesc());
					if (this.character.bounty_hunter) {
						this.sendOutput("They have a wanted level of " + charac.getWantedLevel() + ".");
					}
					if (charac.current_item != null) {
						this.sendOutput("They are carrying a " +
								charac.current_item.name + ".");
					}
					Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name +
							" examines the " + charac.name + ".");
					return;
				}
				this.sendOutput("I can't see a " + item_name + " here.");
			}
		} else {
			this.sendOutput("You can't see anything at the moment!");
		}
	}

	private void cmdUse(String item_name) throws IOException {
		if (this.character.hasItem(item_name)) {
			Item item = this.character.getCharactersItem(item_name);
			if (item.use(this.character) == false) {
				this.character.current_item = item;
				this.sendOutput("You ready the " + item.name + ".");
				Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " wields their "+ item.name+".");
			}
		} else if (this.character.getCurrentLocation().containsItem(item_name)) {
			Item item = this.character.getCurrentLocation().getItem(item_name);
			if (item.use(this.character) == false) {
				this.sendOutput("You cannot use the " + item.name + ".");
				Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " tries and fails to use the "+ item.name+".");
			}
		} else {
			this.sendOutput("You cannot see a " + item_name + ".");
		}
	}

	private void cmdRemove(String item_name) throws IOException {
		if (this.character.current_item != null) {
			if (item_name.equalsIgnoreCase("GUN") && this.character.current_item instanceof RangedWeapon) {
				cmdRemove(this.character.current_item.name);
			} else if (this.character.current_item.name.equalsIgnoreCase(item_name)) {
				this.character.current_item = null;
				this.sendOutput("You remove the " + item_name + ".");
				Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " removes the "+ item_name+".");
			} else {
				this.sendOutput("You are not wielding a " + item_name + ".");
			}
		} else {
			this.sendOutput("You are not wielding anything.");
		}
	}

	/*  private void cmdEat(String item_name, String style) throws IOException {
      if (this.character.hasItem(item_name)) {
          this.character.eat(item_name, style);
      } else if (this.character.getCurrentLocation().containsItem(item_name)) {
          this.character.eat(item_name, style);
      } else {
        this.sendOutput("You don't have a " + item_name + ".");
      }
    }
	 */

	private void cmdInv() throws IOException {
		if (this.character.getNoOfItems() > 0) {
			this.sendOutput("You have: " + Server.CR + this.character.getItemList());
		} else {
			this.sendOutput("You are not carrying any items.");
		}
		if (this.character.cash > 0) {
			this.sendOutput("You have $"+this.character.cash+".");
		} else {
			this.sendOutput("You have no cash.");
		}
		if (this.character.current_item != null) {
			this.sendOutput("You are currently using a " + this.character.current_item.name + ".");
		}

		Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " checks their pockets.");
	}

	private void cmdCash() throws IOException {
		this.sendOutput("You have $"+this.character.cash+".");
	}

	private void cmdSetDesc(String desc) throws IOException {
		this.character.setDesc(desc);
		this.sendOutput("Your description is now '" + this.character.getDesc() + "'.");
	}

	private void cmdGold() throws IOException {
		this.sendOutput("You don't have any gold.  This isn't Dungeons & Dragons, y'know.");
	}

	private void cmdHelp() throws IOException {
		String str = TextFile.ReadAll(Game.DATA_DIR + "help.txt", Server.CR);
		this.sendOutput(str);
	}

	private void cmdSpecificHelp(String cmd) throws IOException {
		boolean found = false;
		TextFile tf = new TextFile();
		tf.openFile(Game.DATA_DIR + "help_commands.txt", TextFile.READ);
		while (tf.isEOF() == false) {
			String line = tf.readLine();
			if (line.length() > 0) {
				CSVString csv = new CSVString(line, "|");
				if (csv.getFirstSection().equalsIgnoreCase(cmd)) {
					found = true;
					this.sendOutput("Help for command '" + csv.getSection(0) + "':-");
					this.sendOutput(csv.getSection(1));
					break;
				}
			}
		}
		tf.close();

		if (!found) {
			this.sendOutput("Sorry, there is no help for that command.");
		}
	}

	private void cmdGasp() throws IOException {
		this.sendOutput("You gasp.");
		Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " gasps.");
	}

	private void cmdEmote(String style) throws IOException {
		this.sendOutput("You emote: " + style + ".");
		Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " " + style + ".");
	}

	private void cmdSimpleEmote(String type) throws IOException {
		this.sendOutput("You " + type + ".");
		Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " " + type + "s.");
	}

	private void cmdShowFile(String file) throws IOException {
		String str = TextFile.ReadAll(Game.DATA_DIR + file, Server.CR);
		this.sendOutput(str);
	}

	private void cmdWrite(String s) throws IOException {
		if (character.getCurrentLocation() == Server.game.start_loc) {
			String str = TextFile.ReadAll(SavedGames.SAVED_GAMES_DIR + Game.NOTICEBOARD, Server.CR);
			TextFile tf = new TextFile();
			tf.openFile(SavedGames.SAVED_GAMES_DIR + Game.NOTICEBOARD, TextFile.WRITE);
			tf.writeLine(str);
			tf.writeLine(this.playername + ": " + s);
			tf.close();
			this.sendOutput("You write '" + s + "' on the noticeboard.");

			Server.SendEmail("todo", "New noticeboard message", s);
		} else {
			this.sendOutput("There is no noticeboard here.");
		}
	}

	private void cmdSendMsgToAdmin(String msg) throws IOException {
		LogFile admin = new LogFile("./logs/" + ADMIN_MSG);
		admin.append("MESSAGE FROM " + this.character.players_name  + "/" + this.character.name + "/" + this.getINetAddress().toString());
		admin.append(msg);
		admin.append("");
		this.sendOutput("Message sent.");
		Server.SendEmail("todo", "New Admin Message", msg);
	}

	private void cmdCredits() throws IOException {
		this.sendOutput("GTA-MUD was designed and written by Stephen Carlyle-Smith (stephen.carlylesmith@googlemail.com).");
	}

	private void cmdLook() throws IOException {
		this.sendOutput(this.character.getCurrentLocation().getDesc(this.character, this.isGM()));
	}

	private void cmdTime() throws IOException {
		this.sendOutput("It is " + Server.game.game_time.getTimeAsString() + " San Torino local time.");
		Server.game.informOthersOfMiscAction(this.character, null, "The " + this.character.name + " looks at their watch.");
	}

	private void cmdWho() throws IOException {
		this.sendOutput("There are " + (server.connections.size()-1) + " other players currently connected.");
		for (int p=0 ; p<server.connections.size() ; p++) {
			ServerConn conn = (ServerConn)server.connections.get(p);
			if (conn != this) {
				this.sendOutput("Players Name: " + conn.playername +
						"  online for " +
						Functions.GetMinutesSince(conn.started_time) +
						" mins.  Last input was " +
						Functions.GetMinutesSince(conn.last_input_time) +
				" mins ago.");
				if (this.isGM()) {
					this.sendOutput("GM Info:");
					if (conn.character != null) {
						if (conn.character.getHealth() > 0) {
							if (conn.character.getCurrentLocation() != null) {
								this.sendOutput("Character: " + conn.character.name);
								this.sendOutput("Location: " +
										conn.character.getCurrentLocation().
										getName());
							} else {
								this.sendOutput("No current location.");
							}
						} else {
							this.sendOutput("Character is dead.");
						}
					} else {
						this.sendOutput("No current character.");
					}
					this.sendOutput("IP: " + conn.sck.getInetAddress().toString());
				}
				this.sendOutput("");
			}
		}
		if (server.connections.size() > 1) {
			this.sendOutput("Hint: Use 'CHAT' to communicate globally.");
		}
	}

	public boolean isGM() {
		return (this.sck.getInetAddress().toString().indexOf("127.0.0.1") >= 0);
	}

	private void cmdStats() throws IOException {
		this.sendOutput("You have $"+this.character.cash+".");
		this.sendOutput("You have "+this.character.getHealth() +" health.");
		this.sendOutput("Your wanted level is "+this.character.getWantedLevel() +".");
		if (this.character.current_item != null ) {
			this.sendOutput("You are currently using a " +
					this.character.current_item.name + ".");
		}

	}

	private void getPriceOf(String item_name) throws IOException {
		try {
			String sentence_case = item_name.substring(0, 1).toUpperCase() + item_name.substring(1, item_name.length()).toLowerCase();
			Item item = (Item) (Class.forName("gta_text.items." + sentence_case).newInstance());
			this.sendOutput(ServerConn.NORMAL_COL, "The " + item.name + " cost $" + item.cost + " each.");
		} catch (Exception ex) {
			this.sendOutput(ServerConn.ERROR_COL, "Sorry, no price could be found for the " + item_name);
			Server.HandleError(ex);
		}

	}

}
