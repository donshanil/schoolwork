package achievements;

import org.newdawn.slick.SavedState;

import entity.Player;
import states.GameplayState;

public class TestAchievement extends Achievement {
	//example achievement, player travels 200000000 horizontally
	private double xTraveled;
	private double xCurrent;
	private int level;
	private static double xGoal = 200000000;
	private static String savedDone = "achTestDone";
	private static String savedX = "achTestX";
	
	public TestAchievement(){
		xTraveled = 0;
		xCurrent = -1;
	}
	
	@Override
	public void observe(GameplayState gameState) {
		if(done)
			return;
		
		int levelBeforeExit = gameState.getWorld().getLevelBeforeExit();
		if(level != levelBeforeExit){
			level = levelBeforeExit;
			xCurrent = -1;
		}
		
		Player player = gameState.getWorld().getPlayer();
		double playerX = player.getX();
		if (xCurrent == -1){
			xCurrent = playerX;
		} else {
			xTraveled += Math.abs(playerX - xCurrent);
			xCurrent = playerX;
		}
		
		if (xTraveled >= xGoal)
			done = true;
	}
	
	@Override
	public void load(SavedState savedGameData){
		try{
			done = Boolean.parseBoolean(savedGameData.getString(savedDone, "false"));
			xTraveled = Double.parseDouble(savedGameData.getString(savedX, "0"));
		} catch(IllegalArgumentException e){
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void save(SavedState savedGameData){
		savedGameData.setString(savedDone, Boolean.toString(done));
		savedGameData.setString(savedX, Double.toString(xTraveled));
	}
	
	public String toString(){
		String doneString = "";
		if(done)
			doneString = "done";
		else
			doneString = String.valueOf((int)xTraveled)+"/"+String.valueOf((int)xGoal);
		return "Traverse the horizontal dimension: " + doneString + "\n";
	}
}
