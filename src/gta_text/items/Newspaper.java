package gta_text.items;

import gta_text.Server;
import gta_text.npcs.GameCharacter;
import java.io.IOException;
import gta_text.ServerConn;

public class Newspaper extends Item {

    public static final String NAME = "Newspaper";

    public Newspaper() throws IOException {
        super(Item.NEWSPAPER);
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.NORMAL_COL, getDesc());
        return true;
    }

    public String getDesc() {
        long hrs = System.currentTimeMillis() - Server.game.last_death_time;
        hrs = hrs/1000/60/60;
        return "It's the latest edition of the San Torino Times.  It says: '" + Server.game.last_death_description;// + "  But on the lighter side, there have been no murders in San Torino For " + hrs + " hours!'";
    }

}
