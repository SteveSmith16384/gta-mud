package gta_text.npcs;

import java.io.IOException;
import gta_text.locations.Location;
import gta_text.Server;
import gta_text.items.Item;
import gta_text.items.Noodles;
import gta_text.ServerConn;
import gta_text.items.Coke;
import gta_text.items.Water;

public class TakeawayAssistant extends CanBeRobbed implements ISeller {

    public static final String NAME = "Assistant";

    public TakeawayAssistant(Location loc) throws IOException {
        super(NonPlayerCharacter.TAKEAWAY_ASSISTANT, NAME, loc);
    }

    protected String getRandomSayingWhenAttacked() {
        return "Much good are you at fighting!";
    }

    protected void performRandomAction() throws IOException {
        Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " says something in chinese.");
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        //super.givenCash(char_from, amt);
        this.sayTo(char_from, "Just buy something.");
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        if (character != this.enemy) {
            this.say("What you wanna order?");
        } else {
            this.say("You barred from here!");
        }
    }

    public String getSalesPitch() {
        return "We have noodles and that's it to eat.  Also we have coke and water.";
    }

    public boolean attemptBuyFrom(GameCharacter charac, String item_name) throws IOException {
        Item item = null;
        if (item_name.toUpperCase().indexOf("NOODLES") >= 0) {
            item = new Noodles();
        } else if (item_name.toUpperCase().indexOf("COKE") >= 0 || item_name.toUpperCase().indexOf("SODA") >= 0) {
            item = new Coke();
        } else if (item_name.toUpperCase().indexOf("WATER") >= 0) {
            item = new Water();
        }
        if (item != null) {
            if (charac.cash >= item.cost) {
                charac.sendMsgToPlayer(ServerConn.ACTION_COL, "You buy a " + item.name + " for $" + item.cost + ".");
                charac.addItem(item);
                charac.cash -= item.cost;
                this.say("They our best seller!");
            } else {
                item.removeFromGame(false, false);
                charac.sendMsgToPlayer(ServerConn.ERROR_COL, "You don't have enough money.  They cost $" + item.cost + ".");
            }
            return true;
        }
        return false;
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (this.attemptBuyFrom(charac, msg)) {
            return true;
        } else if (containsRobbery(msg)) {
            this.sayTo(charac, "Sorry, my english not very good.  Maybe you try bank down road?");
        } else if (only_them) {
            this.sayTo(charac, "Sorry, my english no good.");
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }

}
