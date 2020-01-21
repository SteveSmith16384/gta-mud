/*
 * Created on 03-Jul-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;

public class MeatCleaver extends Item {

    public MeatCleaver() throws IOException {
        super(Item.CLEAVER);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }
    
}

