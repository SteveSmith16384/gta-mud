package gta_text.locations;

import gta_text.npcs.Bouncer;
import java.io.IOException;
import gta_text.npcs.Barmaid;
import java.io.FileNotFoundException;
import gta_text.Server;
import gta_text.npcs.GameCharacter;

public class Casino extends Location {

    public Casino(String no) throws FileNotFoundException, IOException {
        super(no);

        start_time = 0;
        end_time = 8*60*60*1000;

        this.closed_reason = "The door seems to be firmly locked.";

        new Barmaid("Bartender", this);
        new Bouncer(this);
    }

    public void regenChars() throws IOException {
        // do nothing
    }

    public void process() throws IOException {
        super.process();

        // Are we open?
        if (this.is_open) {
            if (Server.game.game_time.GetMS() > end_time) {
                this.is_open = false;
            }
        } else {
            if (Server.game.game_time.GetMS() < end_time) {
                this.is_open = true;
                // Create the characters
                if (this.containsCharacter("BARTENDER") == false) {
                    new Barmaid("Bartender", this);
                }
                if (this.containsCharacter("BOUNCER") == false) {
                    new Bouncer(this);
                }
            } else {
                if (this.containsPlayer()) {
                    GameCharacter charac = this.getCharacter("BARTENDER");
                    if (charac != null) {
                        charac.say("Okay, time to close up shop now.  Could you all leave please.");
                    }
                }
            }
        }

        if (Server.DEBUG) {
            this.is_open = true;
        }
    }

}
