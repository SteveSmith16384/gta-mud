package gta_text.items;

import gta_text.npcs.GameCharacter;
import gta_text.*;
import java.io.IOException;

public class MediKit extends Item {

    public static final String NAME = "MediKit";

  public MediKit() throws IOException {
    super(Item.FIRST_AID_KIT);

    this.other_names.add("Kit");
    this.other_names.add("MedKit");
  }

  public boolean use(GameCharacter character) throws IOException {
      if (character.getHealth() < character.getMaxHealth()) {
          character.increaseHealth(50, null);
          character.sendMsgToPlayer(ServerConn.ACTION_COL, "You use the " + this.name +
                                    ".  You now have " + character.getHealth() + " health.");
          Server.game.informOthersOfMiscAction(character, null, character.name + " uses the " + this.name + ".");
          character.removeItem(this);
      } else {
          character.sendMsgToPlayer(ServerConn.ERROR_COL, "You don't need to use it.  You are full of health.");
      }
      return true;
  }
}
