/*
 * An enum that stores the different game states.
 */

package states;

public enum States implements StateBase {
	
	MAINMENUSTATE(0),
	HIGHSCORESTATE(1),
    LEVELSELECTSTATE(2),
    GAMEPLAYSTATE(3);
	private int value;
	
	States(int theValue)
	{
		value = theValue;
	}
	
	@Override
	public int getValue() {
		return value;
	}
	
}
