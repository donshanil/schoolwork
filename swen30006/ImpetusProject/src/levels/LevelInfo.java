package levels;

public class LevelInfo {
	public static final String LEVELDIR = "assets/levels/";
	private String levelPath = LEVELDIR + "level1";
	private int start_x = 0;
	private int start_y = 0;
	private double gravity = 0.0007;
	
	
	public LevelInfo(String levelPath, int start_x, int start_y, double gravity)
	{
		this.levelPath = LEVELDIR + levelPath;
		this.start_x = start_x;
		this.start_y = start_y;
		this.gravity = gravity;
	}
	
	public String getLevelPath() {
		return levelPath;
	}


	public void setLevelPath(String levelPath) {
		this.levelPath = LEVELDIR + levelPath;
	}


	public int getStart_x() {
		return start_x;
	}


	public void setStart_x(int start_x) {
		this.start_x = start_x;
	}


	public int getStart_y() {
		return start_y;
	}


	public void setStart_y(int start_y) {
		this.start_y = start_y;
	}


	public double getGravity() {
		return gravity;
	}


	public void setGravity(double gravity) {
		this.gravity = gravity;
	}

	

	
}
