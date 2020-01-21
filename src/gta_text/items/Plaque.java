package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;

public class Plaque extends Item {

    public Plaque() throws IOException {
        super(Item.PLAQUE);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
