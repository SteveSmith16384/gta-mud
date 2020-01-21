package gta_text.npcs;

import gta_text.items.Item;
import gta_text.locations.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.*;
import gta_text.*;

public class PizzaChef extends CanBeRobbed implements ISeller {

    public static final String NAME = "PizzaChef";
    public static final long MAX_DELIVERY_TIME = 1000*60*2;

    private GameCharacter del_boy;

  public PizzaChef(Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.PIZZA_CHEF, NAME, loc);
  }

  public void process() throws Exception {
      super.process();

      if (this.getCurrentLocation().containsCharacter(del_boy) == false) {
          del_boy = null;
      }
  }

  protected void performRandomAction() throws IOException {
      if (del_boy != null) {
          if (this.getCurrentLocation().containsCharacter(del_boy)) {
              this.sayTo(del_boy, "You still here?  The pizza it get cold!");
          } else {
              del_boy = null;
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

  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
    this.say("Please do not fight in here.");
  }

  public String getSalesPitch() {
      return "We got pizza.  And a nice coke to go with it if you want.";
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
          if (msg.toUpperCase().indexOf("DELIVER") >= 0 || msg.toUpperCase().indexOf("JOB") >= 0 || msg.toUpperCase().indexOf("WORK") >= 0) {
              giveJob(charac);
              return true;
          } else if (this.attemptBuyFrom(charac, msg)) {
              return true;
          } else if (super.containBuyKeyword(msg) ||
                     msg.toUpperCase().indexOf("EAT") >= 0 ||
                     msg.toUpperCase().indexOf("PIZZA") >= 0) {
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
      if (item_name.toUpperCase().indexOf("PIZZA") >= 0 || item_name.toUpperCase().indexOf("FOOD") >= 0 || item_name.toUpperCase().indexOf("CHICKEN") >= 0) {
          item = new Pizza();
      } else if (item_name.toUpperCase().indexOf("COKE") >= 0 || item_name.toUpperCase().indexOf("SODA") >= 0) {
          item = new Coke();
      } else if (item_name.toUpperCase().indexOf("WATER") >= 0) {
          item = new Water();
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

  private void giveJob(GameCharacter charac) throws IOException {
      NonPlayerCharacter npc = Server.game.getRandomNPC(this);
      npc.wants_pizza = true;
      npc.pizza_time_up = System.currentTimeMillis() + MAX_DELIVERY_TIME;
      Pizza pizza = new Pizza();
      this.addItem(pizza);
      del_boy = charac;
      this.sayTo(charac, "Okay you want work?  Deliver this pizza to the " + npc.name + ".  They are at the " + npc.getCurrentLocation().getName() + ".  And make it snapeee!  You got " + MAX_DELIVERY_TIME/60/1000 + " minutes!");
      this.give(pizza, charac);
  }

}
