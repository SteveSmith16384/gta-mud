package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.*;
import java.io.IOException;

import gta_text.items.Drink;
import gta_text.items.Food;
import gta_text.items.Item;
import ssmith.lang.Functions;
import gta_text.items.Corpse;

public class PawnBroker extends CanBeRobbed implements ISeller {

    public static final String NAME = "PawnBroker";

    public PawnBroker(Location loc) throws IOException {
        super(NonPlayerCharacter.PAWN_BROKER, NAME, loc);

    }

    protected String getRandomSayingWhenAttacked() {
        return "I've dealt with worse than you before!";
    }

    protected void performRandomAction() throws IOException {
        if (enemy == null) {
            int x = Functions.rnd(1, 3);
            switch (x) {
            case 1:
                Server.game.informOthersOfMiscAction(this, null,
                                                     "The " + NAME + " counts his money.");
                break;
            case 2:
                this.say("I'll give you cash for almost anything.");
                break;
            case 3:
                this.say("You should see the rubbish people try to sell me.");
                break;
            }
        }
    }

    public void given(GameCharacter char_from, Item item) throws IOException {
        Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " looks at the " + item.name + ".");
        if (item instanceof Corpse) {
            this.sayTo(char_from, "I'm not touching that!");
            this.give(item.name, char_from);
        } else if (item instanceof Drink) {
            this.sayTo(char_from, "Thanks, but I'm not thirsty.");
            this.give(item.name, char_from);
        } else if (item instanceof Food) {
            this.sayTo(char_from, "Thanks, but I'm not hungry.");
            this.give(item.name, char_from);
        } else if (item.cost <= 1) {
            this.sayTo(char_from, "Sorry mate, there's no demand for those.  I'll never shift 'em.");
            this.give(item.name, char_from);
        } else {
            if (Functions.rnd(1, 10) == 1) {
                this.sayTo(char_from,
                "Nah, not interested.");
                this.give(item.name, char_from);
            } else {
                int c = item.cost / 2;
                this.sayTo(char_from,
                        "Okay, i'll give you $" + c + " for it.");
                this.give("" + c, char_from);
                item.removeFromGame(false, true);
            }
        }
    }

    public String getSalesPitch() {
        return "I'm not selling anything, but I'll buy most stuff.  Just let me have a look at it first.";
    }

}
