package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;

public class CashCard extends Item {

    public static final String NAME = "CashCard";
    
    public CashCard() throws IOException {
        super(Item.CASHCARD);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

}
