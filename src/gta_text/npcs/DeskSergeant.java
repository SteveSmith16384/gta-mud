/*
 * Created on 15-May-2006
 *
 */
package gta_text.npcs;

import gta_text.items.Pistol;
import gta_text.locations.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.Server;
import gta_text.items.Item;
import gta_text.items.Corpse;

public class DeskSergeant extends Law {

    private static final int MAFIA_REWARD = 100;

    public DeskSergeant(Location loc) throws FileNotFoundException, IOException {
        super(NonPlayerCharacter.DESK_SEARGENT, "DeskSergeant", loc);
        this.addItem(new Pistol());
    }

    public void given(GameCharacter char_from, Item item) throws IOException {
        if (item instanceof Corpse) {
            Corpse corpse = (Corpse) item;
            if (corpse.orig_char instanceof MafiaDon) {
                this.sayTo(char_from, "I suppose you want the reward?");
                this.cash += MAFIA_REWARD; // So they've got enough to give away
                this.give("$"+MAFIA_REWARD, char_from);
                Server.game.kill_mafia_mission.setCompleted();
                return;
            }
        }
        this.sayTo(char_from, "What do I want with that?  That's not who we're looking for.");
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        if (character != this.enemy) {
            this.say("Can I help you?");
        }
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        //super.givenCash(char_from, amt);
        this.sayTo(char_from, "Are you trying to bribe me?");
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (charac instanceof PlayersCharacter) {
            if (msg.toUpperCase().indexOf("MAFIA") >=0) {
                if (Server.game.kill_mafia_mission.completed) {
                    this.sayTo(charac,
                            "Sorry, someone already got the Mafia Don.");
                } else {
                    this.sayTo(charac,
                            "Read the poster.  It explains everything.");
                }
            } else if (msg.toUpperCase().indexOf("JOIN") >=0 || msg.toUpperCase().indexOf("WORK") >=0) {
                this.sayTo(charac, "Sorry, I don't think you're what we're looking for.  But I'll tell you what - we'll give you a bounty for every wanted criminal you kill.  Just don't kill any innocent people, or you'll be hunted yourself.  We'll communicate with you via the radio.");
                charac.bounty_hunter = true;
            } else if (msg.toUpperCase().indexOf("BOUNTY") >=0 || msg.toUpperCase().indexOf("HUNTER") >=0 || msg.toUpperCase().indexOf("DETAILS") >=0) {
                this.sayTo(charac, "Okay, we'll give you a bounty for every wanted criminal you kill.  Just don't kill any innocent people, or you'll be hunted yourself.  We'll communicate with you via the radio.");
                charac.bounty_hunter = true;
            } else {
                this.sayTo(charac, "Sorry, I'm to busy.  This paperwork won't do itself.");
            }
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }

    protected void performRandomAction() throws IOException {
        if (this.enemy == null) {
            int x = Functions.rnd(1, 2);
            switch (x) {
            case 1:
                this.say("Can I help you sonny?");
                break;
            case 2:
                this.say("Please join the queue.");
                break;
            default:
                this.say("What are my lines?");
                break;
            }
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "If you want trouble you've come to the right place!";
      }

}

