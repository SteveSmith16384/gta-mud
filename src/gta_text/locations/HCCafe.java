/*
 * Created on 19-May-2006
 *
 */
package gta_text.locations;

import gta_text.GameTime;
import gta_text.Server;
import gta_text.npcs.Barmaid;
import java.io.IOException;
import gta_text.npcs.BarFly;
import gta_text.npcs.Footballer;
import gta_text.npcs.GameCharacter;
import gta_text.items.Cigs;

public class HCCafe extends Location {

    public HCCafe(String no) throws IOException {
        super(no);

        start_time = 12*60*60*1000;
        end_time = 1*60*60*1000;

        this.closed_reason = "The Hard-Cock Cafe is closed at the moment.  It re-opens at " + GameTime.GetTimeAsString(start_time) + ".";

        new Barmaid("Barmaid", this);
        new BarFly(this);

        new Footballer(this); //todo - regen etc...
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
            if (Server.game.game_time.GetMS() > start_time || Server.game.game_time.GetMS() < end_time) {
                this.is_open = true;
                // Create the characters
                if (this.containsCharacter("BARMAID") == false) {
                    new Barmaid("Barmaid", this);
                }
                if (this.containsCharacter("BARFLY") == false) {
                    new BarFly(this);
                }
                if (this.containsItem(Cigs.NAME) == false) {
                    this.addItem(new Cigs());
                }
            } else {
                if (this.containsPlayer()) {
                    GameCharacter charac = this.getCharacter("BARMAID");
                    if (charac != null) {
                        charac.say(
                                "Time gentlemen please.  Haven't you got homes to go to?");
                    }
                }
            }
        }
    }

}
