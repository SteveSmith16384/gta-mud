/*
 * Created on 03-Jul-2006
 *
 */
package gta_text.items;

import gta_text.ServerConn;
import gta_text.npcs.GameCharacter;

import java.io.IOException;

import ssmith.lang.Functions;

public class Pizza extends Food {
    
    public Pizza() throws IOException {
        super(Item.PIZZA);
        
        this.desc = getFlavour();
    }

    private String[] topping1 = {"anchovy", "carrot", "banana", "mince"};
    private String[] topping2 = {"rhubarb", "sprout", "gravy", "chewing gum"};
 
    public String getFlavour() {
        int a = Functions.rnd(0, topping1.length-1);
        int b = Functions.rnd(0, topping2.length-1);
 
        return "It's a deep pan with " + topping1[a] + " and " + topping2[b] + " topping.";
    }

    public boolean eat(GameCharacter character, String style) throws IOException {
        super.eat(character, style);
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "You eat the pizza.  It's delish!");
        //character.removeItem(this);
        return true;
    }

}
