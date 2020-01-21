package gta_text.items;

import gta_text.npcs.GameCharacter;
import gta_text.Server;
import java.io.IOException;
import gta_text.ServerConn;

public class Cigs extends Item {

    public static final String NAME = "Cigs";

    public Cigs() throws IOException {
        super(Item.CIGS);
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You light a cigarette and take a puff.");
        Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, character, null, "The " + character.name + " takes a drag of a cigarette.");
        //character.increaseHealth(-1, null);
        return true;
    }


}
