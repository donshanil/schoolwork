package states;

import menu.MenuButton;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import saving.GameData;

public class LevelSelectState extends GameStateBase<GameData,States> {
	int stateID = 2;
	Image background;
	MenuButton btnMainMenu;
	MenuButton[] btnLevelList;
	
	public LevelSelectState(ClientBase<GameData> theClient, States theState) {
        super(theClient, theState);
    }

	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException {
		background = null;
		
		btnMainMenu = new MenuButton("Main Menu", 100, 300);
		
		//test
		btnLevelList = new MenuButton[6];
		btnLevelList[0] = new MenuButton("Level 1", 5, 50);
		btnLevelList[1] = new MenuButton("Level 2", 5, 75);
		btnLevelList[2] = new MenuButton("Level 3", 5, 100);
		btnLevelList[3] = new MenuButton("Level 4", 5, 125);
		btnLevelList[4] = new MenuButton("Level 5", 5, 150);
		btnLevelList[5] = new MenuButton("Level 6", 5, 175);

	}

	@Override
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException {
		Input input = gc.getInput();
		
		GameData theGameData = getClient().getGameData();
		
		btnMainMenu.update(input);
		
		if(btnMainMenu.buttonClicked())
			game.enterState(States.MAINMENUSTATE.getValue());
		
		
		for(int i = 0; i < btnLevelList.length; i++)
		{
			btnLevelList[i].update(input);
			if(btnLevelList[i].buttonClicked())
			{	
				theGameData.setCurrLevel(i);
				game.enterState(States.GAMEPLAYSTATE.getValue());
			}
		}
	}

	@Override
	public void render(GameContainer gc, StateBasedGame game, Graphics g)
			throws SlickException {
		
		btnMainMenu.render(g);
		//test
		for(int i=0; i<btnLevelList.length; i++){
			btnLevelList[i].render(g);
		}

	}

	@Override
	public int getID() {
		return this.stateID;
	}
	

}
