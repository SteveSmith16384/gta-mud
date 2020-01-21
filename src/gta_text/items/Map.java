package gta_text.items;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.Game;
import gta_text.Server;
import ssmith.io.TextFile;
import gta_text.ServerConn;

public class Map extends Item {

    public Map() throws IOException {
        super(Item.MAP);
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.NORMAL_COL, getDesc());
        return true;
    }

    public String getDesc() throws IOException {
        String str = TextFile.ReadAll(Game.DATA_DIR + "map.txt", Server.CR);
        return str;
    }

}
