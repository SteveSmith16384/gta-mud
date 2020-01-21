/*
 * Created on 03-Jul-2006
 *
 */
package gta_text.npcs;

import gta_text.locations.Location;

import java.io.IOException;
import java.util.Enumeration;

public abstract class MemberOfGang extends NonPlayerCharacter {

    public MemberOfGang(int type, String name, Location loc) throws IOException {
        super(type, name, loc);
    }

    public void process() throws Exception {
        super.process();

        if (this.enemy == null) {
            Enumeration enumr = this.getCurrentLocation().getCharacterEnum();
            while (enumr.hasMoreElements()) {
                GameCharacter charac = (GameCharacter)enumr.nextElement();
                if (charac.gang != 0) {
                    if (charac.gang != this.gang) {
                        this.say("I spy a " + charac.name + "!");
                        this.enemy = charac;
                        break;
                    }
                }
            }
        }

    }

}
