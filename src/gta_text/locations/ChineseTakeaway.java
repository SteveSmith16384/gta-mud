package gta_text.locations;

import java.io.IOException;
import gta_text.npcs.TakeawayAssistant;

public class ChineseTakeaway extends Location {

    public ChineseTakeaway(String no) throws IOException {
        super(no);

        new TakeawayAssistant(this);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(TakeawayAssistant.NAME) == false) {
            new TakeawayAssistant(this);
        }
    }

}
