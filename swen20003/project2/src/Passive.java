import java.util.Vector;


public abstract class Passive extends Unit{
	
	public static final double SPEED = 0.20;
	protected int hit_timer = 0;
	public final int MAX_HIT_TIMER = 5000;
	protected int max_change = 3000;
	public int change = max_change;
	public int dir_x = 0, dir_y=0;
	public double new_x, new_y;

	//passive units move randomly until hit.
	
	public void move(World world, double player_x, double player_y, int delta, Player player, Vector<Entity> blood)
	{
		if (hit_timer > 0){
			hit_timer -= delta;
			
		}	
		if (change <= max_change)
		{
			change += delta;
		}
		if(this.hp <=0)
		{
			death();
		}
		
		double player_distance;
	
		double move_x = 0, move_y = 0, x_dist, y_dist;
		x_dist = (this.x-player_x);
		y_dist = (this.y-player_y);
		player_distance = getDistance(player_x, player_y);
		
		//running away code - basically a reverse of the enemy code.
		if (hit_timer > 0)
		{	
			x_dist = (this.x-player_x);
			y_dist = (this.y-player_y);
			
			move_x = (x_dist/player_distance)*delta*this.max_speed;
			move_y = (y_dist/player_distance)*delta*this.max_speed;
			
			new_x = this.x + move_x;
			new_y = this.y + move_y;
			

		}
		//ie revert to passive mode
		else if (change >= max_change)
		{
			dir_x = getRandom(-1, 1);
			dir_y = getRandom(-1,1);
			change = 0;
			
		}
		else if(change<= max_change)
		{	
			//check for diagonal movement
			if(dir_x * dir_y ==1 || dir_x* dir_y ==-1)
			{
				new_x = this.x + Math.sqrt(0.5)*delta*this.max_speed*dir_x*0.5;
				new_y = this.y + Math.sqrt(0.5)*delta*this.max_speed*dir_y*0.5;

			}
			//otherwise move normally
			else
			{
				new_x = this.x + dir_x*delta*max_speed*0.5;
				new_y = this.y + dir_y*delta*max_speed*0.5;
			}

			
		}
		//finally, commit to the movement
		if(world.terrainBlocks(new_x, new_y))
		{
			if (!world.terrainBlocks(this.x, new_y))
				new_x = this.x;
			else if (!world.terrainBlocks(new_x, this.y))
				new_y = this.y;
			else	
			{
					//DONT MOVE
				new_x = this.x;
				new_y = this.y;
			}
		}	
		this.x = new_x;
		this.y = new_y;
		
	}

	
	//add override behaviour - i.e. run away
    public void damage(int max_damage){
    	//takes max damage and gets a random number between. will assume pre-seeded
    	int amount = this.getRandom(1, max_damage);
    	this.hp = this.hp - amount;
    	//if they take damage, set PANIC timer
    	this.hit_timer = this.MAX_HIT_TIMER;
    }
    
    public void death()
    {
    	this.alive = false;
    }
}	

