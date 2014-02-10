/* 433-294 Object Oriented Software Development
 * RPG Game Engine
 * Sample Solution
 * Author: Matt Giuca <mgiuca>
 */

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/** The character which the user plays as.
 */
public class Player_old
{
    private Image img = null;
    private Image img_flipped = null;
    
    public static final float PLAYER_STARTING_X = 756;
    public static final float PLAYER_STARTING_Y = 684;

    // In pixels
    private double x;
    private double y;
    private boolean face_left = false;

    // Pixels per millisecond
    private static final double SPEED = 0.25;

    /** The x coordinate of the player (pixels). */
    public double getX()
    {
        return x;
    }

    /** The y coordinate of the player (pixels). */
    public double getY()
    {
        return y;
    }

    /** Creates a new Player.
     * @param image_path Path of player's image file.
     */
    public Player_old(String image_path)
        throws SlickException
    {
        img = new Image(image_path);
        img_flipped = img.getFlippedCopy(true, false);
        this.x = PLAYER_STARTING_X;
        this.y = PLAYER_STARTING_Y;
    }

    /** Move the player in a given direction.
     * Prevents the player from moving outside the map space, and also updates
     * the direction the player is facing.
     * @param world The world the player is on (to check blocking).
     * @param dir_x The player's movement in the x axis (-1, 0 or 1).
     * @param dir_y The player's movement in the y axis (-1, 0 or 1).
     * @param delta Time passed since last frame (milliseconds).
     */
    public void move(World world, double dir_x, double dir_y, double delta)
    {
        // Update player facing based on X direction
        if (dir_x > 0)
            this.face_left = false;
        else if (dir_x < 0)
            this.face_left = true;

        // Move the player by dir_x, dir_y, as a multiple of delta * speed
        double new_x = this.x + dir_x * delta * SPEED;
        double new_y = this.y + dir_y * delta * SPEED;

        // Check if the player is on a tile which blocks movement, and stop
        if (world.terrainBlocks(new_x, new_y))
        {
            // Reset either the x or the y or both to the old one
            if (!world.terrainBlocks(this.x, new_y))
                new_x = this.x;
            else if (!world.terrainBlocks(new_x, this.y))
                new_y = this.y;
            else
            {
                new_x = this.x;
                new_y = this.y;
            }
        }

        // Commit to the new location
        this.x = new_x;
        this.y = new_y;
    }

    /** Draw the player to the screen at the correct place.
     * @param g The current Graphics context.
     * @param cam_x Camera x position in pixels.
     * @param cam_y Camera y position in pixels.
     */
    public void render(Graphics g, int cam_x, int cam_y)
    {
        Image which_img;
        // Calculate the player's on-screen location from the camera
        int screen_x, screen_y;
        screen_x = (int) (this.x - cam_x);
        screen_y = (int) (this.y - cam_y);
        which_img = this.face_left ? this.img_flipped : this.img;
        which_img.drawCentered(screen_x, screen_y);
    }
}
