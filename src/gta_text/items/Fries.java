package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class Fries extends Food {

    public Fries() throws IOException {
        super(Item.FRIES);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You eat the fries.  All you can taste is salt.  You need a drink!");
        character.reduceThirst(-50);
        //character.removeItem(this);
        return true;
    }

}
