package states;

import org.newdawn.slick.state.BasicGameState;

/* 
 * base class for our state based game
 */

public abstract class GameStateBase<T, U extends StateBase> extends BasicGameState {
	private U state;
	private ClientBase<T> client;
	
	public GameStateBase(ClientBase<T> client, U theState)
	{
		setClient(client);
	}
	

	private void setClient(ClientBase<T> client) {
		this.client = client;
		
	}
	
	public ClientBase<T> getClient()
	{
		return client;
	}
	
	private void setState(U theState) 
	{
		state = theState;
	}
	
	public U getState()
	{
		return state;
	}
	
	@Override
	public int getID()
	{
		return getState().getValue();
	}
	
	@Override
	public boolean isAcceptingInput() 
	{
		return getClient().getCurrentState() == this;
	}
}
