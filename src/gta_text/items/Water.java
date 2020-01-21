/*
 * Created on 16-Jun-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;
import gta_text.ServerConn;

public class Water extends Drink {

    public static final String NAME = "Water";

    public Water() throws IOException {
        super(Item.WATER, Drink.THIRST_QUENCH*2);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the water.  It tastes just like water, but it really quenches your thirst.");
        //character.removeItem(this);
        return true;
    }


}
