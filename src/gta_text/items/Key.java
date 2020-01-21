package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class Key extends Item {

    public Key() throws IOException {
        super(Item.KEY);
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ERROR_COL, "You can't find anywhere to put the key."); // todo - use in vault
        return true;
    }

}
