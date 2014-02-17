package states;

import menu.MenuButton;
import music.MusicManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.state.transition.FadeOutTransition;

import saving.GameData;

public class MainMenuState extends GameStateBase<GameData,States> {
	private int stateID = 0;
	private Image background = null;
	private MusicManager bgm = null;
	
	MenuButton btnPlayGame = null;
	private MenuButton btnLevelSelect = null;
	private MenuButton btnResume = null;
	private MenuButton btnHighscore = null;
	
	protected GameData theGameData;
	
	public static final String MAIN_BG_PATH = "assets/menus/main_bg.jpg";
	//TODO: ^^^ probably should put this somewhere else
	
	public MainMenuState(ClientBase<GameData> theClient, States theState) {
        super(theClient, theState);
        theGameData = getClient().getGameData();
    }

	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException {
		background = new Image(MAIN_BG_PATH);
		bgm = MusicManager.getInstance();
		
		//test button
		btnPlayGame = new MenuButton("Start Game", ImpetusGame.screenwidth/2-48, 200);
		btnResume = new MenuButton("Resume Game", ImpetusGame.screenwidth/2-50, 250);
		btnLevelSelect = new MenuButton ("Select a level", ImpetusGame.screenwidth/2-66, 300);
		btnHighscore = new MenuButton ("cheevos", ImpetusGame.screenwidth/2-37, 350);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException {
		Input input = gc.getInput();
		
		//TODO maybe make this a function instead?
		btnLevelSelect.update(input);
		if (btnPlayGame.buttonClicked()){
			theGameData.setCurrLevel(0);
			game.enterState(States.GAMEPLAYSTATE.getValue());
		}
		
		btnPlayGame.update(input);
		if (btnLevelSelect.buttonClicked()){
			game.enterState(States.LEVELSELECTSTATE.getValue());
		}
		
		btnResume.update(input);
		if(btnResume.buttonClicked()){
			game.enterState(States.GAMEPLAYSTATE.getValue());
		}
		
		btnHighscore.update(input);
		if(btnHighscore.buttonClicked()){
			game.enterState(States.HIGHSCORESTATE.getValue());
		}
		
		
		
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g)
			throws SlickException {
		background.draw();
		btnLevelSelect.render(g);
		btnResume.render(g);
		btnHighscore.render(g);
		
		//test button
		btnPlayGame.render(g);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException{
		//i changed this back cos it's easier to hear when it starts
		//(also it's the better song obv)
		bgm.playBGM(0); //TODO change index to something static
	}
	
	@Override
	public int getID() {
		return this.stateID;
	}
}
