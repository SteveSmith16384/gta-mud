package gta_text.locations;

import java.io.IOException;
import gta_text.npcs.PawnBroker;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class PawnShop extends Location {

    public PawnShop(String no) throws IOException {
        super(no);

        new PawnBroker(this);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(PawnBroker.NAME) == false) {
            new PawnBroker(this);
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.toUpperCase().startsWith("SELL")) {
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "If you wish to sell something, just give it to the broker.");
            return true;
        }
        return false;
    }
}
