
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Bat extends Passive{

	public static final String IMAGE = "assets/units/dreadbat.png";
	public static final String NAME = "Giant Bat";
	public static final int HEALTH = 40;
	public static final int DAMAGE = 0;
	public static final int COOLDOWN = 0;
	public static final boolean BLEEDS = true;


	public Bat(double start_x, double start_y)
			throws SlickException
	{
		img = new Image(IMAGE);
		img_flipped = img.getFlippedCopy(true, false);
		this.x = start_x;
		this.y = start_y;
		this.max_damage = DAMAGE;
		this.max_hp = HEALTH;
		this.hp = HEALTH;
		this.name = NAME;
		this.max_speed = SPEED;
		this.max_cooldown = COOLDOWN;
		this.bleeds = BLEEDS;
	}

}
