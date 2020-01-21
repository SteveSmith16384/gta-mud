package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class Noodles extends Food {

    public Noodles() throws IOException {
        super(Item.NOODLES);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You eat the noodles.  They taste nice, but you manage to get sauce all around your mouth..");
        //character.removeItem(this);
        return true;
    }
}
