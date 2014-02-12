
public abstract class NPC extends Unit{
	//all enemies move the same speed
	public static final double SPEED = 0.250;
	
	
	public void damage(int x)
	{
		//overrides the unit damage so the target is in effect invincible
	}
	
	public void interact(Player player)
	{
		
	}
	
	
	
}
