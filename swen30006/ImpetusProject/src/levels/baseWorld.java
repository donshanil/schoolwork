package levels;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import entity.Player;
import levels.LevelInfo;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.tiled.TiledMap;

import saving.GameData;
import states.ImpetusGame;

import java.lang.Math;

public class baseWorld {
	
	private int cam_x = 0;
	private int cam_y = 0;
	public static final double GRAVITY = 9.8;
	public static final double SCALE_FACTOR = 0.00012; //this controls the scale of the game world.
	//public static final double SCALE_FACTOR = Math.pow(10,4)/4;
	private TiledMap map = null;
	private Player player = null;
	private double gravity;
	GameData theGameData;
	private int levelBeforeExit;
	private double frictionCoeff;
	Color VALUE = new Color(1.0f, 1.0f, 1.0f);  
	
	private ArrayList<LevelInfo> levels = new ArrayList<LevelInfo>();

	/*
	public baseWorld()
	throws SlickException
	{
		map = new TiledMap("assets/levels/level1.tmx");
		player = new Player(100, 544);
		gravity = 0.0007;
		
		levels.add(0, new LevelInfo("level1.tmx", 100, 544, 0.0007));
		
		levels.add(1, new LevelInfo("level2.tmx", 200, 544, 0.0007));
	}*/
	
	public baseWorld(int levelNumber, GameData theGameData)
	throws SlickException
	{
		levels.add(0, new LevelInfo("level1.tmx", 100, 544, GRAVITY));
		levels.add(1, new LevelInfo("level2.tmx", 200, 544, GRAVITY));
		levels.add(2, new LevelInfo("level3.tmx", 200, 544, GRAVITY));
		levels.add(3, new LevelInfo("level4.tmx", 100, 1364, GRAVITY));
		levels.add(4, new LevelInfo("level5.tmx", 100, 1364, GRAVITY));
		levels.add(5, new LevelInfo("level6.tmx", 100, 1364, GRAVITY));
		
		map = new TiledMap(levels.get(levelNumber).getLevelPath());
		player = new Player(levels.get(levelNumber).getStart_x(), levels.get(levelNumber).getStart_y(), SCALE_FACTOR);
		gravity = levels.get(levelNumber).getGravity();
		
		this.theGameData = theGameData;
		levelBeforeExit = theGameData.getCurrLevel();
		
	}
	
	public void update(double dir_x, double jump, int delta) throws SlickException
	{
		if(theGameData.getCurrLevel() != levelBeforeExit) {
			levelBeforeExit = theGameData.getCurrLevel();
			map = new TiledMap(levels.get(levelBeforeExit).getLevelPath());
			player = new Player(levels.get(levelBeforeExit).getStart_x(), levels.get(levelBeforeExit).getStart_y(), SCALE_FACTOR);
			gravity = levels.get(levelBeforeExit).getGravity();
		}
		
		player.move(this, dir_x, jump, delta);
		
		cam_x = (int) player.getScaledX() - (ImpetusGame.screenwidth/2);
		cam_y = (int) player.getScaledY() - (ImpetusGame.screenheight/2);
	}
	
	public void render(Graphics g)
			throws SlickException
	{
        int cam_tile_x = cam_x / getTileWidth();
        int cam_offset_x = cam_x % getTileWidth();
        int cam_tile_y = cam_y / getTileHeight();
        int cam_offset_y = cam_y % getTileHeight();
        int screen_tilewidth = ImpetusGame.screenwidth / getTileWidth() + 2;  
        int screen_tileheight = ImpetusGame.screenheight / getTileHeight() + 2;
		
			
		NumberFormat formatter = new DecimalFormat("#0.00");
		
		 map.render(-cam_offset_x, -cam_offset_y, cam_tile_x, cam_tile_y,
		            screen_tilewidth, screen_tileheight);
		player.render(g, cam_x, cam_y);
		
		g.setColor(VALUE);
		g.drawString("Friction Coefficient:\n" + String.valueOf(frictionCoeff), 20, ImpetusGame.screenheight - 50);
		g.drawString("Gravitational Pull:\n"+String.valueOf(gravity), 220, ImpetusGame.screenheight - 50);
		g.drawString("x,y velocity: \n"+ formatter.format( player.getX_velocity()* SCALE_FACTOR) +
				"," + formatter.format( player.getY_velocity()* SCALE_FACTOR), 420, ImpetusGame.screenheight - 50);
		
	}
	
	public double getGravity()
	{
		return gravity;
	}
	
	public int getCamX()
	{
		return cam_x;
	}
	
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
	
    public Rectangle getTileRectangle(double x, double y)
    {
    	//this method, given an input x and y coordinate will return a rectangle to represent a tile.
    	Rectangle tile_box;
        int tile_x = (int) x / getTileWidth(); //will truncate the decimals
        int tile_y = (int) y / getTileHeight();
        tile_box = new Rectangle (tile_x*getTileWidth(), tile_y*getTileHeight(), getTileWidth(), getTileHeight());
    	return tile_box;
    }
    
	public boolean isSolid(double x, double y)
	{
        int tile_x = (int) x / getTileWidth();
        int tile_y = (int) y / getTileHeight();
        int tileid = map.getTileId(tile_x, tile_y, 0);
		String solid = map.getTileProperty(tileid, "solid", "0"); 
		return !solid.equals("0");
	}
	
	public boolean collisionDetect(double x, double y, String collisionTypeString)
	{
        int tile_x = (int) x / getTileWidth();
        int tile_y = (int) y / getTileHeight();
        int tileid = map.getTileId(tile_x, tile_y, 0);
		String collision = map.getTileProperty(tileid, collisionTypeString, "0"); 
		return !collision.equals("0");
	}
	
	public double getFrictionCoeff(double x, double d) {
        int tile_x = (int) x / getTileWidth();
        int tile_y = (int) d / getTileHeight();
        int tileid = map.getTileId(tile_x, tile_y, 0);
        //System.out.println(Double.parseDouble(map.getTileProperty(tileid, "friction", "0")));
		frictionCoeff = Double.parseDouble(map.getTileProperty(tileid, "friction", "0.3"));
		return frictionCoeff; 
	}
		
	
	public void nextLevel() 
	throws SlickException
	{
		int level = theGameData.getCurrLevel();
		if (level + 1 < levels.size())
		{
			level++;
			theGameData.setCurrLevel(level);
			levelBeforeExit = level;
			map = new TiledMap(levels.get(level).getLevelPath());
			player = new Player(levels.get(level).getStart_x(), levels.get(level).getStart_y(), SCALE_FACTOR);
			this.gravity = levels.get(level).getGravity();
		}
		else
		{
			player.winner = true;
		}
	}
	
	public void loadLevel(int levelNumber)
	throws SlickException
	{
		theGameData.setCurrLevel(levelNumber);
		levelBeforeExit = levelNumber;
		map = new TiledMap(levels.get(levelNumber).getLevelPath());
		player = new Player(levels.get(levelNumber).getStart_x(), levels.get(levelNumber).getStart_y(), SCALE_FACTOR);
		this.gravity = levels.get(levelNumber).getGravity();
	}

	public Player getPlayer(){
		return player;
	}

	/**
	 * @return the levelBeforeExit
	 */
	public int getLevelBeforeExit() {
		return levelBeforeExit;
	}
	
	public double getScaleFactor()
	{
		return SCALE_FACTOR;
	}
}
