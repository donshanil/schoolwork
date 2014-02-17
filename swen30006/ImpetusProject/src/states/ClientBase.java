package states;

import org.newdawn.slick.state.StateBasedGame;


/*
 * Base class for the game client, allows us to pass game data (current level, achievement tracking)
 * between game states.
 */
public abstract class ClientBase<T> extends StateBasedGame {

	private T gameData = null;
	
	public ClientBase(String name, T gameStateData) {
		super(name);
		setGameData(gameStateData);
	}

	private void setGameData(T gameStateData) {
		this.gameData = gameStateData;
	}
	
	public T getGameData(){
		return gameData;
	}
}
