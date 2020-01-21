package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;

public class Leaflet extends Item {

    public Leaflet(String dsc) throws IOException {
        super(Item.LEAFLET);
        this.desc = dsc;
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
