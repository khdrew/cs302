/* compsys302 2016 - group30
 * Tank
 * Main body of tank, everything is contained here. 
 * The logic, properties and movement all done within this object.
 * Properties such as dimensions, speed, angle and power ups.
 */
package gameObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import main.GameWindow;

public class Tank extends GameObject {
	
	private static final double DSPEED = 3.0;
	private static final double DFIRECD = 1;
	private boolean rev, powerUped;
	private int life, powerTime;
	private BufferedImage shieldImage;
	private double fireCD;
	private boolean ableFire;
	private int nextX, nextY, nextAngle;
	private int fireCount;
	private String powerString, outString;
	private int dispTick, dispRange;
	private boolean justDown, dispEnd, test;
	
	// overloading constructors for various uses
	public Tank(int x, int y, String id, BufferedImage tankSprite, BufferedImage shieldImage) {
		super(x, y, id, tankSprite); // default constructor
		this.shieldImage = shieldImage;
		init();
		if (id == "Player1") {
			angle = 4;
		} else {
			angle = 12;
		}
	}
	
	public Tank(String objID, int x, int y, String id, BufferedImage tankSprite, BufferedImage shieldImage) {
		super(x, y, id, tankSprite, objID); // default constructor
		this.shieldImage = shieldImage;
		init();
		if (id == "Player1") {
			angle = 4;
		} else {
			angle = 12;
		}
	}
	
	public Tank(int x, int y, String id, int angle){
		super(x,y,id,null); // constructor used to calculate AI logic
		this.angle = angle;
	}
	
	public Tank(int x, int y, String id, BufferedImage tankSprite, BufferedImage shieldImage, int angle) {
		super(x, y, id, tankSprite); // constructor used for the show case.
		this.shieldImage = shieldImage;
		test = true;
		this.angle = angle;
		init();
	}

	public void init() { // initialize the variables
		ableFire = true;
		speed = DSPEED;
		fireCD = DFIRECD;
		fireCount = 0;
		rev = false;
		justDown = false;
		shield = false;
		life = 0;
		powerTime = 0;
		powerUped = false;
		rand = new Random();
		
	}
	
	//gun fire cool down
	public boolean isAbleFire(){
		return ableFire;
	}
	
	public void setAbleFire(boolean a){
		ableFire = a;
	}
		
	public void respawn(int x, int y, int angle) {//respawn flags raised.
		life = 0;
		nextX = x;
		nextY = y;
		nextAngle = angle;
		rev = true;
		resetPower();
		powerUped = false; // reset power ups upon res
		justDown = false;
		dispEnd = false;
	}

	public void update() { // update position of the tanks using user inputs
		if (rev) { // revive logic
			life++;
			if (life >= GameWindow.secToTicks(1)) { // revive 1 second after death
				x = nextX;
				y = nextY;
				angle = nextAngle;
				rev = false;
				ableFire = true;
			}
		} else {
			setTempXY();
			prevX = x;
			prevY = y;
			// set pos
			x += (int) (tempX * speed) * velY;
			y += (int) (tempY * speed) * velY;
			
			if (!test){ // if not in show case mode
				// out of bounds logic
				if (x < 0 || x > GameWindow.WIDTH - 46) {
					x -= (int) (tempX * speed) * velY;
				}
				if (y < 0 || y > GameWindow.HEIGHT - 48) {
					y -= (int) (tempY * speed) * velY;
				}
			}
		}
		
		if (powerUped){ // calculate powerUp up time
			powerTime++;
			if (powerTime >= (GameWindow.secToTicks(15.0))){
				resetPower();
				powerUped = false;
				justDown = true;
				dispTick = 0;
				dispRange = 1;
				dispEnd = false;
				powerString = "Effect End...";
			}
		}
		if (!ableFire){ // set gun fire cool down time
			fireCount++;
			if (fireCount >= GameWindow.secToTicks(fireCD)){
				fireCount = 0;
				ableFire = true;
			}
		}
	}

	public void turnLeft() { // turn angle left
		setAngle(getAngle() - 1);
		angleCheck();
	}

	public void turnRight() { // turn angle right
		setAngle(getAngle() + 1);
		angleCheck();
	}

	public void draw(Graphics2D g) {
		// draw to screen the tank
		AffineTransform at = AffineTransform.getTranslateInstance(x, y);
		at.rotate(Math.toRadians(angle * 22.5), image.getWidth() / 2, image.getHeight() / 2);
		g.drawImage(image, at, null); // rotate image
		
		AffineTransform at1 = AffineTransform.getTranslateInstance(x - 9, y - 9);
		if (shield){ // draw shield if shield is active
			g.drawImage(shieldImage, at1, null);
		}
		if (powerUped || justDown){ // display the power up text that is obtained
			dispTick++; // as well as display when power up is lost
			if (!dispEnd){
				if (dispTick >= GameWindow.secToTicks(0.01)){
					if (dispRange <= powerString.length()){
						dispTick = 0;
						outString = powerString.substring(0,dispRange);
						dispRange++;
						dispTick = 2000;
					}
				}
				g.setColor(Color.WHITE);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 10));
				g.drawString(outString, x + 24, y + 65);
			}
			if (dispTick >= GameWindow.secToTicks(3) + 2000){
				dispEnd = true;
				justDown = false;
			}
		}
		

	}

	public void powered(String s) { // power up logic, determines the effect gained.
		powerUped = true;
		resetPower();
		if (s == "pshield"){
			powerString = "Shield Up...";
			setShield(true);
		}else if (s == "pspeedUp"){
			powerString = "Speed Up...";
			setSpeed(DSPEED * 1.5);
		}else if (s == "pspeedDown"){
			powerString = "Speed Down...";
			setSpeed(DSPEED * 0.5);
		}else if (s == "pfireUp"){
			powerString = "Fire Rate Up...";
			fireCD = DFIRECD * 0.5;
		}else if (s == "pfireDown"){
			powerString = "Fire Rate Down...";
			fireCD = fireCD * 1.5;
		}
		dispTick = 0;
		dispRange = 1;  // start the power up text display
		dispEnd = false;
	}
	
	public String getPowerString(){
		return powerString;
	}
	
	public void resetPower(){ // reset power up flags
		powerTime = 0;
		shield = false;
		speed = DSPEED;
		fireCD = DFIRECD;
		dispTick = 0;
	}

}
