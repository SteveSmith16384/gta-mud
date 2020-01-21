package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;

public class Poster extends Item {

    public Poster(String words) throws IOException {
        super(Item.POSTER);

        this.desc = words;
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
