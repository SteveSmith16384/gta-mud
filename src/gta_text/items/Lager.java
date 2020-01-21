package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;
import gta_text.Server;
import ssmith.lang.Functions;
import gta_text.ServerConn;

public class Lager extends Alcohol {

    public Lager() throws IOException {
        super(Item.LAGER, Drink.THIRST_QUENCH, 4);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the lager.  It tastes of flat lager, but refreshing nonetheless.");
        //character.removeItem(this);

        if (Functions.rnd(1, 2) == 1) {
            character.sendMsgToPlayer(ServerConn.ACTION_COL, "You burp.");
            Server.game.informOthersOfMiscAction(character, null, "The " + character.name + " burps.");
        }
        return true;
    }

}
