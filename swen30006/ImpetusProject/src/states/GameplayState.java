package states;

import levels.baseWorld;
import menu.MenuButton;
import music.MusicManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import saving.GameData;
import saving.GameplayStateObserver;

public class GameplayState extends GameStateBase<GameData,States> {
	protected int stateID = 3;
	private MusicManager bgm = null;
	private MenuButton btnMainMenu = null;
	protected GameData theGameData;
	
	private baseWorld world;
	
	public GameplayState(ClientBase<GameData> theClient, States theState ) {
        super(theClient, theState);
        theGameData = getClient().getGameData();
    }
	
	@Override
	public void init(GameContainer gc, StateBasedGame game)
			throws SlickException {
		
		world = new baseWorld(theGameData.getCurrLevel(), theGameData); 
		bgm = MusicManager.getInstance();
		
		//testback
		btnMainMenu = new MenuButton("Quit level", 630, 500);
		
		
	}
	
	public void update(GameContainer gc, StateBasedGame game, int delta)
			throws SlickException {
		
		double dir_x = 0;
		double jump = 0;
		
        Input input = gc.getInput();
        if (input.isKeyDown(Input.KEY_UP))
            jump += 1;
        if (input.isKeyDown(Input.KEY_LEFT))
            dir_x -= 1;
        if (input.isKeyDown(Input.KEY_RIGHT))
            dir_x += 1;
        world.update(dir_x, jump, delta);
        
        notify(theGameData);
        
        btnMainMenu.update(input);
        if(btnMainMenu.buttonClicked())
        	game.enterState(States.MAINMENUSTATE.getValue());
	}

	@Override
	public int getID() {
		return this.stateID;
	}
	
	public void render(GameContainer gc, StateBasedGame game, Graphics g)
			throws SlickException {
		world.render(g);
		
		btnMainMenu.render(g);
	}
	
	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException{
		bgm.playBGM(1); //TODO change index to something static
	}
	
	public void notify(GameplayStateObserver observer){
		//should not be modified by observers
		observer.observe(this);
	}

	public baseWorld getWorld(){
		return world;
	}
}
