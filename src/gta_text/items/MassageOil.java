/*
 * Created on 21-Jun-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;
import gta_text.*;
import java.io.IOException;

public class MassageOil extends Item {

    public static final String NAME = "MassageOil";

    public MassageOil() throws IOException {
        super(Item.MASSAGE_OIL);
    }

    public boolean eat(GameCharacter charac, String style) throws IOException {
        charac.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the oil.  Yuk, it tastes awful, and you feel a bit queasy.");
        return false;
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You rub massage oil all over yourself.");
        Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, character, null, "The " + character.name + " rubs massage oil all over themselves.");
        return true;
    }


}
