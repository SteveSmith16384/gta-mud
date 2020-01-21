/*
 * Created on 10-May-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;

public class Grenade extends Item {

    public Grenade() throws IOException {
        super(Item.GRENADE);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
