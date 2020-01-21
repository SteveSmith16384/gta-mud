package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;

public class Knife extends Item {

    public Knife() throws IOException {
        super(Item.KNIFE);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
