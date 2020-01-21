package gta_text.npcs;

import gta_text.items.Beer;
import gta_text.locations.Location;
import gta_text.*;
import gta_text.npcs.NonPlayerCharacter;
import ssmith.lang.Functions;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Geezer extends NonPlayerCharacter {

    public static final String NAME = "Geezer";

    public Geezer(Location loc) throws FileNotFoundException, IOException {
        super(NonPlayerCharacter.GEEZER, NAME, loc);
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 4);
        switch(x) {
        case 1:
            this.say("Awwoight'.");
            break;
        case 2:
            this.say("You're 'aving a larf, 'intcha?");
            break;
        case 3:
            this.say("Get us another, will ya darlin'?");
            break;
        case 4:
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
        if (only_them) {
            if (charac.male) {
                this.sayTo(charac, "Awwoight mate");
            } else {
                this.sayTo(charac, "Awwoight darlin'");
            }
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }

    protected String getRandomSayingWhenAttacked() {
      return "You facking cant!";
    }


}
