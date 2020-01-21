package gta_text.npcs;

import gta_text.locations.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import gta_text.items.MediKit;
import ssmith.lang.Functions;
import gta_text.items.Item;
import gta_text.items.Corpse;
import gta_text.*;

public class Doctor extends NonPlayerCharacter {

    public static final String NAME = "Doctor";

  public Doctor(Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.DOCTOR, NAME, loc);

      this.addItem(new MediKit());
  }

  public void process() throws Exception {
      super.process();

      if (Functions.rnd(1, 100) == 1) {
          if (this.hasItem(MediKit.NAME) == false) {
              this.addItem(new MediKit());
          }
      }
  }

  public void given(GameCharacter char_from, Item item) throws IOException {
      if (item instanceof Corpse) {
          this.sayTo(char_from, "Sorry, I'm afraid they are dead.  There's nothing I can do for them.");
          this.drop(item.name);
      } else {
          super.given(char_from, item);
      }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      this.sayTo(char_from, "Thanks.  This ain't the fricken NHS, y'know.");
  }

  protected void performRandomAction() throws IOException {
      int x = Functions.rnd(1, 4);
      switch (x) {
      case 1:
          this.say("I'm going on strike if I don't get a payrise.");
          break;
      case 2:
          this.say("You should have seen the body they brought in earlier.");
          break;
      case 3:
          this.say("Man, we're busy today.");
          break;
      case 4:
          Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " washes the blood off his hands.");
          break;
      default:
          this.say("Where's my script?");
          break;
      }
  }

  protected String getRandomSayingWhenAttacked() {
    return "Hey, I'm only tryin' to help, man!";
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (msg.toUpperCase().indexOf("HEAL") >= 0 || msg.toUpperCase().indexOf("HELP") >= 0 || msg.toUpperCase().indexOf("INJURED") >= 0) {
          if (this.hasItem(MediKit.NAME)) {
              //this.addItem(new MediKit());
              this.sayTo(charac, "See if this helps.  It's my last one.");
              this.give(MediKit.NAME, charac);
          } else {
              this.sayTo(charac, "Sorry, I'm out of bandages.");
          }
          return true;
      }
      return super.spokenTo(charac, msg, only_them);
  }

}
