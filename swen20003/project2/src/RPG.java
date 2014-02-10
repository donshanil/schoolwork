/* 433-294 Object Oriented Software Development
 * RPG Game Engine
 * Author: Matt Giuca <mgiuca>
 */

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

/** Main class for the Role-Playing Game engine.
 * Handles initialisation, input and rendering.
 */
public class RPG extends BasicGame
{
    // Configuration for the game.

    /** Location of the "assets" directory. */
    public static final String ASSETS_PATH = "assets";

    /** Location of the map file. */
    public static final String MAP_PATH = "/map.tmx";

    /** Location of the player image. */
    public static final String PLAYER_IMAGE_PATH = "/units/player.png";

    /** Player start location. */
    public static final float PLAYER_STARTING_X = 756;
    public static final float PLAYER_STARTING_Y = 684;

    /** Screen width, in pixels. */
    public static final int screenwidth = 960;
    /** Screen height, in pixels. */
    public static final int screenheight = 540;
    
    /** Panel render height */
    public static final int panelheight = 70;

    // The Game itself.

    private World world;

    /** Create a new RPG object. */
    public RPG()
    {
        super("bkchong's dodgy RPG");
    }

    /** Initialise the game state.
     * @param gc The Slick game container object.
     */
    @Override
    public void init(GameContainer gc)
    throws SlickException
    {
        Music overworldMusic = new Music("assets/town.ogg");
        world = new World();
    	gc.setMusicVolume((float) 0.5);
    	overworldMusic.loop();
    }

    /** Update the game state for a frame.
     * @param gc The Slick game container object.
     * @param delta Time passed since last frame (milliseconds).
     */
    @Override
    public void update(GameContainer gc, int delta)
    throws SlickException
    {
        // Get data about the current input (keyboard state).
        Input input = gc.getInput();

        // Update the player's movement direction based on keyboard presses.
        double dir_x = 0;
        double dir_y = 0;
        if (input.isKeyDown(Input.KEY_DOWN))
            dir_y += 1;
        if (input.isKeyDown(Input.KEY_UP))
            dir_y -= 1;
        if (input.isKeyDown(Input.KEY_LEFT))
            dir_x -= 1;
        if (input.isKeyDown(Input.KEY_RIGHT))
            dir_x += 1;

        // Let World.update decide what to do with this data.
        world.update(dir_x, dir_y, delta);
    }

    /** Render the entire screen, so it reflects the current game state.
     * @param gc The Slick game container object.
     * @param g The Slick graphics object, used for drawing.
     */
    public void render(GameContainer gc, Graphics g)
    throws SlickException
    {
        // Let World.render handle the rendering.
        world.render(g);
    }

    /** Start-up method. Creates the game and runs it.
     * @param args Command-line arguments (ignored).
     */
    public static void main(String[] args)
    throws SlickException
    {
        AppGameContainer app = new AppGameContainer(new RPG());
        // setShowFPS(true), to show frames-per-second.
        app.setShowFPS(false);
        app.setDisplayMode(screenwidth, screenheight, false);
        app.start();
    }
}
