package gta_text.items;

import java.io.IOException;
import gta_text.Server;
import gta_text.npcs.GameCharacter;
import ssmith.lang.Functions;
import gta_text.ServerConn;

public class Beer extends Alcohol {

    public static final String NAME = "Beer";

    public Beer() throws IOException {
        super(Item.BEER, Drink.THIRST_QUENCH, 4);
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You drink the beer.  Mmm, that's refreshing.");
        //character.removeItem(this);

        if (Functions.rnd(1, 2) == 1) {
            character.sendMsgToPlayer(ServerConn.ACTION_COL, "You burp.");
            Server.game.informOthersOfMiscAction(character, null, "The " + character.name + " burps.");
        }
        return true;
    }

}
