/*
 * Created on 10-Aug-2006
 *
 */
package gta_text.npcs;

import gta_text.Server;
import gta_text.items.Knife;
import gta_text.locations.Location;
import java.io.IOException;
import java.util.Enumeration;
import ssmith.lang.Functions;

public class Stalker extends NonPlayerCharacter {

    public static int total_stalkers=0;

    private GameCharacter target;
    //private int stage = 0;
    //private Interval stage_interval = new Interval(1000*10);
    private int tmp_wander; // For storing wander when we're on a job.

    public Stalker(Location loc) throws IOException {
        super(NonPlayerCharacter.STALKER, "Stalker", loc);
        this.addItem(new Knife());
        total_stalkers++;
    }

    public void removeFromGame() throws IOException {
        super.removeFromGame();
        total_stalkers--;
    }

    public void process() throws Exception {
        super.process();

        this.ensureWantedLevel(1);

        if (this.target == null) {
            //if (this.getCurrentLocation().getNoOfCharacters() == 2) { // Just us
                Enumeration enumr = this.getCurrentLocation().getCharacterEnum();

                boolean found = false;
                GameCharacter charac = null;
                while (enumr.hasMoreElements()) {
                    charac = (GameCharacter) enumr.nextElement();
                    if (charac instanceof PlayersCharacter) {
                        found = true;
                        break;
                    }
                }
                if (found) {
                    this.target = charac;
                    this.tmp_wander = this.wander_chance;
                    this.wander_chance = 1;
                }
            //}
        } else if (this.target.getCurrentLocation() == this.getCurrentLocation()) {
            // todo
        } else {
            target = null; // They've left!
            this.wander_chance = this.tmp_wander;
        }
    }

    public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
        if (character == target && disappeared == false) {
            this.goto_location = new_loc; // Follow them
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "Don't hurt me!";
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 2);
        switch(x) {
        case 1:
            Server.game.informOthersOfMiscAction(this, null, "The " + name + " looks around dreamily.");
            break;
        case 2:
            Server.game.informOthersOfMiscAction(this, null, "The " + name + " shuffles his feet shiftily.");
            break;
        default:
            this.say("Script?");
            break;
        }
        
    }

}
