package states;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;

import saving.GameData;

public class ImpetusGame extends ClientBase<GameData> {
    /*
	public static final int MAINMENUSTATE		= 0;
    public static final int HIGHSCORESTATE		= 1;
    public static final int LEVELSELECTSTATE	= 2;
    public static final int GAMEPLAYSTATE		= 3;
	*/
    /** Screen width, in pixels. */
    public static final int screenwidth = 960;
    /** Screen height, in pixels. */
    public static final int screenheight = 540;
    
    public ImpetusGame(String name) {
        super(name, new GameData());
        /*
        this.addState(new MainMenuState(MAINMENUSTATE));
        this.addState(new HighscoreState(HIGHSCORESTATE));
        this.addState(new LevelSelectState(LEVELSELECTSTATE));
        this.addState(new GameplayState(GAMEPLAYSTATE));
        this.enterState(MAINMENUSTATE);
        */
        addState(new MainMenuState(this, States.MAINMENUSTATE));
        addState(new HighscoreState(this, States.HIGHSCORESTATE));
        addState(new LevelSelectState(this, States.LEVELSELECTSTATE));
        addState(new GameplayState(this, States.GAMEPLAYSTATE));
        enterState(States.MAINMENUSTATE.getValue());
    }
  
    public static void main(String[] args) throws SlickException {
         AppGameContainer app = new AppGameContainer(new ImpetusGame("IMPETUS PROJECT"));
         
         app.setDisplayMode(screenwidth, screenheight, false);
         app.setTargetFrameRate(240); //fps cap
         app.setMaximumLogicUpdateInterval(31);  // minimum fps
         app.setShowFPS(true);
         app.start();
    }
  
    @Override
    public void initStatesList(GameContainer gameContainer) throws SlickException {
        
    	getState(States.MAINMENUSTATE.getValue()).init(gameContainer, this);
        getState(States.HIGHSCORESTATE.getValue()).init(gameContainer, this);
        getState(States.LEVELSELECTSTATE.getValue()).init(gameContainer, this);
        getState(States.GAMEPLAYSTATE.getValue()).init(gameContainer, this);
        
    	

    }
    
    @Override
    public boolean closeRequested()
    {
    	this.getGameData().saveGame();
    	System.exit(0);
    	return false;
    }


    
    
}