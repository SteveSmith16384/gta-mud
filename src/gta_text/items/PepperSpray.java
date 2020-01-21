package gta_text.items;

import gta_text.Server;
import gta_text.npcs.GameCharacter;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.ServerConn;

public class PepperSpray extends Item implements ISpecialAttack {

    public PepperSpray() throws IOException {
        super(Item.PEPPER_SPRAY);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

    public void attack(GameCharacter user, GameCharacter target) throws
            IOException {
        if (Functions.rnd(1, 2) == 1) {
            target.setBlinded(1000*60*2);
            target.sendMsgToPlayer(ServerConn.COMBAT_COL, "You have been blinded by the pepper spray!".toUpperCase());
            Server.game.informOthersOfMiscAction(user, target, "The " + user.name + " sprays pepper spray in the eyes of the " + target.name + ".");
        }
    }
}
