/*
 * Created on 21-Jun-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;
import gta_text.*;

import java.io.IOException;

public class Toilet extends Item {

    private String dir;

    public Toilet(String d) throws IOException {
        super(Item.TOILET);
        this.dir = d;
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You sit on the toilet and drop a bomb.  Poo-ey!");
        Server.game.informOthersOfMiscAction(character, null, "The " + character.name + " does a poo on the toilet.  Your eyes begin to sting from the stench.");
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "However, you notice that the toilet is loose, and there seems to be a gap in the wall behind it (to the " + dir + ") just big enough to squeeze through.");
        return true;
    }

    public boolean eat(GameCharacter charac, String style) throws IOException {
        charac.sendMsgToPlayer(ServerConn.NORMAL_COL, "You kneel down in front of the toilet and start to lick the rim.");
        Server.game.informOthersOfMiscAction(charac, null, "The " + charac.name + " kneels down in front of the toilet and starts to lick the rim.");
        return false;
    }
}
