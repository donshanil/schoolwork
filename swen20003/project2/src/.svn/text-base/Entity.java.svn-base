import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public abstract class Entity {

    protected Image img = null;
    protected Image img_flipped = null;
    protected double x;
    protected double y;
    
    /** x coordinate of entity
     * 
     */
    public double getX()
    {
    	return x;
    }
    
    public double getY()
    {
    	return y;
    }
	public double getDistance(double x, double y)
	{
		//gets the distance from given x, y coordinate
		double distance;
		distance = Math.sqrt(Math.pow(this.x-x,2) + Math.pow(this.y-y,2));
		return distance;
	}		
    /**don't need to create a new entity */
	public void render(Graphics g, int cam_x, int cam_y)
	{
		int screen_x, screen_y;
		screen_x = (int) (this.x - cam_x);
		screen_y = (int) (this.y - cam_y);
		this.img.drawCentered(screen_x, screen_y);
	}
	public void move()
	{
		//placeholder so my code doesn't break.
	}
	
    public int getRandom(int min, int max)
    {
    	//returns random integer between range
    	return min+(int)(Math.random()*((max-min) + 1)); 
    }

    
}
