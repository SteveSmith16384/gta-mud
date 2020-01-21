package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class Note extends Item {

    private String message;

    public Note(String msg) throws IOException {
        super(Item.NOTE);
        this.message = msg;
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.NORMAL_COL, getDesc());
        return true;
    }

    public String getDesc() {
        return "It's a piece of paper.  It says '" + this.message + "'.";
    }

}
