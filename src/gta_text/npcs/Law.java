package gta_text.npcs;

import java.io.IOException;
import gta_text.locations.Location;
import gta_text.Server;
import java.util.Enumeration;
import ssmith.lang.Functions;

public abstract class Law extends Security {

    public static final int BRIBE_AMT = 20;

    public int tot_bribe = 0;

    public Law(int type, String name, Location loc) throws IOException {
        super(type, name, loc);
    }

    public void process() throws Exception {
        try {
            if (this.enemy == null) {
                if (this.getCurrentLocation() != null) {
                    Enumeration enumr = this.getCurrentLocation().
                                        getCharacterEnum();
                    while (enumr.hasMoreElements()) {
                        GameCharacter charac = (GameCharacter) enumr.
                                               nextElement();
                        if (charac.getWantedLevel() > 0) {
                            if (charac instanceof PlayersCharacter) {
                                this.enemy = charac;
                                int x = Functions.rnd(1, 3);
                                switch(x) {
                                case 1:
                                    this.sayTo(charac, "You're wanted by the law!");
                                    break;
                                case 2:
                                    this.sayTo(charac, "You're under arrest!");
                                    break;
                                case 3:
                                    this.sayTo(charac, "You're a wanted fugitive!");
                                    break;
                                default:
                                    this.say("Script!");
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    System.err.println(this.name + " has null location!");
                }
            }
        } catch (Exception ex) {
            Server.HandleError(ex);
        }

        super.process(); // Must be after we check for wanted people, otherwise we might leave the loc.
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        if (char_from.getWantedLevel() > 0) {
            this.tot_bribe += amt;
            if (this.tot_bribe < BRIBE_AMT) {
                this.sayTo(char_from,
                        "Are you trying to bribe me?  If so, it's not enough.");
            } else {
                char_from.incWantedLevel(-1);
                this.sayTo(char_from,
                        "Thanks.  Now get outta here.");
             this.tot_bribe -= BRIBE_AMT;
            }
        } else {
            this.sayTo(char_from,
                    "What have you given me that for?");
        }
    }

    public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
        if (!disappeared) {
            if (character == enemy) {
                if (Functions.rnd(1, 3) > 1) {
                    // Follow them!
                    this.goto_location = new_loc;
                /*} else {
                    enemy = null;*/
                }
            }
        }
    }


}
