import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Tome extends Item{
	
	public static final int COOLDOWN_BONUS = 200;
	public static final String IMAGE = "assets/items/book.png";
	public static final String NAME = "Tome of Agility";
	public static final String PICKUP = "assets/holdable.wav";
	
	public Tome(double start_x, double start_y) throws SlickException
	{
		this.cooldown_bonus = COOLDOWN_BONUS;
		this.img = new Image(IMAGE);
		this.name = NAME;
		this.x = start_x;
		this.y = start_y;
		this.pickup_noise = new Sound(PICKUP);
	}

}
