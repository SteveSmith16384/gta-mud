package gta_text.npcs;

import gta_text.locations.Location;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Criminal extends NonPlayerCharacter {

  public Criminal(Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.CRIMINAL, "Criminal", loc);
  }

  protected void performRandomAction() throws IOException {
    this.say("I drive without car insurance!");
  }

  protected String getRandomSayingWhenAttacked() {
    return "Eh eh!  Calm down man!  Eh!";
  }

}
