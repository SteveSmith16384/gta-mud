package gta_text.items;

import gta_text.Game;
import gta_text.locations.Location;
import gta_text.npcs.GameCharacter;
import java.io.IOException;
import java.util.Enumeration;
import ssmith.io.TextFile;
import ssmith.lang.Functions;
import gta_text.*;
import java.util.ArrayList;

public abstract class Item {

    // CSV file cols
    private static final int ITEM_TYPE = 1, ITEM_NAME=2, ITEM_DESC=3, ITEM_AMMO=4, ITEM_DAM=5, ITEM_COST=6, ITEM_EDIBLE=7, ITEM_CARRYABLE=8, ITEM_PERMANENT=9;

    public static final int STICK = 1, AK47=2, FIRST_AID_KIT=3, PISTOL=4, GRENADE=5;
    public static final int LAGER = 6, BURGER=7, CORPSE=8, UZI=9, KNIFE=10, SHOT=11;
    public static final int POSTER=12, GANDT=13, FRIES=14, NEWSPAPER=15, SHOTGUN=16;
    public static final int BEER=17, CASHCARD=18, PEPPER_SPRAY=19, LEAFLET=20, CARDS=21;
    public static final int NOTICEBOARD=22, KEY=23, PLAQUE=24, MAP=25, PACKAGE=26;
    public static final int NOTE=27, NOODLES=28, POO=29, CIGS=30, COKE=31, WATER=32;
    public static final int PICTURE=33, BED=34, TOILET=35, MASSAGE_OIL=36, MASSAGE_BED=37;
    public static final int VOMIT=38, PIZZA=39, CLEAVER=40, NOTICE=41, DAILY_MAIL=42;

  public String name;
  protected String desc; // Must be protected as we need to change the desc
  public int type, ammo, max_damage;
  public int cost;
  public boolean edible, carryable, permanent;
  public ArrayList other_names = new ArrayList();
  public long time_created;

  public Item(int type) throws IOException {
      this.type = type;

      if (ServerConn.DEBUG_OBJS) {
          //System.out.println(this.toString() + " created.");
      }

      boolean found = false;

      TextFile tf = new TextFile();
      tf.openFile(Game.DATA_DIR + "items.txt", TextFile.READ);
      tf.readLine();
      while (tf.isEOF() == false) {
          String line = tf.readLine();
          if (line.startsWith("#") == false) {
              int t = new Integer(Functions.GetParam(ITEM_TYPE, line,
                      Game.SEPERATOR)).intValue();
              if (t == type) {
                  found = true;
              name = Functions.GetParam(ITEM_NAME, line, Game.SEPERATOR);
              desc = Functions.GetParam(ITEM_DESC, line, Game.SEPERATOR);
              ammo = new Integer(Functions.GetParam(ITEM_AMMO, line, Game.SEPERATOR)).intValue();
              max_damage = new Integer(Functions.GetParam(ITEM_DAM, line, Game.SEPERATOR)).intValue();
              cost = new Integer(Functions.GetParam(ITEM_COST, line, Game.SEPERATOR)).intValue();
              edible = Functions.GetParam(ITEM_EDIBLE, line, Game.SEPERATOR).equalsIgnoreCase("Y");
              carryable = Functions.GetParam(ITEM_CARRYABLE, line, Game.SEPERATOR).equalsIgnoreCase("Y");
              permanent = Functions.GetParam(ITEM_PERMANENT, line, Game.SEPERATOR).equalsIgnoreCase("Y");

              this.other_names.add(name);
              }
          }
      }
       tf.close();

       if (!found) {
           throw new IOException("Type " + type + " not found in item file.");
       }

       this.time_created = System.currentTimeMillis();
       Server.game.items.add(this);
  }

  /**
   * Override if reqd.
   *
   */
  public void process() throws IOException {
  }

  /**
   * This returns whether they can actually use it.
   */
  public abstract boolean use(GameCharacter character) throws IOException;

  /**
   * Override if reqd.  Returns whether it should be removed from the game.
   * @param charac
   */
  public boolean eat(GameCharacter charac, String style) throws IOException {
      return false;
  }

  public String getDesc() throws IOException {
    return desc;
  }

  public Location getLocationSlow() {
      Enumeration enumr = null;
      enumr = Server.game.locations.elements();
      while (enumr.hasMoreElements()) {
          Location loc = (Location) enumr.nextElement();
          if (loc.containsItem(this.name)) {
              return loc;
          }
      }
      return null;
  }

  public void removeFromGame(boolean from_locs, boolean from_chars) throws IOException {
      Enumeration enumr = null;

      // Process locations
      if (from_locs) {
          Location loc = this.getLocationSlow();
          if (loc != null) {
              Server.game.informAllCharsInLocation(loc,
                      "The " + this.name + " disintegrates.");
              loc.removeItem(this);
          }
      }

      // Process characters
      if (from_chars) {
          enumr = Server.game.getCharacterEnum();
          while (enumr.hasMoreElements()) {
              GameCharacter charac = (GameCharacter) enumr.nextElement();
              if (charac.hasItem(this)) {
                  charac.removeItem(this);
                  charac.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + this.name + " disappears.");
              }
          }
      }

      Server.game.items.remove(this);
  }

  public String read(GameCharacter character) throws IOException {
      // Override if reqd
      return this.getDesc();
  }

}
