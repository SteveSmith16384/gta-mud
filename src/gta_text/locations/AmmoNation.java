package gta_text.locations;

import java.io.FileNotFoundException;
import java.io.IOException;
import gta_text.npcs.AmmoNationShopKeeper;
import gta_text.npcs.Dog;
import gta_text.npcs.GameCharacter;

public class AmmoNation extends Location {

    private AmmoNationShopKeeper shopkeeper;

    public AmmoNation(String no) throws FileNotFoundException, IOException {
        super(no);

        this.closed_reason = "It's closed for the day.";

        shopkeeper = new AmmoNationShopKeeper(this);
        new Dog(this, shopkeeper);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter("SHOPKEEPER") == false) {
            shopkeeper = new AmmoNationShopKeeper(this);

        }
        if (this.containsCharacter("DOG") == false) {
            new Dog(this, shopkeeper);
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws
            IOException {
        if (cmd.toUpperCase().startsWith("SELL ") ||
            cmd.equalsIgnoreCase("SELL")) {
            if (shopkeeper != null) {
                if (shopkeeper.getHealth() > 0) {
                    shopkeeper.sayTo(character,
                                     "Sorry, I don't buy stuff.  You could try the Pawn shop though.");
                    //                    this.sendOutput("If you wish to sell something, you can should find another player and make them an offer.");
                }
            } else {
                character.sendMsgToPlayer("", "There is no-one buying stuff here.");
            }
            return true;
        }
        return false;
    }
}



