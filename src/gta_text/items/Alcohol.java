package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;

public abstract class Alcohol extends Drink {

    private int drunk_inc;

    public Alcohol(int type, int quench, int alc) throws IOException {
        super(type, quench);
        this.drunk_inc = alc;
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.drunkness += this.drunk_inc;
        return true;
    }


}
