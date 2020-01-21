/*
 * Created on 15-May-2006
 *
 */
package gta_text.locations;

import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.Server;
import gta_text.npcs.Hooker;

public class RedlightDistrict extends Location {

    public RedlightDistrict(String no) throws FileNotFoundException, IOException {
        super(no);

        this.start_time = ((19*60) + Functions.rnd(0, 120))*60*1000;
        this.end_time = ((5*60) + Functions.rnd(0, 120))*60*1000;
    }

    public void regenChars() throws IOException {
        // do nothing
    }

    public void process() throws IOException {
        super.process();

        // Time to arrive/go?
        // Have we disappeared.
        if (this.containsCharacter("HOOKER") == false) {
            long ms = Server.game.game_time.GetMS();
            if (ms >= this.start_time && ms <= this.start_time+(1000*60*2)) {
                new Hooker(this);
            }
        }
    }

}
