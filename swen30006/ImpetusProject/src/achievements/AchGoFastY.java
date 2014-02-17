package achievements;

import org.newdawn.slick.SavedState;

import states.GameplayState;
import entity.Player;

public class AchGoFastY extends Achievement {
	private double yRecord;
	private static double yVelocityGoal = -9400;
	private static String savedRecord = "achGoFastYRecord";
	private static String savedDone = "achGoFastYDone";
	
	public AchGoFastY(){
		done = false;
		yRecord = 0;
	}
	
	@Override
	public void observe(GameplayState gameState) {
		if(done)
			return;
		
		Player player = gameState.getWorld().getPlayer();
		double yVel = player.getY_velocity();
		
		if (yVel < yRecord)
			yRecord = yVel;
		
		if(yVel < yVelocityGoal){
			done = true;
		}
	}
	
	@Override
	public void load(SavedState savedGameData){
		try{
			this.yRecord = Double.parseDouble(savedGameData.getString(savedRecord, "0")); 
			this.done = Boolean.parseBoolean(savedGameData.getString(savedDone, "false"));
		} catch(IllegalArgumentException e){
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void save(SavedState savedGameData){
		savedGameData.setString(savedDone, Boolean.toString(done));
		savedGameData.setString(savedRecord, Double.toString(yRecord));
	}
	
	public String toString(){
		String doneString = "jump faster";
		if(done)
			doneString = "done";
		else
			doneString += " (record: " +Double.toString(0-yRecord)+ ")";
		return "Gotta Jump Fast: " + doneString + "\n";
	}
}
