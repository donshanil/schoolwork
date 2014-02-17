package saving;

import java.io.IOException;

import org.newdawn.slick.SavedState;
//TODO: add some kind of xml saving library and save everything in GameData
/*
 * Allows us to pass information between game states, such as achievements and level progression.
 * Enables us to save the game. 
 */
import org.newdawn.slick.SlickException;


import states.GameplayState;

public class GameData implements GameplayStateObserver{
	private SavedState savedGameData;
	private GameAchievements cheevoManager;
	private GameHighscores scoreManager;
	
	private static int currLevel;
	
	public GameData() {
		currLevel = 0;
		
		cheevoManager = new GameAchievements();
		scoreManager = new GameHighscores();
		
		//open the file
		try {
			savedGameData = new SavedState("save_game.save");
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		//load if exists
		try{
			loadGame();
		} catch (IOException e){
			saveGame();
		}
	}
	
	public int getCurrLevel()
	{
		return currLevel;
	}
	
	public void setCurrLevel(int level)
	{
		currLevel = level;
	}
	
	public void saveGame() {
		savedGameData.setNumber("currLevel", currLevel);
		cheevoManager.saveAchievements(savedGameData);
		scoreManager.saveHighscores(savedGameData);
		
		try {
			savedGameData.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadGame() throws IOException {
		savedGameData.load();
		cheevoManager.loadAchievements(savedGameData);
		scoreManager.loadHighscores(savedGameData);
		currLevel = (int) savedGameData.getNumber("currLevel");
	}

	@Override
	public void observe(GameplayState gameState) {
		cheevoManager.observe(gameState);
	}
	
	public void notifyHighscoreManager(int levelNo, int score){
		/* use this method to notify highscore */
		scoreManager.observe(levelNo, score);
	}
	
	public String getAchievementString(){
		/* use this to get the string output of achievements
		 * each achievement's toString is separated by \n */
		return cheevoManager.toString();
	}	
	
	public String getHighscoreString(){
		/* use this to get the string output of highscores
		 * empty highscores (0) are not shown because
		 * either the level doesn't exist or it's meaningless */
		return scoreManager.toString();
	}
	
	protected GameAchievements getAchievementManager(){
		return cheevoManager;
	}
	
	protected HighscoreManager getHighscoreManager(){
		return scoreManager;
	}
}
