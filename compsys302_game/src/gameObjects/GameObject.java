/* compsys302 2016 - group30
 * GameObject
 * Abstract class that contains the basics for each object, such as angle control and movement
 * Contains all setters and getters for all properties
 */
package gameObjects;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class GameObject {
	protected int x, y, velX, velY;
	protected String id;
	protected int angle;
	protected double speed;
	protected boolean life;
	protected BufferedImage image;
	protected String owner;
	protected Random rand;
	protected boolean shield;
	protected double tempX, tempY;
	protected int prevX, prevY;
	public String objID;
	
	
	public abstract void init();

	public abstract void respawn(int x, int y, int angle);

	public abstract void update();

	public GameObject(int x, int y, String id, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.id = id;
		image = sprite;
		prevX = 0;
		prevY = 0;
		tempX = 0;
		tempY = 0;
		angle = 0;
		life = true;
	}

	public GameObject(int x, int y, String id, BufferedImage sprite, String objID) {
		this.x = x;
		this.y = y;
		this.id = id;
		image = sprite;
		prevX = 0;
		prevY = 0;
		tempX = 0;
		tempY = 0;
		angle = 0;
		life = true;
		this.objID = objID;
	}
	
	

	public Rectangle getBounds() {
		Rectangle temp;
		temp = new Rectangle(0,0,image.getWidth(), image.getHeight());
		return temp;
	}
	
	public Rectangle getRectangle(){
		Rectangle temp;
		temp = new Rectangle(x,y,image.getWidth(), image.getHeight());
		return temp;
	}

	public boolean isAlive() {
		return life;
	}

	public void setAlive(boolean life) {
		this.life = life;
	}

	public void angleCheck() {
		if (angle == 16) {
			angle = 0;
		} else if (angle < 0) {
			angle = 15;
		}
	}

	public void setTempXY(){
		if (angle >= 0 && angle <= 3) {
			tempY = -Math.cos(angle * Math.PI / 8);
			tempX = Math.sin(angle * Math.PI / 8);
		} else if (angle >= 4 && angle <= 7) {
			tempX = Math.cos((angle - 4) * Math.PI / 8);
			tempY = Math.sin((angle - 4) * Math.PI / 8);
		} else if (angle >= 8 && angle <= 11) {
			tempY = Math.cos((angle - 8) * Math.PI / 8);
			tempX = -Math.sin((angle - 8) * Math.PI / 8);
		} else if (angle >= 12 && angle <= 15) {
			tempX = -Math.cos((angle - 12) * Math.PI / 8);
			tempY = -Math.sin((angle - 12) * Math.PI / 8);
		}
	}
	
	public void setPrevPov(){
		x = prevX;
		y = prevY;
	}
	
	public double getTempX() {
		return tempX;
	}

	public void setTempX(double tempX) {
		this.tempX = tempX;
	}

	public double getTempY() {
		return tempY;
	}

	public void setTempY(double tempY) {
		this.tempY = tempY;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double d) {
		this.speed = d;
	}

	public abstract void draw(Graphics2D g);

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getVelX() {
		return velX;
	}

	public void setVelX(int velX) {
		this.velX = velX;
	}

	public int getVelY() {
		return velY;
	}

	public void setVelY(int velY) {
		this.velY = velY;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean hasShield() {
		return shield;
	}

	public void setShield(boolean shield) {
		this.shield = shield;
	}
	public int getPrevX(){
		return prevX;
	}
	public int getPrevY(){
		return prevY;
	}

	public abstract void powered(String s);

}
