package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.Server;
import gta_text.ServerConn;
import gta_text.items.Item;
import gta_text.items.RangedWeapon;
import java.io.IOException;
import ssmith.lang.Functions;
import ssmith.util.Interval;

public class PlayersCharacter extends GameCharacter {

    private static final int START_CASH = 35;

  public ServerConn conn;
  public String players_name, pwd;
  private Interval sober_interval = new Interval(1000*60); // 1 min
  private Interval heal_interval = new Interval(1000*60*2); // 2 mins

  public PlayersCharacter(ServerConn c, String playername, String n, boolean is_male, boolean show_loc_desc) throws IOException {
    super(n, null, 100, START_CASH, is_male, show_loc_desc);
    conn = c;
    players_name = playername;

    this.setCurrentLocation(Server.game.start_loc, "", true, show_loc_desc); // Need to set this here cos if we use the constructor, conn hasn't been set yet.  Also they won't get the description of the first location.
    //AbstractServer.write("Character " + name + " created.");
  }

  public void process() throws Exception  {
      super.process();

    String i = this.conn.next_input;
    this.conn.next_input = "";
      if (i.length() > 0) {
          this.conn.parseInput(i);
      }

      if (last_day_period < Server.game.game_time.getDayPeriodNo()) {
          this.sendMsgToPlayer(ServerConn.NORMAL_COL, "It is " + Server.game.game_time.getDayPeriodName() + ".");
          last_day_period = Server.game.game_time.getDayPeriodNo();
      }

      if (this.last_weather_type < Server.game.game_weather.current_weather) {
          if (this.getCurrentLocation() != null) {
              if (this.getCurrentLocation().internal == false) { // Only tell them if they are outside.
                  this.sendMsgToPlayer(ServerConn.NORMAL_COL, Server.game.game_weather.getDesc());
                  this.last_weather_type = Server.game.game_weather.
                                           current_weather;
              }
          }
      }

      // See if we stagger
      if (sober_interval.hitInterval()) {
          if (Functions.rnd(1, 50) < this.drunkness) {
              if (Functions.rnd(1, 2) == 1) {
                  this.sendMsgToPlayer(ServerConn.ACTION_COL, "You stagger a bit.");
                  Server.game.informOthersOfMiscAction(this, null,
                          "The " + this.name + " staggers a bit.");
              } else {
                  this.sendMsgToPlayer(ServerConn.ACTION_COL, "The room spins.");
                  Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null,
                          "The " + this.name + " looks a bit dizzy.");
              }
          }
          this.drunkness--;
      }

      if (heal_interval.hitInterval()) {
          this.increaseHealth(1, null);
      }

  }

  public void removeFromGame() throws IOException {
      super.removeFromGame();
      Server.write("Character " + name + " removed.");
      this.conn.quitting = true;
  }

  public void given(GameCharacter char_from, Item item) throws IOException {
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + char_from.name + " gives you a " + item.name + ".");
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + char_from.name + " gives you $" + amt + " hard cash.");
  }

  public void sendMsgToPlayer(String col, String msg) throws IOException {
      if (conn != null) {
          this.conn.sendOutput(col, msg);
      } else {
          System.err.println("COULD NOT SEND: " + msg);
      }
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
    this.conn.sendOutput(ServerConn.ACTION_COL, "The " + character.name + " tries to shag you.");
    return false;
  }

  public void attackedBy(GameCharacter character, String method) throws IOException {
  }

  public void shotAtBy(GameCharacter character, RangedWeapon item) throws IOException {
    //this.conn.sendOutput("The " + character.name + " shoots at you with a "+item.name+".");
  }

  public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
    if (old_loc != null) {
      this.conn.sendOutput(ServerConn.ACTION_COL, "A " + character.name + " has entered from the " +
			   old_loc.getName() + ".");
    } else {
      this.conn.sendOutput(ServerConn.ACTION_COL, "A " + character.name + " has entered.");
    }
  }

  public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
	  if (new_loc != null) {
              if (to_dir.length() > 0) {
                  this.conn.sendOutput(ServerConn.ACTION_COL, "The " + character.name + " has gone " + to_dir +
                                       " towards the " + new_loc.getName() +
                                       ".");
              } else {
                  this.conn.sendOutput(ServerConn.ACTION_COL, "The " + character.name + " has gone towards the " + new_loc.getName() +
                                       ".");
              }
	  } else {
              if (character.getHealth() > 0) { // So we don't say anything if they've died.
                  this.conn.sendOutput(ServerConn.ERROR_COL, "The " + character.name + " has left.");
              }
	  }
  }

  public String getFightMethod() {
    return "attack";
  }

  public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
    // Do nothing
  }

  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
    // Do nothing
  }

  public boolean spokenTo(GameCharacter character, String msg, boolean only_them) throws IOException {
    if (only_them) {
        this.conn.sendOutput(ServerConn.SPEECH_COL, "The " + character.name + " says to you '" + msg + "'.");
        return true;
    }
    return false;
  }

  public boolean attemptBuyFrom(GameCharacter character, String item_name) throws IOException {
      return false;
  }

  public void kissedBy(GameCharacter character) throws IOException {
    this.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + character.name + " kisses you.");
}

    public void greeted(GameCharacter character) throws IOException {
        this.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + character.name + " greets you.");
    }

    public void touched(GameCharacter character) throws IOException {
        this.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + character.name + " touches you.");
    }

    public void mugged(GameCharacter charac) throws IOException {
        this.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + charac.name + " tries to mug you.");
    }
}
