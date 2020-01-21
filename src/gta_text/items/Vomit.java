package gta_text.items;

import gta_text.Server;
import gta_text.ServerConn;
import gta_text.npcs.GameCharacter;
import java.io.IOException;

public class Vomit extends Food {

    public Vomit() throws IOException {
        super(Item.VOMIT);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

    public boolean eat(GameCharacter charac, String style) throws IOException {
        charac.sendMsgToPlayer(ServerConn.NORMAL_COL, "You sit down and pick out the bits of carrot from the yellow custard, savouring the aftertaste of bile.");
        Server.game.informOthersOfMiscAction(charac, null, "The " + charac.name + " starts to eat the vomit off the floor.");
        return false;
    }

}
