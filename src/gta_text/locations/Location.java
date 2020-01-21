package gta_text.locations;

import gta_text.items.Item;
import gta_text.npcs.GameCharacter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import ssmith.util.CSVString;
import ssmith.lang.Functions;
import gta_text.*;
import gta_text.npcs.PlayersCharacter;

public abstract class Location {

    // Location id's
    public static final int BRIDGE=1, REDLIGHT_DISTRICT=2, HOSPITAL=3, AMMONATION=4, POLICE_STATION=5, MF_CHICKEN=6, PORTLAND_WAY=13, NIGHTCLUB=14;
    public static final int ABANDONED_WAREHOUSE=15, HC_CAFE=16, MAFIA_HOUSE=17, BANK=19, BANK_VAULT=20;
    public static final int GAMBLING_ROOM=21, CASINO=23, PARK=24, ROULETTE_ROOM=25, SLOT_ROOM=26;
    public static final int DANCEFLOOR=27, CHINESE_TAKEAWAY=28, CHINESE_KITCHEN=29, BELVEDERE=30;
    public static final int MANSION=31, BEDROOM=32, BAR_TOILETS=33, MASSAGE_PARLOUR=34, CASINO_TOILETS=35, HOTEL_RECEPTION=38;
    public static final int PAWN_SHOP = 39,PIZZA_AL_FORNICATE=41, LAUNDRETTE=43;

  private Vector items = new Vector();
  private Vector characters = new Vector();
  public Hashtable all_exits = new Hashtable();
  public Hashtable visible_exits = new Hashtable();
  public int cash=0;
  public boolean is_open = true, internal, wanderable;
  public String closed_reason;
  public int start_time, end_time;
  public String no;
  private String name, desc; // Need to be private so we can format them on the way out
  private long time_of_last_player = System.currentTimeMillis();
  public GameCharacter robber;

  public Location(String n) {
      this.no = n;
      this.closed_reason = "It is closed.";

      Server.game.locations.put(no, this);
  }

  public abstract void regenChars() throws IOException;

  public String getVisibleExits() {
    try {
        StringBuffer str = new StringBuffer();
        str.append("Visible exits are ");
        Enumeration enumr = visible_exits.keys();
        while (enumr.hasMoreElements()) {
            str.append((String) enumr.nextElement());
            str.append(", ");
        }
        if (str.length() > 0) {
            str.delete(str.length() - 2, str.length());
            str.append(".");
        }
        return str.toString();
    } catch (Exception ex) {
        Server.HandleError(ex);
        return "";
    }
  }

  public String getDesc(GameCharacter viewer) {
	  return getDesc(viewer, false);
  }

  public String getDesc(GameCharacter viewer, boolean debug) {
      if (viewer.blinded == false) {
          StringBuffer str = new StringBuffer();
          str.append(Server.CR + name + Server.CR + desc + getBespokeDesc() + Server.CR + this.getVisibleExits() + Server.CR);
          // List items
          if (cash > 0) {
              str.append("There is some cash here." + Server.CR);
          }
          if (this.items.size() > 0) {
              for (int i = 0; i < items.size(); i++) {
                  Item item = (Item) items.get(i);
                  str.append("You can see a " + item.name + "." + Server.CR);
                  if (debug) {
                	  str.append("It has been here for " + ((System.currentTimeMillis() - item.time_created)/1000) + " seconds." + Server.CR);
                  }
              }
          }

          // List characters
          if (this.characters.size() > 1) { // > 1 as we're there too
              //str.append(Server.CR + "Characters: ");
              Enumeration enumr = this.getCharacterEnum();
              while (enumr.hasMoreElements()) {
                  GameCharacter charac = (GameCharacter) enumr.nextElement();
                  if (charac.name.equalsIgnoreCase(viewer.name) == false) {
                      str.append("There is a " + charac.name + " here." +
                                 Server.CR);
                  }
              }
          }

          return str.toString();
      } else {
          return "You try and see where you are but you can't see a thing.";
      }
  }

  /**
   * Override if reqd.
   * @return
   */
  public String getBespokeDesc() {
      return "";
  }

  public void addCharacter(GameCharacter charac) throws IOException {
      this.characters.add(charac);
  }

  public boolean containsCharacter(String name) {
      return getCharacter(name) != null;
/*      for(int i=0 ; i<characters.size() ; i++) {
          GameCharacter charac = (GameCharacter)characters.get(i);
          if (charac.name.toUpperCase().indexOf(name.toUpperCase()) >= 0) {
              return true;
          }
      }
      return false;*/
  }

  public boolean containsPlayer() {
      for(int i=0 ; i<characters.size() ; i++) {
          GameCharacter charac = (GameCharacter)characters.get(i);
          if (charac instanceof PlayersCharacter) {
              return true;
          }
      }
      return false;
  }

  public boolean containsCharacter(GameCharacter charac) {
      return this.characters.contains(charac);
  }

  public Enumeration getCharacterEnum() {
      return this.characters.elements();
  }

  public Enumeration getItemEnum() {
      return this.items.elements();
  }

  public void addItem(Item item) {
      this.items.add(item);
  }

  public void addItems(Vector v) {
      this.items.addAll(v);
  }

  public boolean containsItem(Item item) {
      return this.items.contains(item);
  }

  public boolean containsItem(String name) {
      return getItem(name) != null;
  }

  public void removeCharacter(GameCharacter charac) {
      this.characters.remove(charac);
  }

  public int getNoOfCharacters() {
      return this.characters.size();
  }

  public int getNoOfItems() {
      return this.items.size();
  }

  public GameCharacter getCharacter(String name) {
      for(int i=0 ; i<characters.size() ; i++) {
          GameCharacter charac = (GameCharacter)this.characters.get(i);
          if (charac.name.toUpperCase().indexOf(name.toUpperCase()) >= 0) {
            return charac;
          }
      }
      return null;
  }

  public Item getItem(String name) {
      int idx = 1;
      if (name.indexOf(".") >= 0) {
          CSVString csv = new CSVString(name, ".");
          String no = csv.getFirstSection();
          if (Functions.isNumeric(no)) {
              idx = new Integer(no).intValue();
          }
          name = csv.getSection(1);
      }

      for(int i=0 ; i<items.size() ; i++) {
          Item item = (Item)items.get(i);
          if (item.name.toUpperCase().indexOf(name.toUpperCase()) >= 0) {
              idx--;
              if (idx == 0) {
                  return item;
              }
          }
      }
      return null;
  }

  public Item getItem(int n) {
      return (Item)items.get(n);
  }

  public void removeItem(String name) {
      for(int i=0 ; i<items.size() ; i++) {
          Item item = (Item)items.get(i);
          if (item.name.equalsIgnoreCase(name)) {
              items.remove(i);
              break;
          }
      }
  }

  public void removeItem(Item it) {
/*      for(int i=0 ; i<items.size() ; i++) {
          Item item = (Item)items.get(i);
          if (item == it) {
              items.remove(i);
              break;
          }
      }*/
      items.remove(it);
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    name = n;
  }

  public void setDesc(String d) {
    desc = d;
  }

  public void process() throws IOException {
      if (ServerConn.DEBUG_OBJS) {
          if (this.getNoOfItems() > 10) {
              System.err.println("Location " + name + " has more than 10 items.");
          }
      }

      if (this.containsPlayer()) {
          this.time_of_last_player = System.currentTimeMillis();
      } else {
    	  // This is where we remove corpses etc...
          if (System.currentTimeMillis() - this.time_of_last_player > (1000*60*10)) {
              for (int i=0 ; i<this.items.size() ; i++) {
                  Item item = (Item)items.get(i);
                  if (item.permanent == false) {
                      this.items.remove(item);
                      item.removeFromGame(false, false);
                  }
              }
              this.cash = 0; // Remove cash
          }
      }

      if (this.containsCharacter(robber) == false) {
          this.robber = null;
      }
  }

  public Location getExitLocation(String dir) {
    if (this.all_exits.containsKey(dir.toUpperCase())) {
      String exit_no = (String)this.all_exits.get(dir.toUpperCase());
      return (Location)Server.game.locations.get(exit_no);
    } else {
      return null;
    }
  }

  public String getRandomExitDir() {
      int k = Functions.rnd(0, all_exits.size()-1);
      Enumeration enumr = all_exits.keys();
      for(int x=0 ; x<k ; x++) {
          enumr.nextElement();
      }
      return (String)enumr.nextElement();
  }

  /**
   * Override if reqd.
   * @param cmd String
   * @return boolean
   */
  public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
      return false;
  }

}
