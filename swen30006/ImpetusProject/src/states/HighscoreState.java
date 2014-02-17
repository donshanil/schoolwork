package states;

import menu.MenuButton;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import saving.GameData;


public class HighscoreState extends GameStateBase<GameData,States> {
	int stateID = 1;
	protected GameData theGameData;
	
	private MenuButton btnMainMenu;
	
	private String cheevoString;
	private String scoreString;
	
	public HighscoreState(ClientBase<GameData> theClient, States theState) {
        super(theClient, theState);
        theGameData = getClient().getGameData();
    }


	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException {

		btnMainMenu = new MenuButton("mainmenu", 630, 500);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException {
		Input input = gc.getInput();
		
		btnMainMenu.update(input);
        if(btnMainMenu.buttonClicked())
        	game.enterState(States.MAINMENUSTATE.getValue());
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g)
			throws SlickException {
		g.drawString(cheevoString+"\n"+scoreString, 20, 50);
		
		btnMainMenu.render(g);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException{
		cheevoString = theGameData.getAchievementString();
		scoreString = theGameData.getHighscoreString();
	}

	@Override
	public int getID() {
		return this.stateID;
	}


}
