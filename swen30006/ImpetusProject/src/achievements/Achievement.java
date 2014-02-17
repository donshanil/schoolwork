package achievements;

import org.newdawn.slick.SavedState;

import saving.GameplayStateObserver;
import states.GameplayState;

public class Achievement implements GameplayStateObserver {
	protected boolean done;
	
	public Achievement(){
		done = false;
	}
	
	public boolean isDone(){
		return done;
	}

	public void observe(GameplayState gameState) {
		// override this with whatever the cheevo is!
		// don't forget to skip this if done
		if(done)
			return;
		else
			done = true;
	}

	public void save(SavedState savedGameData) {
		//use savedGameData as you want
	}

	public void load(SavedState savedGameData) {
		//load whatever in sgd or just pass
	}
}
