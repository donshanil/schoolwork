package saving;

import org.newdawn.slick.SavedState;

public class GameHighscores implements HighscoreManager {
	private int[] highscores;
	
	public GameHighscores(){
		highscores = new int[15];	//TODO put some kind of hard global limit
	}
	
	public void loadHighscores(SavedState savedGameData){
		for (int level=0; level<highscores.length; level++){
			highscores[level] =
					(int)savedGameData.getNumber("scoreLevel"+String.valueOf(level), 0);
		}
	}

	public void saveHighscores(SavedState savedGameData) {
		int level = 0;
		for (int highscore: highscores){
			savedGameData.setNumber("scoreLevel"+String.valueOf(level), highscore);
			level++;
		}
	}
	
	public void observe(int levelNo, int score){
		if(highscores[levelNo] < score)
			highscores[levelNo] = score;
	}

	@Override
	public int[] getHighscoreList() {
		return highscores;
	}
	
	@Override
	public String toString(){
		/* returns a string of scores as a list
		 * 
		 * example:
		 * HIGHSCORES\n
		 * Level 1: 100\n
		 * Level 2: 80\n
		 * 
		 * something like that
		 * if score is 0, who cares
		 * (also the level may not exist anyway?)
		 */
		String output_string = "***HIGHSCORES***\n";
		int level = 0;
		boolean noScore = true;
		for (int highscore: highscores){
			level++;	//the levels are shown from 1 in the ui
			if(highscore==0)
				continue;
			output_string += "Level " +String.valueOf(level)+ ": " +String.valueOf(highscore);
			noScore = false;
		}
		if (noScore)
			output_string += "you have no scores yet :(";
		
		return output_string;
	}
}
