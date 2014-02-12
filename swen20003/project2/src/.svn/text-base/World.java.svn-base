/* 433-294 Object Oriented Software Development
 * RPG Game Engine
 * Sample Solution
 * Author: Matt Giuca <mgiuca>
 */

import java.util.Enumeration;
import java.util.Vector;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

/** Represents the entire game world.
 * (Designed to be instantiated just once for the whole game).
 */
public class World
{
    private Player player = null;
    private TiledMap map = null;
    Image winner = new Image("assets/winner.png");
 
    //private Enemy enemies = null;
    Vector<Entity> enemies = new Vector<Entity>();
    Vector<Entity> passive = new Vector<Entity>();
    Vector<Entity> blood = new Vector<Entity>();
    Vector<Entity> items = new Vector<Entity>();
    Vector<Entity> item_bag = new Vector<Entity>();
    Vector<Entity> NPC = new Vector<Entity>();
    

    // Camera location, in pixels, relative to top-left of screen
    private int cam_x = 0;
    private int cam_y = 0;

    /** Camera X location, in pixels, relative to top-left of screen. */
    public int getCamX()
    {
        return cam_x;
    }

    /** Camera Y location, in pixels, relative to top-left of screen. */
    public int getCamY()
    {
        return cam_y;
    }

    /** Map width, in tiles. */
    public int getMapWidth()
    {
        return map.getWidth();
    }

    /** Map height, in tiles. */
    public int getMapHeight()
    {
        return map.getHeight();
    }

    /** Tile width, in pixels. */
    public int getTileWidth()
    {
        return map.getTileWidth();
    }

    /** Tile height, in pixels. */
    public int getTileHeight()
    {
        return map.getTileHeight();
    }

    /** Create a new World object. */
    public World()
    throws SlickException
    {
        map = new TiledMap(RPG.ASSETS_PATH + RPG.MAP_PATH, RPG.ASSETS_PATH);
        player = new Player(RPG.ASSETS_PATH + RPG.PLAYER_IMAGE_PATH);
        
        //initialise our enemy units and items
        enemies.add(new Zombie(1260, 540));
        enemies.add(new Zombie(1476, 900));
        enemies.add(new Zombie(1548, 1332));
        enemies.add(new Zombie(756, 2556));
        enemies.add(new Zombie(900, 2844));
        enemies.add(new Zombie(1980, 2412));
        enemies.add(new Zombie(2844, 1548));
        enemies.add(new Zombie(2196, 1044));
        enemies.add(new Zombie(2988, 396));
        enemies.add(new Bandit(1116, 1476));
        enemies.add(new Bandit(1260, 1908));
        enemies.add(new Bandit(540, 1476));
        enemies.add(new Bandit(1404, 2484));
        enemies.add(new Bandit(2772, 2556));
        enemies.add(new Bandit(2052, 1548));
        enemies.add(new Bandit(1980, 1404));
        enemies.add(new Skeleton(2916, 974));
        enemies.add(new Skeleton(1980, 612));
        enemies.add(new Skeleton(2052, 972));
        enemies.add(new Skeleton(2772, 540));
        enemies.add(new Draelic(2052, 548));
        NPC.add(new Elvira(900, 540));
        NPC.add(new Garth(828, 828));
        NPC.add(new Prince(540, 612));
        items.add(new Sword(1980, 1476));
        items.add(new Elixir(2052, 396));
        items.add(new Amulet(972, 2916));
        items.add(new Tome(2052, 900));
        passive.add(new Bat(1260, 540));
        passive.add(new Bat(1476, 900));
        passive.add(new Bat(1548, 1332));
        passive.add(new Bat(756, 2556));
        passive.add(new Bat(1260, 1908));
        passive.add(new Bat(2772, 2556));
        passive.add(new Bat(2844, 1548));
        passive.add(new Bat(2196, 1044));
        passive.add(new Bat(2916, 974));
        passive.add(new Bat(1980, 612));
        passive.add(new Bat(2052, 912));
        passive.add(new Bat(2772, 540));
 
    }

    /** Update the game state for a frame.
     * @param dir_x The player's movement in the x axis (-1, 0 or 1).
     * @param dir_y The player's movement in the y axis (-1, 0 or 1).
     * @param delta Time passed since last frame (milliseconds).
     */
    public void update(double dir_x, double dir_y, int delta)
    		throws SlickException
    {
        player.move(this, dir_x, dir_y, delta, enemies, blood, items, item_bag, NPC, passive);
        //move enemies and check if dead.
        for(Enumeration<Entity> e = enemies.elements(); e.hasMoreElements();){
        	
        	Enemy enemy = (Enemy)e.nextElement();
        	enemy.move(this, player.getX(), player.getY(), delta, player, blood);
        	if (enemy.alive == false)
        	{
        		enemies.removeElement(enemy);
        		player.levelup();
        	}
        }
        for(Enumeration<Entity> e = passive.elements(); e.hasMoreElements();){
        //move passive creatures and check if dead	
        	Passive passive_c = (Passive)e.nextElement();
        	passive_c.move(this, player.getX(), player.getY(), delta, player, blood);
        	if (passive_c.alive == false)
        		passive.removeElement(passive_c);
        	
        }

        // Update the camera based on the player's position
        cam_x = (int) player.getX() - (RPG.screenwidth/2);
        cam_y = (int) player.getY() - (RPG.screenheight/2) + RPG.panelheight/2;
        
        
    }

    /** Render the entire screen, so it reflects the current game state.
     * @param g The Slick graphics object, used for drawing.
     */
    public void render(Graphics g)
    throws SlickException
    {
        
    	// Calculate the camera location (in tiles) and offset (in pixels)
        int cam_tile_x = cam_x / getTileWidth();
        int cam_offset_x = cam_x % getTileWidth();
        int cam_tile_y = cam_y / getTileHeight();
        int cam_offset_y = cam_y % getTileHeight();
        int screen_tilewidth = RPG.screenwidth / getTileWidth() + 2;    // 13
        int screen_tileheight = RPG.screenheight / getTileHeight() + 2; // 10
        map.render(-cam_offset_x, -cam_offset_y, cam_tile_x, cam_tile_y,
            screen_tilewidth, screen_tileheight);
        
        // Render bloodstains
        for(Enumeration<Entity> e = blood.elements(); e.hasMoreElements();){
        	Bloodstain blood = (Bloodstain)e.nextElement();
        	blood.render(g,cam_x,cam_y);
        }
        player.render(g, cam_x, cam_y);
        //render all enemies
        for(Enumeration<Entity> e = enemies.elements(); e.hasMoreElements();){
        	Enemy enemy = (Enemy)e.nextElement();
        	enemy.render(g,cam_x,cam_y);
        }
        //render all items
        for(Enumeration<Entity> e = items.elements(); e.hasMoreElements();){
        	Item items = (Item)e.nextElement();
        	items.render(g,cam_x,cam_y);
        }
        //render all non combat NPCs
        for(Enumeration<Entity> e = NPC.elements(); e.hasMoreElements();){
        	NPC npc = (NPC)e.nextElement();
        	npc.render(g,cam_x,cam_y);
        }
        //render passive creatures
        for(Enumeration<Entity> e = passive.elements(); e.hasMoreElements();){
        	
        	Passive passive_c = (Passive)e.nextElement();
        	passive_c.render(g, cam_x, cam_y);
        }
        player.renderPanel(g, item_bag);
        //check win state, if winner, draw YOU'RE WINNER
        if(player.winner == true)
        {
        	winner.drawCentered(RPG.screenwidth/2, RPG.screenheight/2);
        }
        
     }

    /** Determines whether a particular map coordinate blocks movement.
     * @param x Map x coordinate (in pixels).
     * @param y Map y coordinate (in pixels).
     * @return true if the coordinate blocks movement.
     */
    public boolean terrainBlocks(double x, double y)
    {
        int tile_x = (int) x / getTileWidth();
        int tile_y = (int) y / getTileHeight();
        int tileid = map.getTileId(tile_x, tile_y, 0);
        String block = map.getTileProperty(tileid, "block", "0");
        return !block.equals("0");
    }
}
