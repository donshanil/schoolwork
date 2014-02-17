package entity;

import org.newdawn.slick.Image;

public abstract class Entity {
	protected double x;
	protected double y;
	protected Image img = null;
	protected Image img_flipped = null;
	


	public double getX()
	{
		return x;
	}
	public double getY()
	{
		return y;
	}
}
