import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public abstract class Item extends Entity{
	protected int hp_bonus = 0;
	protected int cooldown_bonus = 0;
	protected int damage_bonus = 0;
	protected boolean pickup = false;
	protected Sound pickup_noise;
	String name;
	
	public void interact(Player player) throws SlickException
	{
		//do all these things to the player
		player.buffDamage(damage_bonus);
		player.buffCooldown(cooldown_bonus);
		player.buffHP(hp_bonus);
		this.pickup = true;
		pickup_noise.play(1, (float) 0.4);
		
	}
	public void itemBagRender(Graphics g, double x, double y){
		this.x = x;
		this.y = y;
		this.img.drawCentered((float)x, (float)y);
	}
}
