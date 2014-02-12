import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;


public class Elvira extends NPC{
	
	public static final String IMAGE = "assets/units/shaman.png";
	public static final String NAME = "Elvira";
	public static final int HP = 1;
	Sound recover = new Sound(RECOVER);
	public static final String RECOVER = "assets/recovery.ogg";

	public Elvira(double x, double y) throws SlickException{
		this.x = x;
		this.y = y;
		this.hp = HP;
		this.max_hp = HP;
		this.img = new Image(IMAGE);
		this.name = NAME;
		
	}
	public void interact (Player player)
	{
		if (player.getHP() < player.getMaxHP())
		{
			player.setHPMax();
			recover.play(1, (float) 0.4);
		}
	}
}
