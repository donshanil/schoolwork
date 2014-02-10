import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Sword extends Item{
	
	public static final int DAMAGE_BONUS = 10;
	public static final String IMAGE = "assets/items/sword.png";
	public static final String NAME = "Sword of Strength";
	public static final String PICKUP = "assets/ar3_pkup.wav";
	
	public Sword(double start_x, double start_y) throws SlickException
	{
		this.damage_bonus = DAMAGE_BONUS;
		this.img = new Image(IMAGE);
		this.name = NAME;
		this.x = start_x;
		this.y = start_y;
		this.pickup_noise = new Sound(PICKUP);
	}
}