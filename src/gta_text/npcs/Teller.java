package gta_text.npcs;

import java.io.IOException;
import gta_text.Server;
import ssmith.lang.Functions;
import gta_text.items.CashCard;
import gta_text.items.Item;
import gta_text.locations.Location;

public class Teller extends CanBeRobbed {

    public static final String NAME = "Teller";
    private static final int BALANCE = 75;

    public Teller(Location loc) throws IOException {
        super(NonPlayerCharacter.TELLER, NAME, loc);
    }

  protected void performRandomAction() throws IOException {
      int x = Functions.rnd(1, 2);
      switch (x) {
      case 1:
          Server.game.informOthersOfMiscAction(this, null, "The teller types something into her computer.");
          break;
      case 2:
          this.say("Please ask about our comprehensive life insurance.");
          break;
      default:
          this.say("I need a better script.");
          break;
      }
  }

  public void given(GameCharacter char_from, Item item) throws IOException {
      if (item instanceof CashCard) {
          this.sayTo(char_from, "Thank you.  Your account has to be closed, but here is the remaining balance.");
          //this.cash += BALANCE; // So they've got enough to give away
          this.give("$"+BALANCE, char_from);
      } else {
          super.given(char_from, item);
      }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      this.sayTo(char_from, "Thank-you.");
      Server.game.informOthersOfMiscAction(this, null, "The " + name + " puts the money in her pocket.");
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
    this.say("That isn't in my job description!");
    return false;
  }

  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
      if (this.getCurrentLocation().robber == null) {
          this.say("Please don't fight in here.");
      }
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (msg.toUpperCase().indexOf("LOAN") >= 0) {
          this.sayTo(charac, "I'm sorry, you need property to secure against the loan.  Thankyou for your interest though.");
          return true;
      } else if (msg.toUpperCase().indexOf("INSURANCE") >= 0) {
          this.sayTo(charac, "I'm sorry, you look like too much of a risk.");
          return true;
      } else if (msg.toUpperCase().indexOf("WITHDRAW") >= 0) {
          this.sayTo(charac, "Do you have an account here, or a cashcard?");
          return true;
      } else if (msg.toUpperCase().indexOf("DEPOSIT") >= 0) {
          this.sayTo(charac, "Okay.  Can you give me the money you wish to deposit please.");
          return true;
      } else if (msg.toUpperCase().indexOf("JOB") >= 0) {
          this.sayTo(charac, "Sorry, I don't have the authority to give you a job.  You'd have to talk to the manager.  But he's not here at the moment.");
          return true;
      } else if (msg.toUpperCase().indexOf("OPEN") >= 0) {
          this.sayTo(charac, "Sorry, our computer is currently down, so I can't open you an account.");
          return true;
      } else if (msg.equalsIgnoreCase("NO")) {
          this.sayTo(charac, "Sorry, I can't help you.");
          return true;
      } else {
          return super.spokenTo(charac, msg, only_them);
      }
  }

}
