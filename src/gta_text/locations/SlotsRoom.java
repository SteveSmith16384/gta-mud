package gta_text.locations;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.npcs.*;
import gta_text.ServerConn;

public class SlotsRoom extends Location {

    public SlotsRoom(String no) throws IOException {
        super(no);

        new GamblingLady(this);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(GamblingLady.NAME) == false) {
            new GamblingLady(this);
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.toUpperCase().startsWith("PUT ")) {
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "All the slot machines are currently being used by old ladies.  Please try again later.");
            return true;
        } else if (cmd.toUpperCase().startsWith("GAMBLE") || cmd.toUpperCase().startsWith("PLAY")) {
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "All the slot machines are currently being used by old ladies.  Please try again later.");
            return true;
        }
        return false;
    }
}
