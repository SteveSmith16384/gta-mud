package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;
import gta_text.Game;
import gta_text.Server;
import ssmith.io.TextFile;
import gta_text.admin.SavedGames;

public class Noticeboard extends Item {

    public static final String NAME = "Noticeboard";

    public Noticeboard() throws IOException {
        super(Item.NOTICEBOARD);
    }

    public boolean use(GameCharacter character) throws IOException {
        return false;
    }

    public String read(GameCharacter character) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("The noticeboard says:-");
        String str = TextFile.ReadAll(SavedGames.SAVED_GAMES_DIR + Game.NOTICEBOARD, Server.CR);
        sb.append(str);
        sb.append("-- End of notices --");

        return sb.toString();

    }

}
