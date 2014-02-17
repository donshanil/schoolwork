package entity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

import states.ImpetusGame;

import levels.baseWorld;

public class Player extends Unit {

	protected double scaled_x;
	protected double scaled_y;
	protected double x_force;
	protected double x_opposing_force;
	protected double net_x_force;
	protected double x_velocity;
	protected double y_velocity;
	protected double x_accel;
	protected double y_accel;
	protected boolean airborne;
	protected int model_height;
	protected int model_width;
	public boolean winner = false;
	public static final double START_MASS = 70; //in kilograms
	public static final double MAX_FORCE = 1150; //in newtons
	//public static final double MAX_ACCEL = 0.3;
	public static final double JUMP_SPEED = 4500;
	public static final double UP_SPEED = 95;
	public static final double DOWN_SPEED = 95;
	//public static final double MAX_VELOCITY = 4;
	public static final double FRICTION_SCALE_FACTOR = 0.4;
	public static final double AIR_CONTROL_FACTOR = 0.5; //value from 0 to 1, determines air control.
	
	public Player(double x_in, double y_in, double SCALE_FACTOR)
	throws SlickException
	{
		//creates a player object at this location.
		this.x = x_in/SCALE_FACTOR;
		this.y = y_in/SCALE_FACTOR;
		this.mass = START_MASS;
		img = new Image("assets/sprites/player.png");
		img_flipped = img.getFlippedCopy(true, false);
		model_height = img.getHeight();
		model_width = img.getWidth();
	}
	
	public void move(baseWorld world, double dir_x, double jump, int delta)
	{
		

		double player_friction = world.getFrictionCoeff(this.scaled_x, this.scaled_y + (model_height/2)+1.5);
		//(x_velocity);
		double new_x, new_y;
		//update facing direction.
		if (dir_x > 0)
			this.face_left = false;
		else if (dir_x < 0)
			this.face_left = true;
		
		if (this.airborne == true)
		{
			jump = 0;
			dir_x = dir_x*AIR_CONTROL_FACTOR;
		}
		
		//calculate acceleration!
		//we'll need to check directly underneath the player to see if we need to account for friction.
		
		x_force = dir_x*MAX_FORCE*player_friction;
		x_opposing_force = player_friction*x_velocity*FRICTION_SCALE_FACTOR; //this should be lateral friction when we get to it
		net_x_force = x_force - x_opposing_force;
		//f=ma broskies~
		
		x_accel = net_x_force/this.mass;
		//x_accel = dir_x*MAX_ACCEL*player_friction*FRICTION_SCALE_FACTOR-x_velocity*player_friction*FRICTION_SCALE_FACTOR; //remember to add friction later
		x_velocity = x_velocity+(x_accel*delta);
		new_x = x+x_velocity*delta;
		scaled_x = new_x * world.getScaleFactor(); //this will be used for collision boxes
		

		//now do gravity. no jumping yet, our character is white
		y_accel = world.getGravity();
		//y_velocity = -jump*JUMP_SPEED;
		y_velocity = y_velocity+(y_accel*delta)-jump*JUMP_SPEED;
		new_y = y+y_velocity*delta;
		scaled_y = new_y * world.getScaleFactor();
		
		//we need to get an array of rectangles which are relevant to check.
		//because our character should be thinner than two tiles, we can use a slightly imprecise method.
		//generate an array of 'relevant rectangles'

		LinkedList<Rectangle> collision_list;
		collision_list = new LinkedList<Rectangle>();
		
		Rectangle player_box;
		player_box = new Rectangle((float) scaled_x-model_width/2, (float) scaled_y-model_height/2, (float) model_width, (float) model_height);
				
		collision_list.add(world.getTileRectangle(player_box.getMinX(), player_box.getMaxY()));
		collision_list.add(world.getTileRectangle(player_box.getMaxX(), player_box.getMaxY()));

		
		//CHECK BOTTOM
		this.airborne = true;
		while(collision_list.peek() != null)
		{
			Rectangle check_box = collision_list.poll();
			if (check_box.intersects(player_box))
			{
				
				if(world.collisionDetect(check_box.getX(), check_box.getY(), "up"))
				{
					y_velocity -= UP_SPEED;
				}
				
				if(world.collisionDetect(check_box.getX(), check_box.getY(), "down"))
				{
					y_velocity += DOWN_SPEED;
				}
				
				//Checks if the player is standing on a solid block
				if(world.isSolid(check_box.getX(), check_box.getY()))
				{
					this.airborne = false;
					new_y = this.y;
					y_velocity = 0;
					
					// Gets the friction coefficient of the current block the player is standing on
					//double frictionCoeff = world.getFrictionCoeff(check_box.getX(), check_box.getY());
					
					//Read a dang highschool physics textbook and make this work
					//x_velocity = x_velocity / frictionCoeff;
				}
				
				//Checks if the player is standing on top of the level end block
				if(world.collisionDetect(check_box.getX(), check_box.getY(), "end"))
				{
					
					try {
						world.nextLevel();
					} catch (SlickException e) {
						e.printStackTrace();
					}
				}
				

			}
		}
		
		//check top
		collision_list.add(world.getTileRectangle(player_box.getMinX(), player_box.getMinY()));
		collision_list.add(world.getTileRectangle(player_box.getMaxX(), player_box.getMinY()));
		while(collision_list.peek() != null)
		{
			Rectangle check_box = collision_list.poll();
			if (check_box.intersects(player_box))
			{
				
				
				if(world.isSolid(check_box.getX(), check_box.getY()))
				{
					new_y = this.y;
					y_velocity = 0;
				}
			}
		}
		
		//now do side to side collision detection.
		
		Rectangle player_box2;
		player_box2 = new Rectangle((float) scaled_x-model_width/2, (float) (y*world.getScaleFactor()-model_height/2), (float) model_width, (float) model_height);
		
		collision_list.add(world.getTileRectangle(player_box2.getMinX(), player_box2.getMinY()));
		collision_list.add(world.getTileRectangle(player_box2.getMinX(), player_box2.getMaxY()));
		collision_list.add(world.getTileRectangle(player_box2.getMinX(), player_box2.getCenterY()));
		collision_list.add(world.getTileRectangle(player_box2.getMaxX(), player_box2.getCenterY()));
		collision_list.add(world.getTileRectangle(player_box2.getMaxX(), player_box2.getMinY()));
		collision_list.add(world.getTileRectangle(player_box2.getMaxX(), player_box2.getMaxY()));
		while(collision_list.peek() != null)
		{
			Rectangle check_box = collision_list.poll();
			if (check_box.intersects(player_box2))
			{
								
				if(world.isSolid(check_box.getX(), check_box.getY()))
				{
					new_x = this.x;
					x_velocity = 0;
				}
			}
		}
		
		
		//commit to movement
		x = new_x;
		y = new_y;
		
	}
	//override
/*	public void render(baseWorld world, Graphics g, double x, double y)
	{
		Image entity_img;
		entity_img = this.face_left ? this.img_flipped : this.img; //CHANGE THIS LATER
		int screen_x, screen_y;
		screen_x = (int) (this.x*world.getScaleFactor());
		screen_y = (int) (this.y*world.getScaleFactor());
		entity_img.drawCentered(screen_x, screen_y);*/
		
/*		Rectangle collision_box;
		collision_box = new Rectangle((float) x-model_width/2, (float) y-model_height/2, (float) model_width, (float) model_height);
		g.fill(collision_box);
		
		double bottom_left_x, bottom_right_x, bottom_y;
		bottom_left_x = this.x-model_width/2;
		bottom_right_x = this.x+model_width/2;
		bottom_y = (float) this.y+model_height/2;
		LinkedList<Rectangle> collision_list;
		collision_list = new LinkedList<Rectangle>();
		collision_list.add(world.getTileRectangle(bottom_left_x, bottom_y));
		collision_list.add(world.getTileRectangle(bottom_right_x, bottom_y));
		
		while(collision_list.peek() != null)
		{
			g.fill(collision_list.poll());
		}*/
		
		//fill this shit out yo	
		
	
	//}
	public double getX_velocity() {
		return x_velocity;
	}

	public double getY_velocity() {
		return y_velocity;
	}
	public double getNetXForce()
	{
		return net_x_force;
	}
	public double getMaxForce()
	{
		//returns max input force
		return MAX_FORCE;
	}

	public double getX_accel() {
		return x_accel;
	}

	public double getY_accel() {
		return y_accel;
	}
	public double getScaledX() {
		return scaled_x;
	}
	public double getScaledY() {
		return scaled_y;
	}
	public void render(Graphics g, int cam_x, int cam_y) throws SlickException
	{
		Image which_img;
		// Calculate the player's on-screen location from the camera
		int screen_x, screen_y;
		screen_x = (int) (this.scaled_x - cam_x);
		screen_y = (int) (this.scaled_y - cam_y);
		which_img = this.face_left ? this.img_flipped : this.img;
		which_img.drawCentered(screen_x, screen_y);
		
		NumberFormat formatter = new DecimalFormat("0");
		
		//draw force meter
		Color BAR_BG = new Color(0.1f, 0.1f, 0.1f, 0.8f);
		Color BAR = new Color(0.8f, 0.0f, 0.0f, 0.8f);
		Color VALUE = new Color(1.0f, 1.0f, 1.0f);  
		int bar_width, bar_height;
		bar_width = 75;
		g.setColor(BAR_BG);
		g.fillRect(screen_x-bar_width/2, screen_y-model_height/2-20, 70, 10); //background
		g.setColor(BAR);
		float fill_width;
		fill_width = (float) ((x_force-x_opposing_force)/MAX_FORCE);
		//if moving right
		if (java.lang.Math.signum(x_velocity) > 0)
		{
			g.fillRect(screen_x, screen_y-model_height/2-20, 35*fill_width, 10);
			g.drawString(formatter.format(x_force), screen_x+20, screen_y);
			g.drawString(formatter.format(x_opposing_force), screen_x-model_width-30, screen_y);
		}
		else if (java.lang.Math.signum(x_velocity) < 0)
		{
			g.fillRect(screen_x-(fill_width), screen_y-model_height/2-20, 35*fill_width, 10);
			g.drawString(formatter.format(-x_opposing_force), screen_x+20, screen_y);
			g.drawString(formatter.format(-x_force), screen_x-model_width-30, screen_y);
		}
		else if (java.lang.Math.signum(x_velocity) == 0)
		{
			//do nothing
			
		}
		g.setColor(VALUE);
		g.drawString(formatter.format(net_x_force), screen_x-model_width/2, screen_y-model_height/2-40);
		if (winner == true)
		{
			Image winner_img = new Image("assets/winner.png");
			winner_img.drawCentered(ImpetusGame.screenwidth/2, ImpetusGame.screenheight/2);
		}
		
	}

}	

