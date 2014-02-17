package saving;

import states.GameplayState;

public interface GameplayStateObserver {
	//ONLY to be used as an observer. should not change gameState.
	public void observe(GameplayState gameState);
}
