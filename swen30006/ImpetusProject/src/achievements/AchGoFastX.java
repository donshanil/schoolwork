package achievements;

import org.newdawn.slick.SavedState;

import states.GameplayState;
import entity.Player;

public class AchGoFastX extends Achievement {
	private double xRecord;
	private static double xVelocityGoal = 2500;
	private static String savedRecord = "achGoFastXRecord";
	private static String savedDone = "achGoFastXDone";
	
	public AchGoFastX(){
		done = false;
		xRecord = 0;
	}
	
	@Override
	public void observe(GameplayState gameState) {
		if(done)
			return;
		
		Player player = gameState.getWorld().getPlayer();
		double xVel = player.getX_velocity();
		
		if (xVel > xRecord)
			xRecord = xVel;
		
		if(xVel > xVelocityGoal){
			done = true;
		}
	}
	
	@Override
	public void load(SavedState savedGameData){
		try{
			this.xRecord = Double.parseDouble(savedGameData.getString(savedRecord, "0")); 
			this.done = Boolean.parseBoolean(savedGameData.getString(savedDone, "false"));
		} catch(IllegalArgumentException e){
			e.printStackTrace();
			
		}
		
	}
	
	@Override
	public void save(SavedState savedGameData){
		savedGameData.setString(savedDone, Boolean.toString(done));
		savedGameData.setString(savedRecord, Double.toString(xRecord));
	}
	
	public String toString(){
		String doneString = "run faster";
		if(done)
			doneString = "done";
		else
			doneString += " (record: " +Double.toString(xRecord)+ ")";
		return "Gotta Run Fast: " + doneString + "\n";
	}
}
