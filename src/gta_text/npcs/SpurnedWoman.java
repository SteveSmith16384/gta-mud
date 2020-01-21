package gta_text.npcs;

import java.io.IOException;
import ssmith.lang.Functions;
import java.io.FileNotFoundException;
import gta_text.locations.Location;

public class SpurnedWoman extends NonPlayerCharacter {

    public static final String NAME = "SpurnedWoman";

    private GameCharacter husband;

  public SpurnedWoman(Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.SPURNED_WOMAN, NAME, loc);

//      husband = Server.game.getRandomNPC(this);
  }

  public void process() throws Exception {
      super.process();

  }

  protected void performRandomAction() throws IOException {
      if (husband != null) {
          if (this.getCurrentLocation().containsCharacter(husband)) {
              this.sayTo(husband, "You still here?  The pizza it get cold!");
          } else {
              husband = null;
          }
      } else {
          int x = Functions.rnd(1, 4);
          switch(x) {
          case 1:
              this.say("What can I get you?");
              break;
          case 2:
              this.say("Okay, who's a next?");
              break;
          case 3:
              this.say("Mamma mia, it's burned!");
              break;
          case 4:
              this.say("I'm a looking for a pizza delivery boy.");
              break;
          default:
              this.say("I need a better script.");
              break;
          }
      }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      this.sayTo(char_from, "Grassias, but you need to tell me what you want first.");
  }

  protected String getRandomSayingWhenAttacked() {
    return "Mamma mia!";
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
    this.say("My mother warned me about people like you!");
    return false;
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
          if (msg.toUpperCase().indexOf("DELIVER") >= 0 || msg.toUpperCase().indexOf("JOB") >= 0 || msg.toUpperCase().indexOf("WORK") >= 0) {
              return true;
          } else if (this.attemptBuyFrom(charac, msg)) {
              return true;
          } else {
              if (only_them) {
                  if (msg.equalsIgnoreCase("ME")) {
                      this.sayTo(charac, "Okay, what can I get you?");
                  } else if (msg.equalsIgnoreCase("NO")) {
                      this.sayTo(charac, "Okay.  Could you move to the back please.  Who's next?");
                  } else {
                      this.sayTo(charac, "Pardon?");
                  }
                  return true;
              }
          }
      return super.spokenTo(charac, msg, only_them);
  }

/*  private void giveJob(GameCharacter charac) throws IOException {
      NonPlayerCharacter npc = Server.game.getRandomNPC(this);
      npc.wants_pizza = true;
      del_boy = charac;
      this.sayTo(charac, "Okay you want work?  Deliver this pizza to the " + npc.name + ".  They are at the " + npc.getCurrentLocation().getName() + ".  And make it snapeee!  You got " + MAX_DELIVERY_TIME/60/1000 + " minutes!");
      this.give(pizza, charac);
  }
*/

}
