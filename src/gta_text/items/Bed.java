/*
 * Created on 20-Jun-2006
 *
 */
package gta_text.items;

import gta_text.locations.Location;
import gta_text.npcs.GameCharacter;
import gta_text.*;
import java.io.IOException;
import java.util.Enumeration;

public class Bed extends Item {

    public static final String NAME = "Bed";

    public Bed() throws IOException {
        super(Item.BED);
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You lie on the bed.");
        Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, character, null, "The " + character.name + " lies on the bed.");
        
        // tell others (in case it's a mssr)
        GameCharacter charac;
        Location loc = character.getCurrentLocation();
        Enumeration enumr = loc.getCharacterEnum();
        while (enumr.hasMoreElements()) {
            charac = (GameCharacter) enumr.nextElement();
            if (charac != character) {
                charac.seenCharacterUseBed(character);
            }
        }

        return true;
    }

}
