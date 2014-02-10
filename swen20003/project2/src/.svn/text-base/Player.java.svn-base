/* 433-294 Object Oriented Software Development
 * RPG Game Engine
 * Sample Solution
 * Author: Matt Giuca <mgiuca>
 */

import java.util.Enumeration;
import java.util.Vector;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/** The character which the user plays as.
 */
public class Player extends Unit
{

	public static final float PLAYER_STARTING_X = 756;
	public static final float PLAYER_STARTING_Y = 684;
	public static final int INITIAL_HP = 100;
	public static final float COOLDOWN = 600;
	public static final int INITIAL_DAMAGE = 26;
	public static final boolean BLEEDS = true;
	public static final String PANEL_LOCATION = "assets/panel.png";
	public static final int RESPAWN_X = 900;
	public static final int RESPAWN_Y = 590;
	public boolean winner = false;
	public Image panel;
	public String HIT_SOUND_LOCATION = "assets/slice.wav";
	public String RESPAWN = "assets/respawn.ogg";
	Sound hit = new Sound(HIT_SOUND_LOCATION);
	Sound respawn_sound = new Sound(RESPAWN);
	

	// In pixels

	//private boolean face_left = false;

	// Pixels per millisecond
	private static final double SPEED = 0.25;

	/** Creates a new Player.
	 * @param image_path Path of player's image file.
	 */
	public Player(String image_path)
			throws SlickException
			{
		//intialise our player.
		img = new Image(image_path);
		panel = new Image(PANEL_LOCATION);
		img_flipped = img.getFlippedCopy(true, false);
		this.x = PLAYER_STARTING_X;
		this.y = PLAYER_STARTING_Y;
		this.hp = INITIAL_HP;
		this.max_hp = INITIAL_HP;
		this.max_damage = INITIAL_DAMAGE;
		this.max_cooldown = COOLDOWN;
		this.cooldown = COOLDOWN;
		this.bleeds = BLEEDS;
			}

	/** Move the player in a given direction.
	 * Prevents the player from moving outside the map space, and also updates
	 * the direction the player is facing.
	 * @param world The world the player is on (to check blocking).
	 * @param dir_x The player's movement in the x axis (-1, 0 or 1).
	 * @param dir_y The player's movement in the y axis (-1, 0 or 1).
	 * @param delta Time passed since last frame (milliseconds).
	 * @throws SlickException 
	 */
	
	public void respawn()
	{
		//respawns player
		respawn_sound.play(1, (float) 0.35);
		this.x = RESPAWN_X;
		this.y = RESPAWN_Y;
		this.hp = this.max_hp;
	}
	public void levelup()
	{
		this.buffHP(3);
		this.buffCooldown(5);
	}
	public void move(World world, double dir_x, double dir_y, int delta, Vector<Entity> enemies, Vector<Entity> blood, Vector<Entity>items, Vector <Entity>item_bag, Vector<Entity>npc, Vector<Entity> passive) throws SlickException
	{
		
		//check if dead
		if(this.hp <= 0){
			respawn();
		}

		//update cooldown
		if (this.cooldown > 0){ 
			this.cooldown = this.cooldown - (float)delta;
		}
		// Update player facing based on X direction
		if (dir_x > 0)
			this.face_left = false;
		else if (dir_x < 0)
			this.face_left = true;

		// Move the player by dir_x, dir_y, as a multiple of delta * speed
		double new_x = this.x + dir_x * delta * SPEED;
		double new_y = this.y + dir_y * delta * SPEED;

		// Check if the player is on a tile which blocks movement, and stop
		if (world.terrainBlocks(new_x, new_y))
		{
			// Reset either the x or the y or both to the old one
			if (!world.terrainBlocks(this.x, new_y))
				new_x = this.x;
			else if (!world.terrainBlocks(new_x, this.y))
				new_y = this.y;
			else
			{
				new_x = this.x;
				new_y = this.y;
			}
		}

		//check if blocked by enemy or you've hit an NPC
		for(Enumeration<Entity> e = enemies.elements(); e.hasMoreElements();){
			//instanceof
			Unit entity = (Unit)e.nextElement();
			if (entity.getDistance(new_x, new_y) <=50)
			{

				if(entity instanceof Enemy)
				{
					Enemy enemy = (Enemy) entity;
					if(this.cooldown <= 0)
					{
						
						//if you hit them, spray blood if applicable
						hit.play(1, (float) 0.3);
						if(entity.getBleed() == true)
						{
							blood.add(new Bloodstain(entity.getX(), entity.getY()));
							
						}
						entity.damage(this.max_damage);
						this.cooldown = this.max_cooldown;
					}
					if (enemy.isXBlocked(new_x, this.y))
					{
						new_x = this.x;
					}
					if (enemy.isYBlocked(this.x, new_y))
					{
						new_y = this.y;
					}
				}
				
			}
		}
		//check collision with items
		for(Enumeration<Entity> e = items.elements(); e.hasMoreElements();){

			Item entity = (Item)e.nextElement();
			if (entity.getDistance(new_x, new_y) <=50)
			{
				if(entity instanceof Item)
				{
					entity.interact(this);
					//add the item to my item bag
					item_bag.add(entity);
					//then remove from world
					items.remove(entity);

				}

			}
		}
		//check collision with passive entities
		//i wasn't able to combine them into unit without it breaking
		for(Enumeration<Entity> e = passive.elements(); e.hasMoreElements();){

			Unit entity = (Unit)e.nextElement();
			if (entity.getDistance(new_x, new_y) <=50)
			{

				if(entity instanceof Passive)
				{
					Passive harmless = (Passive) entity;
					if(this.cooldown <= 0)
					{
						
						//if you hit them, spray blood if applicable
						hit.play(1, (float) 0.3);
						if(harmless.getBleed() == true)
						{
							blood.add(new Bloodstain(entity.getX(), entity.getY()));
							
						}
						harmless.damage(this.max_damage);
						this.cooldown = this.max_cooldown;

					}
					if (harmless.isXBlocked(new_x, this.y))
					{
						new_x = this.x;
					}
					if (harmless.isYBlocked(this.x, new_y))
					{
						new_y = this.y;
					}
				}
				
			}
		}
		for(Enumeration<Entity> e = npc.elements(); e.hasMoreElements();){
			//instanceof
			NPC entity = (NPC)e.nextElement();
			if (entity.getDistance(new_x, new_y) <=50)
			{
				if(entity instanceof NPC)
				{
					//if we interact with the prince, check for the elixir of life.
					if(entity instanceof Prince)
					{
						Prince prince = (Prince) entity;
						prince.interact(this, item_bag);
						winner = prince.checkWin();
												
					}
					else
					{
						entity.interact(this);
					
					}
				}

			}
		}
		
		// Commit to the new location
		this.x = new_x;
		this.y = new_y;
	}

	public void buffHP(int hp)
	{
		this.hp += hp;
		this.max_hp += hp;

	}
	public void buffDamage(int damage)
	{
		this.max_damage += damage;
	}
	public void buffCooldown(float cooldown)
	{
		this.max_cooldown -= cooldown;
	}
	/** Draw the player to the screen at the correct place.
	 * @param g The current Graphics context.
	 * @param cam_x Camera x position in pixels.
	 * @param cam_y Camera y position in pixels.
	 */
	public void render(Graphics g, int cam_x, int cam_y)
	{
		Image which_img;
		// Calculate the player's on-screen location from the camera
		int screen_x, screen_y;
		screen_x = (int) (this.x - cam_x);
		screen_y = (int) (this.y - cam_y);
		which_img = this.face_left ? this.img_flipped : this.img;
		which_img.drawCentered(screen_x, screen_y);
	}



	/** Renders the player's status panel.
	 * @param g The current Slick graphics context.
	 */
	public void renderPanel(Graphics g, Vector<Entity>item_bag)
	{
		// Panel colours
		Color LABEL = new Color(0.9f, 0.9f, 0.4f);          // Gold
		Color VALUE = new Color(1.0f, 1.0f, 1.0f);          // White
		Color BAR_BG = new Color(0.0f, 0.0f, 0.0f, 0.8f);   // Black, transp
		Color BAR = new Color(0.8f, 0.0f, 0.0f, 0.8f);      // Red, transp

		// Variables for layout
		String text;                // Text to display
		int text_x, text_y;         // Coordinates to draw text
		int bar_x, bar_y;           // Coordinates to draw rectangles
		int bar_width, bar_height;  // Size of rectangle to draw
		int hp_bar_width;           // Size of red (HP) rectangle
		int inv_x, inv_y;           // Coordinates to draw inventory item

		float health_percent;       // Player's health, as a percentage

		// Panel background image
		panel.draw(0, RPG.screenheight - RPG.panelheight);

		// Display the player's health
		text_x = 15;
		text_y = RPG.screenheight - RPG.panelheight + 25;
		g.setColor(LABEL);
		g.drawString("Health:", text_x, text_y);
		text = (Integer.toString(this.getHP())+"/"+Integer.toString(this.getMaxHP()));                                 // TODO: HP / Max-HP

		bar_x = 90;
		bar_y = RPG.screenheight - RPG.panelheight + 20;
		bar_width = 90;
		bar_height = 30;
		health_percent = ((float)this.getHP()/(float)this.getMaxHP()); 
		// TODO: HP / Max-HP
		hp_bar_width = (int) (bar_width * health_percent);
		text_x = bar_x + (bar_width - g.getFont().getWidth(text)) / 2;
		g.setColor(BAR_BG);
		g.fillRect(bar_x, bar_y, bar_width, bar_height);
		g.setColor(BAR);
		g.fillRect(bar_x, bar_y, hp_bar_width, bar_height);
		g.setColor(VALUE);
		g.drawString(text, text_x, text_y);

		// Display the player's damage and cooldown
		text_x = 200;
		g.setColor(LABEL);
		g.drawString("Damage:", text_x, text_y);
		text_x += 80;
		text = Integer.toString(this.getMaxDamage());                                    // TODO: Damage
		g.setColor(VALUE);
		g.drawString(text, text_x, text_y);
		text_x += 40;
		g.setColor(LABEL);
		g.drawString("Rate:", text_x, text_y);
		text_x += 55;
		text = Integer.toString((int)this.getMaxCooldown());                                    // TODO: Cooldown
		g.setColor(VALUE);
		g.drawString(text, text_x, text_y);

		// Display the player's inventory
		g.setColor(LABEL);
		g.drawString("Items:", 420, text_y);
		bar_x = 490;
		bar_y = RPG.screenheight - RPG.panelheight + 15;
		bar_width = 288;
		bar_height = bar_height + 20;
		g.setColor(BAR_BG);
		g.fillRect(bar_x, bar_y, bar_width, bar_height);

		inv_x = 490+30;
		inv_y = RPG.screenheight - RPG.panelheight
				+ ((RPG.panelheight - 72) / 2)+43;
		for(Enumeration<Entity> e = item_bag.elements(); e.hasMoreElements();){
			Item entity = (Item)e.nextElement();
			{
				entity.itemBagRender(g, inv_x, inv_y);
				inv_x += 72;       			
			}

		}
		// Render the item to (inv_x, inv_y)

	}

	private float getMaxCooldown() {

		return max_cooldown;
	}
	
	public void setHPMax()
	{
		this.hp = this.max_hp;
	}
	
	public boolean checkVictory()
	{
		return winner;
	}
}



