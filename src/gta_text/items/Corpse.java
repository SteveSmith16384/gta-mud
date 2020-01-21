
package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;
import gta_text.*;
import ssmith.lang.Functions;
import gta_text.locations.Location;

public class Corpse extends Item {

	private static final long DURATION = 1000*60*10; // 10 mins
	public static final String NAME = "Corpse";

	private long expire_time;
	public GameCharacter orig_char;

	public Corpse(GameCharacter character) throws IOException {
		super(Item.CORPSE);
		this.orig_char = character;
		this.desc = "It is the still-warm corpse of a " + character.name + ".  There is nothing of value on it.";
		this.expire_time = System.currentTimeMillis() + DURATION;
	}

	public void process() throws IOException {
		if (System.currentTimeMillis() > this.expire_time) {
			this.removeFromGame(true, true);
		} else if (Functions.rnd(1, 160) == 1) {
			Location loc = this.getLocationSlow();
			if (loc != null) {
				Server.game.informAllCharsInLocation(loc,
						"The corpse twitches.");
			}
		}
	}

	public boolean use(GameCharacter character) throws IOException {
		return false;
	}

	public boolean eat(GameCharacter charac, String style) throws IOException {
		charac.sendMsgToPlayer(ServerConn.NORMAL_COL, "You try to eat it, but the gangrene and maggots puts you off.");
		Server.game.informOthersOfMiscAction(charac, null, "The " + charac.name + " starts to eat the corpse.");
		return false;
	}

}
