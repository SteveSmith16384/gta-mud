package gta_text.npcs;

import gta_text.items.Burger;
import gta_text.items.Item;
import gta_text.locations.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.*;
import gta_text.ServerConn;

public class MFCAssistant extends CanBeRobbed implements ISeller {

  public MFCAssistant(Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.MFC_ASSISTANT, "Assistant", loc);
  }

  protected void performRandomAction() throws IOException {
          int x = Functions.rnd(1, 2);
          switch(x) {
          case 1:
              this.say("What can I get you?");
              break;
          case 2:
              this.say("Who's next?");
              break;
          default:
              this.say("I need a better script.");
              break;
          }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      //super.givenCash(char_from, amt);
      this.sayTo(char_from, "You need to tell me what you want first.");
  }

  /*protected String getRandomSayingWhenAttacked() {
    return "Get out of here!";
  }*/

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
    this.say("That isn't in my job description!");
    return false;
  }

  /*public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
      if (character != this.enemy) {
          this.say("Welcome to MFC.");
      } else {
          this.say("Not you again!");
      }
  }
*/
/*  public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
    this.say("Have a nice day.");
  }
*/
  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
    this.say("Please don't fight in here.");
  }

  public String getSalesPitch() {
      return "We've got MFC Burgers, fries, and coke, and that's it.";
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
          if (this.attemptBuyFrom(charac, msg)) {
              return true;
          } else if (super.containBuyKeyword(msg) ||
                     msg.toUpperCase().indexOf("EAT") >= 0 ||
                     msg.toUpperCase().indexOf("CHICKEN") >= 0) {
              this.sayTo(charac, this.getSalesPitch());
              return true;
          } else if (msg.toUpperCase().indexOf("MUCH") >= 0) {
              this.sayTo(charac,
                      "Try buying something and I'll tell you if you've got enough.");
              return true;
          } else {
              if (only_them) {
                  if (msg.equalsIgnoreCase("ME")) {
                      this.sayTo(charac, "Okay, what can I get you?");
                  } else if (msg.equalsIgnoreCase("NO")) {
                      this.sayTo(charac, "Okay.  Could you move to the back please.  Who's next?");
                  } else if (msg.toUpperCase().indexOf("NO ") >= 0) {
                      this.sayTo(charac, "Okay.  Could you move to the back please.  Who's next?");
                  } else {
                      this.sayTo(charac, "Pardon?");
                  }
                  return true;
              }
          }
      return super.spokenTo(charac, msg, only_them);
  }

  public boolean attemptBuyFrom(GameCharacter charac, String item_name) throws IOException {
      Item item = null;
      if (item_name.toUpperCase().indexOf("BURGER") >= 0 || item_name.toUpperCase().indexOf("HAMBURGER") >= 0 || item_name.toUpperCase().indexOf("FOOD") >= 0 || item_name.toUpperCase().indexOf("CHICKEN") >= 0) {
          item = new Burger();
      } else if (item_name.toUpperCase().indexOf("FRIES") >= 0 || item_name.toUpperCase().indexOf("CHIPS") >= 0) {
          item = new Fries();
      } else if (item_name.toUpperCase().indexOf("COKE") >= 0 || item_name.toUpperCase().indexOf("DRINK") >= 0 || item_name.toUpperCase().indexOf("SODA") >= 0) {
          item = new Coke();
      }
      if (item != null) {
          if (charac.cash >= item.cost) {
              charac.sendMsgToPlayer(ServerConn.ACTION_COL, "You buy a " + item.name + " for $" + item.cost + ".");
              charac.addItem(item);
              charac.cash -= item.cost;
              this.sayTo(charac, "Do you want anything with that?");
          } else {
              item.removeFromGame(false, false);
              charac.sendMsgToPlayer(ServerConn.ERROR_COL, "You don't have enough money.  They cost $" + item.cost + ".");
          }
          return true;
      }
      return false;

  }
}
