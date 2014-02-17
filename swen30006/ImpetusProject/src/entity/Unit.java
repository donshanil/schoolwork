package entity;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public abstract class Unit extends Entity{
	protected boolean face_left = false;
	protected double mass;
	
	public void render(Graphics g, double x, double y)
	{
		Image entity_img;
		entity_img = this.face_left ? this.img_flipped : this.img; //CHANGE THIS LATER
		int screen_x, screen_y;
		screen_x = (int) this.x;
		screen_y = (int) this.y;
		entity_img.drawCentered(screen_x, screen_y);
		//fill this shit out yo	
		
	
	}
}
