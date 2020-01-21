/*
 * Created on 12-May-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;
import gta_text.ServerConn;

public class Burger extends Food {

    public Burger() throws IOException {
        super(Item.BURGER);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You eat the burger.  It tastes of soggy cardboard with a special sauce, but it sorts out your hunger temporarily.");
        //character.removeItem(this);
        return true;
    }

}
