import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class Garth extends NPC{
	
	public static final String IMAGE = "assets/units/peasant.png";
	public static final String NAME = "Garth";
	public static final int HP = 1;

	public Garth(double x, double y) throws SlickException{
		this.x = x;
		this.y = y;
		this.hp = HP;
		this.max_hp = HP;
		this.img = new Image(IMAGE);
		this.name = NAME;
		
	}
	public void interact (Player player)
	{
		
	}
}
