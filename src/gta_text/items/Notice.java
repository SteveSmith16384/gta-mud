package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;
import gta_text.ServerConn;

public class Notice extends Item {

    private String desc;

    public Notice(String descr) throws IOException {
        super(Item.NOTICE);
        this.desc = descr;
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.NORMAL_COL, getDesc());
        return true;
    }

    public String getDesc() {
        return desc;
    }

}
