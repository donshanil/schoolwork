import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;


public abstract class Unit extends Entity{
	protected int hp;
	protected int max_hp;
	protected int max_damage;
	protected float cooldown;
	protected float max_cooldown;
	protected double max_speed;
	protected String name;
	protected boolean alive = true;
	protected boolean face_left = false;
	protected boolean bleeds = true;
	
    Color VALUE = new Color(1.0f, 1.0f, 1.0f);          // White
    Color BAR_BG = new Color(0.0f, 0.0f, 0.0f, 0.8f);   // Black, transp
    Color BAR = new Color(0.8f, 0.0f, 0.0f, 0.8f);      // Red, transp

	
	public int getHP()
	{
		return hp;
	}
	public int getMaxHP()
	{
		return max_hp;
	}
	public int getMaxDamage()
	{
		return max_damage;
	}
	public float getCooldown()
	{
		return cooldown;
	}
	public boolean getBleed()
	{
		return bleeds;
	}
	public void death()
	{
		this.alive = false;
	}
    public void damage(int max_damage){
    	//takes max damage and gets a random number between. will assume pre-seeded
    	int amount = this.getRandom(1, max_damage);
    	this.hp = this.hp - amount;
    }
    public void interact(Player player)
    {
    	
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
	public void render(Graphics g, int cam_x, int cam_y)
	{
		Image which_img;
		int bar_height=12;
		int bar_width;
		int font_width = 9;
		//only works with monospace fonts!
		if (this.name.length()*font_width > 70) {
			bar_width = this.name.length()*font_width;
		}
		else {
			bar_width = 70;
		}
	
		int hp_bar_vertical_offset = 50;
		float health_percent;
		// Calculate the player's on-screen location from the camera
		int screen_x, screen_y;
		screen_x = (int) (this.x - cam_x);
		screen_y = (int) (this.y - cam_y);
		which_img = this.face_left ? this.img_flipped : this.img;
		which_img.drawCentered(screen_x, screen_y);
		/** render the health bar*/
		g.setColor(BAR_BG);
		g.fillRect((float)screen_x-bar_width/2, (float)screen_y-hp_bar_vertical_offset, bar_width, bar_height);
		health_percent = ((float)this.getHP()/(float)this.getMaxHP());
		g.setColor(BAR);
		g.fillRect((float)screen_x-bar_width/2, (float)screen_y-hp_bar_vertical_offset, health_percent*bar_width, bar_height);
		g.setColor(VALUE);
		g.drawString(this.name, (float)screen_x-bar_width/2, (float) screen_y-hp_bar_vertical_offset-3);
	}
}
