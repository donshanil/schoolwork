import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class Bloodstain extends Entity{

	public static final String IMAGE = "assets/Bloodstain.png";
	
	/*adds a decal to the world. if i wanted to extend this further, i'd make a decal abstract class
	 * and extend bloodstain from that instead.
	 */
	public Bloodstain(double new_x, double new_y)
		throws SlickException
	{
		img = new Image(IMAGE);
		//picks a random orientation for the bloodstain
		int rotation =(int)(Math.random() * (360));
		img.setRotation(rotation);
		img.setAlpha((float) 0.85);
		this.x = new_x;
		this.y = new_y;
	}
	
}
