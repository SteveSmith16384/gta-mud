/*
 * Created on 17-May-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;
import gta_text.items.Alcohol;
import java.io.IOException;
import gta_text.ServerConn;

public class Shot extends Alcohol {

    public Shot() throws IOException {
        super(Item.SHOT, Drink.THIRST_QUENCH, 6);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the shot.  It burns the back of your throat, but it sure does taste nice.");
        //character.removeItem(this);
        return true;
    }
}
