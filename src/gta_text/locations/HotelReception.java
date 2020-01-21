/*
 * Created on 27-Jun-2006
 *
 */
package gta_text.locations;

import gta_text.admin.SavedGames;
import gta_text.npcs.GameCharacter;
import gta_text.npcs.HotelReceptionist;
import gta_text.npcs.PlayersCharacter;

import java.io.IOException;
import gta_text.ServerConn;

public class HotelReception extends Location {

    public static final int ROOM_COST = 40;

    public HotelReception(String n) throws IOException {
        super(n);

        new HotelReceptionist(this);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(HotelReceptionist.NAME) == false) {
            new HotelReceptionist(this);
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.toUpperCase().startsWith("SAVE")) {
            if (character.cash >= ROOM_COST) {
                character.cash -= ROOM_COST;
                SavedGames games = new SavedGames();
                games.saveCharacter((PlayersCharacter)character);
                character.sendMsgToPlayer(ServerConn.ACTION_COL, "Game succesfully saved.  $" + ROOM_COST + " charged.");
            } else {
                character.sendMsgToPlayer(ServerConn.ERROR_COL, "I'm sorry.  It costs $" + ROOM_COST + " for a room for the night and you don't seem to have enough.");
            }
            return true;
        }
        return false;
    }


}
