package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class GAndT extends Alcohol {

    public GAndT() throws IOException {
        super(Item.GANDT, Drink.THIRST_QUENCH, 5);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the G&T.  It's a bit sour.");
        //character.removeItem(this);
        return true;
    }

}
