package saving;

import org.newdawn.slick.SavedState;

import achievements.*;

import states.GameplayState;


public class GameAchievements implements AchievementManager, GameplayStateObserver {
	private Achievement[] cheevoList = null;
	
	public GameAchievements(){
		//TODO do stuff here!!! load stuff up!!
		cheevoList = new Achievement[3];
		cheevoList[0] = new TestAchievement();
		cheevoList[1] = new AchGoFastY();
		cheevoList[2] = new AchGoFastX();
	}

	@Override
	public Achievement[] getAchievementList() {
		return cheevoList;
	}

	@Override
	public void observe(GameplayState gameState) {
		for (Achievement cheevo : cheevoList){
			cheevo.observe(gameState);
		}
	}

	public void saveAchievements(SavedState savedGameData){
		for (Achievement cheevo: cheevoList){
			cheevo.save(savedGameData);
		}
	}

	public void loadAchievements(SavedState savedGameData) {
		for (Achievement cheevo: cheevoList){
			cheevo.load(savedGameData);
		}
	}
	
	public String toString(){
		String out_string = "";
		for (Achievement cheevo: cheevoList){
			out_string += cheevo.toString();
		}
		return out_string;
	}
	
}
