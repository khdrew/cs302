/* compsys302 2016 - group30
 * PowerUp
 * Contains the dimensions, position and images of the power up
 * Randomly generate which power it is
 */

package gameObjects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import main.GameWindow;

public class PowerUp extends GameObject {
	private String[] type = { "pshield", "pspeedUp", "pspeedDown", "pfireUp", "pfireDown" };
	private int currentSelect;
	private Random rand;

	public PowerUp(int x, int y, BufferedImage sprite) {
		super(x, y, "p", sprite);
		init();
	}
	
	public PowerUp(int x, int y, BufferedImage sprite, String objID) {
		super(x, y, "p", sprite, objID);
		init();
		
	}

	public void init() { // initialize variable
		rand = new Random();
		currentSelect = rand.nextInt(type.length);
		id = type[currentSelect];
	}

	public void powered(String s) { // change its current power up (not used)
		currentSelect = rand.nextInt(type.length);
		id = type[currentSelect];
	}

	public void respawn(int x, int y, int nextAngle) { // respawn power up;
		this.x = x;
		this.y = y;
		angle = 0;
	}

	private int tick = 0;

	public void update() { // set death of power up after 10 seconds
		tick++;
		if (tick >= GameWindow.secToTicks(10)) {
			setAlive(false);
		}
	}

	public void draw(Graphics2D g) { //draw power up to screen
		g.drawImage(image, x, y, null);
	}
}
