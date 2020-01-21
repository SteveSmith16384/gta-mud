/*
 * Created on 27-Jun-2006
 *
 */
package gta_text.npcs;

import gta_text.locations.HotelReception;
import gta_text.locations.Location;
import gta_text.*;
import java.io.IOException;
import ssmith.lang.Functions;

public class HotelReceptionist extends NonPlayerCharacter {

    public static final String NAME = "Receptionist";

    public HotelReceptionist(Location loc) throws IOException {
        super(NonPlayerCharacter.HOTEL_RECEPTIONIST, NAME, loc);
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        if (character instanceof PlayersCharacter) {
            this.sayTo(character, "Would you like a room for the night?  It costs $" + HotelReception.ROOM_COST + " to save your game.  Please note that the security box only has room for one item.");
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "I'll call the manager!";
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch(x) {
        case 1:
            Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " takes a phone call.");
            break;
        case 2:
            Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " sorts out some mail.");
            break;
        case 3:
            this.say("Would you like to book a room?");
            break;
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (msg.toUpperCase().startsWith("NO ") || msg.equalsIgnoreCase("NO")) {
            this.sayTo(charac, "Okay.");
            return true;
        } else if (msg.toUpperCase().startsWith("YES ") || msg.equalsIgnoreCase("YES")) {
            this.sayTo(charac, "Please enter 'save' to if you want a room.");
            return true;
        } else {
            return super.spokenTo(charac, msg, only_them);
        }
    }

}
