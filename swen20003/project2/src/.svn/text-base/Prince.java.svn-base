import java.util.Enumeration;
import java.util.Vector;

import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;


public class Prince extends NPC{
	
	public static final String IMAGE = "assets/units/prince.png";
	public static final String NAME = "Prince Aldric";
	public static final int HP = 1;
	protected boolean elixir_taken = false;
	Music win = new Music("assets/win.ogg");

	public Prince(double x, double y) throws SlickException{
		this.x = x;
		this.y = y;
		this.hp = HP;
		this.max_hp = HP;
		this.img = new Image(IMAGE);
		this.name = NAME;
		
	}
	public void interact (Player player, Vector<Entity>item_bag)
	{
		//check player's item bag for the elixir.
		for(Enumeration<Entity> e = item_bag.elements(); e.hasMoreElements();){
			Item item = (Item)e.nextElement();
			{
				if (item.name == "Elixir of Life")
				{
					elixir_taken = true;
					item_bag.remove(item);
					win.loop();
					
				}
			}

		}
	}
	public boolean checkWin()
	{
		return elixir_taken;
	}
}
