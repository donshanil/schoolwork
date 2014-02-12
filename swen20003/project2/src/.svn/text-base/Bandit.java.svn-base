import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Bandit extends Enemy{

	public static final String IMAGE = "assets/units/bandit.png";
	public static final String NAME = "Bandit";
	public static final int HEALTH = 40;
	public static final int DAMAGE = 8;
	public static final int COOLDOWN = 200;
	public static final boolean BLEEDS = true;
	public static final String BLUNT_HIT = "assets/blunthit.wav";


	public Bandit(double start_x, double start_y)
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
