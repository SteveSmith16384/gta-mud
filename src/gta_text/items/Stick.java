package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;

public class Stick extends Item {

  public Stick() throws IOException {
    super(Item.STICK);
  }

  public boolean use(GameCharacter character) throws IOException {
    return false;
  }

}
