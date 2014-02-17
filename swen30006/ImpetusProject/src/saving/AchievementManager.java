package saving;

import org.newdawn.slick.SavedState;

import achievements.Achievement;


public interface AchievementManager{
	public void saveAchievements(SavedState savedGameData);
	public void loadAchievements(SavedState savedGameData);
	public Achievement[] getAchievementList();
	public String toString();
}
