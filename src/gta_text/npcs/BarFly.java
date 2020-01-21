package gta_text.npcs;

import gta_text.Server;
import gta_text.locations.Location;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.Beer;
import gta_text.items.Item;
import gta_text.items.Alcohol;
import gta_text.items.CashCard;

public class BarFly extends NonPlayerCharacter {

    private static final int REWARD=20;
    private static final String NAME = "Barfly";

    public BarFly(Location loc) throws IOException {
        super(NonPlayerCharacter.BARFLY, NAME, loc);
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 5);
        switch(x) {
        case 1:
            //if (this.getCurrentLocation().
            this.say("Get me another.");
            break;
        case 2:
            this.say("I'll have the usual please.");
            break;
        case 3:
            this.say("They say there's a brothel round here, but I can't find it...");
            break;
        case 4:
            this.say("Those addicts mugged me for my cashcard.");
            break;
        case 5:
            if (this.hasItem(Beer.NAME) == false) {
                Server.game.informOthersOfMiscAction(this, null,
                        "The " + NAME + " buys another beer.");
                this.addItem(new Beer());
            } else {
                this.eat(Beer.NAME, "drink");
            }
            break;
        default:
            this.say("I need a better script.");
            break;
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (msg.toUpperCase().indexOf("BROTHEL") >= 0) {
            this.sayTo(charac, "I dunno, no-one will tell me where it is.");
            return true;
        } else if (msg.toUpperCase().indexOf("ADDICTS") >= 0 || msg.toUpperCase().indexOf("MISSION") >= 0 || msg.toUpperCase().indexOf("HELP") >= 0 || msg.toUpperCase().indexOf("CASHCARD") >= 0) {
            this.sayTo(charac, "The addicts in the abandoned warehouse mugged me for my cashcard, no doubt to get drugs.  I'll give you a reward if you can get it back for me.");
            if (Server.game.get_cashcard_mission.completed) {
                Server.game.get_cashcard_mission.reset();
            }
            return true;
        } else {
            if (only_them) {
                this.sayTo(charac, "Is it your round?");
                return true;
            }
        }
        return super.spokenTo(charac, msg, only_them);
    }

    public void given(GameCharacter char_from, Item item) throws IOException {
        if (item instanceof Alcohol) {
            this.sayTo(char_from, "Cheers!");
            if (Server.game.get_cashcard_mission.completed == false) {
                this.sayTo(char_from, "You look like the sort of person who might be able to help me.");
            }
        } else if (item instanceof CashCard) {
            this.sayTo(char_from, "That's great!  How can I ever thank you!  Let me get you a drink!");
            //this.cash += REWARD; // So they've got enough to give away
            this.give(""+REWARD, char_from);
            Server.game.get_cashcard_mission.setCompleted();
        } else {
            super.given(char_from, item);
            //this.sayTo(char_from, "Thanks, but I'd prefer a drink!");
        }
    }

    protected String getRandomSayingWhenAttacked() {
      return "I only came here for a drink!";
    }

}
