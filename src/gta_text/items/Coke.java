/*
 * Created on 16-Jun-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;
import gta_text.ServerConn;

public class Coke extends Drink {

    public Coke() throws IOException {
        super(Item.COKE, Drink.THIRST_QUENCH);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the coke.  It's coldness makes your teeth tingle, but it quenches your thirst.");
        //character.removeItem(this);
        return true;
    }


}
