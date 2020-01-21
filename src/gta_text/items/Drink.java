/*
 * Created on 16-Jun-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;
import gta_text.ServerConn;

public abstract class Drink extends Item {

    public static final int THIRST_QUENCH = 300;

    private int thirst_quench;

    public Drink(int type, int quench) throws IOException {
        super(type);

        this.thirst_quench = quench;
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.ERROR_COL, "Try drinking it.");
        return true;
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.reduceThirst(this.thirst_quench);
        return true;
    }



}
