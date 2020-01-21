package gta_text;

import ssmith.lang.Functions;
import ssmith.mail.MailAddress;
import ssmith.mail.SmtpMail;
import ssmith.io.TextFile;
import ssmith.util.Interval;
import gta_text.items.Item;
import gta_text.locations.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.IOException;
import gta_text.npcs.*;
import gta_text.mission.*;
import gta_text.items.Plaque;

public class Game extends AbstractGame {

	private static final long LOOP_DELAY = 1500;
	public static final String DATA_DIR = "./data/";
	public static final String SEPERATOR = "|";
	public static final String SUB_SEPERATOR = "~";
	public static final String NOTICEBOARD = "noticeboard.txt";

	private Interval process_locs_10_secs_interval = new Interval(10000);
	private Interval process_regens_5_mins_interval = new Interval(1000*60*5);
	private Interval process_missions_8_mins_interval = new Interval(1000*60*8);
	public long last_death_time = System.currentTimeMillis();
	public String last_death_description = "More deaths in San Torino.";
	public boolean fight_occurred = false;
	private Server server;

	public ArrayList missions = new ArrayList();
	public KillMafia kill_mafia_mission = new KillMafia(missions);
	public GetATMCard get_cashcard_mission = new GetATMCard(missions);
	public DrugsBust drugs_bust_mission = new DrugsBust(missions);

	// Special locations
	public Location start_loc, hospital, jail, rl_district, nightclub;
	public Location mafia_house, gambling_room, park, mansion, hotel_reception;
	public Location laundrette;

	public PoliceStation police_station;
	public AbandonedWarehouse abandoned_warehouse;
	public MassageParlour parlour;

	public Game(Server serv) throws IOException {
		server = serv;
	}

	public void game_loop() throws IOException {
		// Reset any missions
		for (int x=0 ; x<missions.size() ; x++) {
			Mission miss = (Mission)missions.get(x);
			miss.reset();
			miss.completed = false;
		}

		// Main game loop
		System.out.println("Game started.");
		while (true) {
			try {
				long start = System.currentTimeMillis();

				if (this.server.connections.size() > 0) {
					game_time.process();
					game_weather.process();

					Enumeration enumr = null;

					// Process characters - players first, send blank line
					for(int i=0 ; i<server.connections.size() ; i++) {
						ServerConn conn = (ServerConn)server.connections.get(i);
						conn.rcvd_blank_line = false;
					}

					enumr = characters.elements();
					while (enumr.hasMoreElements()) {
						GameCharacter charac = (GameCharacter) enumr.nextElement();
						if (charac instanceof PlayersCharacter) {
							charac.process();
						}
					}

					enumr = characters.elements();
					while (enumr.hasMoreElements()) {
						GameCharacter charac = (GameCharacter) enumr.nextElement();
						if (charac instanceof PlayersCharacter == false) {
							charac.process();
						}
					}

					// Process locations
					if (process_locs_10_secs_interval.hitInterval()) {
						enumr = locations.elements();
						while (enumr.hasMoreElements()) {
							Location loc = (Location) enumr.nextElement();
							loc.process();
						}
					}

					// Process items
					for(int i=0 ; i<items.size() ; i++) {
						Item item = (Item)items.get(i);
						item.process();
					}

					// Regens
					if (process_regens_5_mins_interval.hitInterval()) {
						regens();
					}

					// Misc
					if (this.cops_needed_at != null) {
						if (Functions.rnd(1, 10) == 1) {
							new Policeman(this.cops_needed_at);
							cops_needed_at = null;
						}
					}

					if (this.army_needed_at != null) {
						if (Functions.rnd(1, 10) == 1) {
							new Soldier(this.army_needed_at);
							new Soldier(this.army_needed_at);
							new Soldier(this.army_needed_at);
							army_needed_at = null;
						}
					}

					// Missions
					if(process_missions_8_mins_interval.hitInterval()) {
						for (int x=0 ; x<missions.size() ; x++) {
							Mission miss = (Mission)missions.get(x);
							if (miss.completed) {
								miss.reset();
								miss.completed = false;
							}
						}
					}
				}

				long time_taken = System.currentTimeMillis() - start;
				long wait = LOOP_DELAY - time_taken;
				Functions.delay(wait);

				if (fight_occurred) {
					// Wait a bit longer
					Functions.delay(2500);
					fight_occurred = false;
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
				e.printStackTrace();
				Server.HandleError(e);
			}
		}
	}

	private void regens() throws IOException {
		while (Mugger.total_muggers < 1) {
			Location loc = getRandomLocation(true);
			new Mugger(loc);
		}

		while (Stalker.total_stalkers < 1) {
			Location loc = getRandomLocation(true);
			new Stalker(loc);
		}

		while (Mercenary.total_gangmembers < (this.locations.size()/8)) {
			Location loc = getRandomLocation(true);
			int x = Functions.rnd(1, 5); // Thug, hitman, criminal, assassin
			switch (x) {
			case 1:
				new Mercenary("Gangster", loc);
				break;
			case 2:
				new Mercenary("Mercenary", loc);
				break;
			case 3:
				new Mercenary("Henchman", loc);
				break;
			case 4:
				new Mercenary("BountyHunter", loc);
				break;
			case 5:
				new Mercenary("HiredGun", loc);
				break;
			default:
				System.err.println("No name for hitman.");
			break;
			}
		}

		Enumeration enumr = locations.elements();
		while (enumr.hasMoreElements()) {
			Location loc = (Location) enumr.nextElement();
			if (loc.containsPlayer() == false) {
				loc.regenChars();
			}
		}

		// Check if too many police
		if (Policeman.total_police > Policeman.GetMaxPolice()) {
			enumr = characters.elements();
			while (enumr.hasMoreElements()) {
				GameCharacter charac = (GameCharacter) enumr.nextElement();
				if (charac instanceof Policeman) {
					if (charac.getCurrentLocation().containsPlayer() == false) {
						charac.removeFromGame();
						break;
					}
				}
			}
		}

		// Check if too many army
		if (Server.game.GetHighestWantedLevel() < AbstractGame.CALL_ARMY_IN_LEVEL) {
			enumr = characters.elements();
			while (enumr.hasMoreElements()) {
				GameCharacter charac = (GameCharacter) enumr.nextElement();
				if (charac instanceof Soldier) {
					if (charac.getCurrentLocation().containsPlayer() == false) {
						charac.removeFromGame();
					}
				}
			}
		}

	}

	public void loadGame() throws IOException {
		loadLocations(); // Must be first so we can add items and characters to them
	}

	private void loadLocations() throws IOException {
		int LOC_NO=1, LOC_INTERNAL=2, LOC_WANDERABLE=3, LOC_NAME=4, LOC_DESC=5, LOC_EXITS = 6;

		locations = new Hashtable();

		TextFile tf = new TextFile();
		tf.openFile(DATA_DIR + "locations.txt", TextFile.READ);
		tf.readLine();
		while (tf.isEOF() == false) {
			String line = tf.readLine();
			if (line.startsWith("#") == false && line.length() > 0) {
				int no = new Integer(Functions.GetParam(LOC_NO, line, SEPERATOR)).intValue();
				Location location = null;
				if (no == Location.BRIDGE) {
					location = new Bridge(""+no);
					start_loc = location;
				} else if (no == Location.MF_CHICKEN) {
					location = new MFChicken(""+no);
				} else if (no == Location.NIGHTCLUB) {
					location = new Nightclub(""+no);
					nightclub = location;
				} else if (no == Location.POLICE_STATION) {
					location = new PoliceStation(""+no);
					police_station = (PoliceStation)location;
				} else if (no == Location.HOSPITAL) {
					location = new Hospital(""+no);
					this.hospital = location;
				} else if (no == Location.REDLIGHT_DISTRICT) {
					location = new RedlightDistrict(""+no);
					this.rl_district = location;
				} else if (no == Location.AMMONATION) {
					location = new AmmoNation(""+no);
				} else if (no == Location.ABANDONED_WAREHOUSE) {
					location = new AbandonedWarehouse(""+no);
					abandoned_warehouse = (AbandonedWarehouse)location;
				} else if (no == Location.HC_CAFE) {
					location = new HCCafe(""+no);
				} else if (no == Location.MAFIA_HOUSE) {
					location = new BasicLocation(""+no);
					mafia_house = location;
				} else if (no == Location.BANK) {
					location = new Bank(""+no);
				} else if (no == Location.BANK_VAULT) {
					location = new BankVault(""+no);
				} else if (no == Location.GAMBLING_ROOM) {
					location = new BasicLocation(""+no);
					gambling_room = location;
				} else if (no == Location.CASINO) {
					location = new Casino(""+no);
				} else if (no == Location.PARK) {
					location = new Park(""+no);
					park = location;
				} else if (no == Location.ROULETTE_ROOM) {
					location = new RouletteRoom(""+no);
				} else if (no == Location.SLOT_ROOM) {
					location = new SlotsRoom(""+no);
				} else if (no == Location.DANCEFLOOR) {
					location = new Dancefloor(""+no);
				} else if (no == Location.CHINESE_TAKEAWAY) {
					location = new ChineseTakeaway(""+no);
				} else if (no == Location.MANSION) {
					location = new Mansion(""+no);
					mansion = location;
				} else if (no == Location.BEDROOM) {
					location = new Bedroom(""+no);
				} else if (no == Location.BAR_TOILETS) {
					location = new Toilets(""+no, "East");
				} else if (no == Location.MASSAGE_PARLOUR) {
					parlour = new MassageParlour(""+no);
					location = parlour;
				} else if (no == Location.CASINO_TOILETS) {
					location = new Toilets(""+no, "North");
				} else if (no == Location.HOTEL_RECEPTION) {
					hotel_reception = new HotelReception(""+no);
					location = hotel_reception;
				} else if (no == Location.PAWN_SHOP) {
					location = new PawnShop(""+no);
				} else if (no == Location.PIZZA_AL_FORNICATE) {
					location = new PizzaAlFornicate(""+no);
				} else if (no == Location.LAUNDRETTE) {
					laundrette = new Laundrette(""+no);
					location = laundrette;
				} else if (no == Location.PORTLAND_WAY) {
					location = new PortlandWay(""+no);
				} else {
					location = new BasicLocation(""+no);
					//throw new IOException("Location type " + no + " not defined.");
				}

				location.setName(Functions.GetParam(LOC_NAME, line, SEPERATOR));
				location.internal = Functions.GetParam(LOC_INTERNAL, line, SEPERATOR).equalsIgnoreCase("Y");
				location.wanderable = Functions.GetParam(LOC_WANDERABLE, line, SEPERATOR).equalsIgnoreCase("Y");
				location.setDesc(Functions.GetParam(LOC_DESC, line, SEPERATOR));
				String exits = Functions.GetParam(LOC_EXITS, line, SEPERATOR);
				int no_of_exists = Functions.GetNoOfParams(exits, SUB_SEPERATOR);
				for (int e=0 ; e<no_of_exists ; e++) {
					String dir_param = Functions.GetParam(e+1, exits, SUB_SEPERATOR);
					String dir = dir_param.substring(0, 1);
					String no2;
					if (dir_param.endsWith("X")) {
						no2 = dir_param.substring(1, dir_param.length()-1);
					} else {
						no2 = dir_param.substring(1, dir_param.length());
						location.visible_exits.put(dir.toUpperCase(), no2);
					}
					location.all_exits.put(dir.toUpperCase(), no2);
				}

				// Misc
				if (no == 22) { //todo - move to own location
					location.addItem(new Plaque());
				}
			}
		}
		tf.close();

		// Check special locations have been set
		if (start_loc == null) {
			System.err.println("Warning: start_location not set.");
		}
	}

}
