import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Draelic extends Enemy{

	public static final String IMAGE = "assets/units/necromancer.png";
	public static final String NAME = "Drealic";
	public static final int HEALTH = 140;
	public static final int DAMAGE = 30;
	public static final int COOLDOWN = 400;
	public static final boolean BLEEDS = true;
	public static final String BLUNT_HIT = "assets/blunthit.wav";


	public Draelic(double start_x, double start_y)
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
