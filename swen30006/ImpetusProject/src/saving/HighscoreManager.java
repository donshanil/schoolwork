package saving;

import org.newdawn.slick.SavedState;

public interface HighscoreManager {
	public void loadHighscores(SavedState savedGameData);

	public void saveHighscores(SavedState savedGameData);
	
	public void observe(int levelNo, int score);
	/* pass the level no and score, manager will decide if it goes in
	 * don't forget that the index starts from 0, not 1*/
	
	public int[] getHighscoreList();
	/*get list of top scores (index is level no)*/
	
	public String toString();
}
