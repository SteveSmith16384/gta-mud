package gta_text.locations;

import java.io.FileNotFoundException;
import java.io.IOException;
import gta_text.*;
import gta_text.npcs.GameCharacter;
import gta_text.npcs.MFCAssistant;
import gta_text.items.Newspaper;

public class MFChicken extends Location {

    public MFChicken(String no) throws FileNotFoundException, IOException {
        super(no);

        start_time = 8*60*60*1000;
        end_time = 23*60*60*1000;

        this.closed_reason = "Motherfucker Chicken is closed.  The sign on the door says it re-opens at " + GameTime.GetTimeAsString(start_time) + ".";
        new MFCAssistant(this);
        this.addItem(new Newspaper());
    }

    public void regenChars() throws IOException {
        // do nothing
    }

    public void process() throws IOException {
        super.process();

        // Are we open?
        if (this.is_open) {
            if (Server.game.game_time.GetMS() < start_time || Server.game.game_time.GetMS() > end_time) {
                this.is_open = false;
            }
        } else {
            if (Server.game.game_time.GetMS() > start_time && Server.game.game_time.GetMS() < end_time) {
                this.is_open = true;

                // Create the assistant
                if (this.containsCharacter("ASSISTANT") == false) {
                    new MFCAssistant(this);
                }
                if (this.containsItem("NEWSPAPER") == false) {
                    this.addItem(new Newspaper());
                }
            } else {
                if (this.containsPlayer()) {
                    GameCharacter charac = this.getCharacter("ASSISTANT");
                    if (charac != null) {
                        charac.say(
                                "Okay, time to close up shop now.  Could you all leave please.");
                    }
                }
            }
        }
    }

}
