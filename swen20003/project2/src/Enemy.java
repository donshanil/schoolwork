import java.util.Vector;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public abstract class Enemy extends Unit{
	//all enemies move the same speed
	public static final double SPEED = 0.250;
	public String BLUNT_HIT = "assets/blunt_hit.wav";
	protected Sound enemy_hit;

	
	public void move(World world, double player_x, double player_y, double delta, Player player, Vector<Entity> blood) throws SlickException
	{
    	if(this.hp <=0)
    		{
    		death();
    		}
		//update cooldown timer
		if (this.cooldown > 0) 
    		this.cooldown = this.cooldown - (float)delta;
    	
		double player_distance;
		double x_dist, y_dist, move_x, move_y, new_x, new_y;
		x_dist = (this.x-player_x);
		y_dist = (this.y-player_y);
		player_distance = getDistance(player_x, player_y);
		
		if (player_distance < 150){
			//player is close, move towards player
			
			move_x = (x_dist/player_distance)*delta*this.max_speed;
			move_y = (y_dist/player_distance)*delta*this.max_speed;
			
			new_x = this.x - move_x;
			new_y = this.y - move_y;
			
			//check blocking
			if(world.terrainBlocks(new_x, new_y))
			{
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
			//player is close enough to attack! sick em rex
			if(player_distance<=50){
				new_x = this.x;
				new_y = this.y;
				//ATTACK
				if(cooldown <= 0){
					
					if(player.bleeds == true)
					
						blood.add(new Bloodstain(player.getX(), player.getY()));
					player.damage(this.max_damage);
					this.cooldown = max_cooldown;
					enemy_hit.play(1, (float)0.4);
				}
			}
			
			//commit movement
			this.x= new_x;
			this.y = new_y;
			
			
		}
		
	}
	public boolean isXBlocked(double new_x, double new_y)
	{
		if(new_x<=this.x+10 && new_x>=this.x-10)
			{
				return true;
			}
		return false;
	}
	public boolean isYBlocked(double new_x, double new_y)
	{
		if(new_y<=this.y+10 && new_y>=this.y-10)
			{
				return true;
			}
		return false;
	}
	

	
}
