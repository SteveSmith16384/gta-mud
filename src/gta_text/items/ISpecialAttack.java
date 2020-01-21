package gta_text.items;

import gta_text.npcs.GameCharacter;
import java.io.IOException;

public interface ISpecialAttack {

    public void attack(GameCharacter user, GameCharacter target) throws IOException;

}
