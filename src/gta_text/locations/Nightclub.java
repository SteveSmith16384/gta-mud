package gta_text.locations;

import gta_text.GameTime;
import gta_text.Server;
import java.io.FileNotFoundException;
import java.io.IOException;
import gta_text.npcs.Barmaid;
import gta_text.npcs.Bouncer;
import gta_text.npcs.Geezer;
import gta_text.npcs.GameCharacter;
import gta_text.items.Cigs;

public class Nightclub extends Location {

    public Nightclub(String no) throws FileNotFoundException, IOException {
        super(no);

        start_time = 21*60*60*1000;
        end_time = 5*60*60*1000;

        this.closed_reason = "Shitzy's has closed to clean up the blood and vomit.  It re-opens at " + GameTime.GetTimeAsString(start_time) + ".";

        new Geezer(this);
        new Barmaid("Barmaid", this);
        new Bouncer(this);
}

    public void regenChars() throws IOException {
        // do nothing
    }

    public void process() throws IOException {
        super.process();

        // Are we open?
        if (this.is_open) {
            if (Server.game.game_time.GetMS() < start_time && Server.game.game_time.GetMS() > end_time) {
                this.is_open = false;
            }
        } else {
            if (Server.game.game_time.GetMS() < end_time || Server.game.game_time.GetMS() > start_time) {
                this.is_open = true;
                // Create the characters
                if (this.containsCharacter("BARMAID") == false) {
                    new Barmaid("Barmaid", this);
                }
                if (this.containsCharacter("BOUNCER") == false) {
                    new Bouncer(this);
                }
                if (this.containsCharacter("GEEZER") == false) {
                    new Geezer(this);
                }
                if (this.containsItem(Cigs.NAME) == false) {
                    this.addItem(new Cigs());
                }
            } else {
                if (this.containsPlayer()) {
                    GameCharacter charac = this.getCharacter("BARMAID");
                    if (charac != null) {
                        charac.say(
                                "Okay, get your asses out of here, we're closing.");
                    }
                }
            }
        }
    }

}
