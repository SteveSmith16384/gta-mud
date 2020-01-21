package gta_text.items;

import gta_text.ServerConn;
import gta_text.npcs.GameCharacter;
import java.io.IOException;

import ssmith.lang.Functions;

public class DeckOfCards extends Item {

    public DeckOfCards() throws IOException {
        super(Item.CARDS);
    }

    public boolean use(GameCharacter character) throws IOException {
        StringBuffer str = new StringBuffer();
        str.append("You deal the ");
        
        int no = Functions.rnd(1, 13);
        switch (no) {
        case 1:
            str.append("Ace");
            break;
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
            str.append(""+no);
            break;
        case 11:
            str.append("Jack");
            break;
        case 12:
            str.append("Queen");
            break;
        case 13:
            str.append("King");
            break;
        }
        
        str.append(" of ");
        
        int suit = Functions.rnd(1, 4);
        switch (suit) {
        case 1:
            str.append("Spades");
            break;
        case 2:
            str.append("Hearts");
            break;
        case 3:
            str.append("Clubs");
            break;
        case 4:
            str.append("Diamonds");
            break;
        }
        str.append(".");
        
        character.sendMsgToPlayer(ServerConn.ACTION_COL, str.toString());
        
        return true;
    }

}
