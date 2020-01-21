package gta_text.locations;

import java.io.IOException;
import gta_text.items.Poo;
import gta_text.npcs.Flasher;

public class Park extends Location {

    public Park(String no) throws IOException {
        super(no);

        this.addItem(new Poo());
}

    public void regenChars() throws IOException {
        if (this.containsCharacter("FLASHER") == false) {
            new Flasher(this);
        }

        if (this.containsItem(Poo.NAME) == false) {
            this.addItem(new Poo());
        }
    }
}
