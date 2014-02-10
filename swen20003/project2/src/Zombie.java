
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Zombie extends Enemy{

	public static final String IMAGE = "assets/units/zombie.png";
	public static final String NAME = "Zombie";
	public static final int HEALTH = 60;
	public static final int DAMAGE = 10;
	public static final int COOLDOWN = 800;
	public static final boolean BLEEDS = true;
	public static final String BLUNT_HIT = "assets/blunthit.wav";


	public Zombie(double start_x, double start_y)
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
		this.enemy_hit = new Sound(BLUNT_HIT);
	}

}
