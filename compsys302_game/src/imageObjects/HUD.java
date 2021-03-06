/* compsys302 2016 - group30
 * HUD
 * Overlay object used for the game screen.
 * To be drawn last, over everything else.
 * This displays the time and score.
 * The score logic is also controlled here.
 */

package imageObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import main.GameWindow;
import main.SocketClient;

public class HUD {
	private int score1, score2;
	private int ticks, totalTicks;
	private boolean running, on;
	private int minutes, seconds;
	private String mode, p1, p2;
	private SocketClient socket;
	
	public HUD(String mode, String p1, String p2){ // initialize
		this.mode = mode;
		this.p1 = p1;
		this.p2 = p2;
		score1 = 0;
		score2 = 0;
		ticks = 0;
		totalTicks = 0;
		minutes = 2;
		seconds = 0;
		running = false;
		on = true;
	}
	
	public void addSocket(SocketClient socket){
		this.socket = socket;
	}
	
	public void update(){
		if (running){
			if (mode == "Practise"){ // infinite time in practise mode
				totalTicks = 0;
				minutes = 88;
				seconds = 88;
			}else{
				ticks++;
				totalTicks++;
				if (ticks >= GameWindow.secToTicks(1.0)){ // control seconds and minutes
					ticks = 0;
					seconds--;
					if (seconds < 0){
						seconds = 59;
						minutes--;
					}
				}
			}
		}
	}
	
	public void draw(Graphics2D g) { // display hud
		g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 24));
		
		g.setColor(Color.BLACK); // show player 1's score
		g.drawString((p1 + ((score1<= 9)? ": 0": ": ") + score1), 32, 37);
		g.setColor(Color.BLUE);
		g.drawString((p1 + ((score1<= 9)? ": 0": ": ") + score1), 30, 35);
		String s;
		if (mode == "Practise"){ // change player 2's name according to which mode
			s = "Dummy";
		}else if (mode == "Single Player"){
			s = "Bot";
		}else{
			s = p2;
		}
		g.setColor(Color.BLACK); // show player 2's score
		g.drawString((s + ((score2<= 9)? ": 0": ": ") + score2), GameWindow.WIDTH - 153, 37);
		g.setColor(Color.RED);
		g.drawString((s + ((score2<= 9)? ": 0": ": ") + score2), GameWindow.WIDTH - 155, 35);
		
		if (on){ // display time
			g.setColor(Color.BLACK);
			g.drawString((minutes + ((seconds <= 9)? ":0": ":") + seconds), GameWindow.WIDTH/2 - 23, 37);
			g.setColor(Color.WHITE);
			g.drawString((minutes + ((seconds <= 9)? ":0": ":") + seconds), GameWindow.WIDTH/2 - 25, 35);
		}
	}
	
	public int getElapseTicks(){ // gives total time since game has started
		return totalTicks;
	}
	
	public int remainingSeconds(){
		return minutes * 60 + seconds; 
	}
	
	// add to player scores
	public void add1(){
		score1++;
		if (socket.master && mode == "Online"){
			socket.sendScoreA(score1);
		}
	}
	
	public void add2(){
		score2++;
		if (socket.master && mode == "Online"){
			socket.sendScoreB(score2);
		}
	}
	
	public void set1(int i){
		score1 = i;
	}
	
	public void set2(int i){
		score2 = i;
	}
	
	
	// get player scores
	public int getScore1(){
		return score1;
	}
	
	public int getScore2(){
		return score2;
	}
	
	public void blink(){ // blink the time when game ends (visual effect)
		totalTicks++;
		if ((totalTicks % GameWindow.secToTicks(0.5)) == 0){
			on = !on;
		}
	}
	
	public void pause() { // paused game
		running = !running;
	}
	
	public void setRunning(boolean running){ // set the game running state
		this.running = running;
	}
}
