package gta_text.items;

import java.io.IOException;
import gta_text.ServerConn;
import gta_text.npcs.GameCharacter;

public abstract class Food extends Item {

    public static final int HUNGER_QUENCH = 400;

    public Food(int no) throws IOException {
        super(no);
        
    }

    public boolean use(GameCharacter character) throws IOException {
       character.sendMsgToPlayer(ServerConn.ERROR_COL, "Try eating it.");
       return true;
   }

   public boolean eat(GameCharacter character, String style) throws IOException {
       character.reduceHunger(HUNGER_QUENCH);
       return true;

   }

}
