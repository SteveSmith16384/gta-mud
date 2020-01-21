/*
 * Created on 20-Jun-2006
 *
 */
package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;

public class Picture extends Item {
    
    public static final String NAME = "Picture";

    public Picture(String desc) throws IOException {
        super(Item.PICTURE);
        this.desc = desc;
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
