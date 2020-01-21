package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.Server;
import gta_text.ServerConn;

import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.Drink;
import gta_text.items.Alcohol;
import gta_text.items.Item;

public class DancingGirl extends NonPlayerCharacter {

    public static final String NAME = "DancingGirl";

    private GameCharacter friend;

    public DancingGirl(Location loc) throws IOException {
        super(NonPlayerCharacter.DANCING_GIRL, NAME, loc);
    }

    protected String getRandomSayingWhenAttacked() {
        return "Help!";
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 7);
        switch(x) {
        case 1:
            this.say("Can you dance?");
            break;
        case 2:
            Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " moves her body sexily around the dancefloor.");
            break;
        case 3:
            this.say("I just split up with my boyfriend.");
            break;
        case 4:
            Server.game.informOthersOfMiscAction(this, null, "The girl giggles.");
            break;
        case 5:
        case 6:
            if (this.current_item instanceof Drink) {
                this.eat(current_item.name, "drink");
            } else {
                this.say("Are you going to buy me a drink?");
            }
        case 7:
            this.say("Do you come here often?");
            break;
        }
    }

    public void given(GameCharacter char_from, Item item) throws IOException {
        if (item instanceof Alcohol) {
            this.sayTo(char_from, "Thank you!");
            if (Functions.rnd(1, 2) == 1) {
                this.kiss(char_from);
            } else {
                char_from.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + this.name + " winks at you.");
                Server.game.informOthersOfMiscAction(this, char_from, "The " + this.name + " winks at the " + char_from.name + ".");
            }
            this.friend = char_from;
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (msg.toUpperCase().indexOf("SUCK") >= 0 || msg.toUpperCase().indexOf("BLOW") >= 0 || msg.toUpperCase().indexOf("SWALLOW") >= 0) {
            this.sayTo(charac, "You're disgusting!");
            return true;
        } else if (msg.toUpperCase().indexOf("MMM") >= 0 || msg.toUpperCase().indexOf("BREASTS") >= 0 || msg.toUpperCase().indexOf("TIT") >= 0) {
            this.sayTo(charac, "Hey, I hardly know you!");
            return true;
        } else if (msg.toUpperCase().indexOf("LOVE") >= 0) {
            this.sayTo(charac, "It's a bit sudden.");
            return true;
        } else if (msg.toUpperCase().indexOf("SEXY") >= 0) {
            this.sayTo(charac, "You're not bad yourself.");
            return true;
        } else {
            if (only_them) {
                this.sayTo(charac, "I've had too much to drink.");
                return true;
            }
        }
        return super.spokenTo(charac, msg, only_them);
    }

    public boolean shagAttemptedBy(GameCharacter character) throws IOException {
        if (character == friend) {
            this.sayTo(character, "Hey, we should go somewhere private!");
            return true;
        } else {
            return super.shagAttemptedBy(character);
        }
    }

    public void kissedBy(GameCharacter character) throws IOException {
        if (character == friend) {
            character.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + this.name + " slides her tongue all the way down your throat.");
        } else {
            this.sayTo(character, "Hey, I hardly know you!");
        }
    }

    public void touched(GameCharacter character) throws IOException {
        if (character == friend) {
            this.sayTo(character, "Mmm.  Wanna take it further?");
        } else {
            this.sayTo(character, "You could at least by me a drink first!");
        }
    }
}
