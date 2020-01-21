package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.Server;
import gta_text.ServerConn;

public class Poo extends Food {

    public static final String NAME = "Poo";

    public Poo() throws IOException {
        super(Item.POO);
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ERROR_COL, "And how do you do that exactly?");
        return true;
    }

    public boolean eat(GameCharacter charac, String style) throws IOException {
    charac.sendMsgToPlayer(ServerConn.ACTION_COL, "Yuck!");
    Server.game.informOthersOfMiscAction(charac, null, "The " + charac.name + " starts to eat the poo.");
    return false;
}


}
