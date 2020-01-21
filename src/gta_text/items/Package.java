package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;

public class Package extends Item{

    public Package() throws IOException {
        super(Item.PACKAGE);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
