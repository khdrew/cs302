/* compsys302 2016 - group30
 * Game Screen
 * Where the game is displayed on the panel.
 * This also imports all the game objects and images.
 * The objects are placed into the object handler where all the game logic is controlled
 * The objects are then obtained again to be displayed
 * Also creates the log file with the key presses
 */

package screenState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import audio.AudioPlayer;
import gameObjects.*;
import imageObjects.*;
import main.SocketClient;
import main.GameWindow;

public class GameScreen extends ScreenObject {

	private Background bg;
	private GameObjectHandler goh;
	private String mode, map, difficulty, p1, p2;
	private HUD hud;
	private Tank tank1, tank2;
	private AITank ai;
	private boolean loading;
	private boolean gameStarted;
	private boolean gameEnd;
	private BufferedImage tank1Image, tank2Image, bulletImage, flare0, flare1, wallImage, shieldImage, powerUpImage;
	private AudioPlayer sfx;
	private PrintWriter outputStream;
	DateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	DateFormat keyFormat = new SimpleDateFormat("mm:ss:SS");
	Calendar cal = Calendar.getInstance();
	
	//online mode variables
	public SocketClient socket;
	
	
	// single player mode
	public GameScreen(ScreenObjectHandler soh, String mode, String map, AudioPlayer sfx, String difficulty) {
		this.soh = soh; // single player constructor
		this.mode = mode;
		this.map = map;
		this.sfx = sfx;
		this.difficulty = difficulty;
		try {
			outputStream = new PrintWriter("game_" + dateFormat.format(cal.getTime())+ ".log");
			bg = new Background("res/game_bg.gif");
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// online mode
	public GameScreen(ScreenObjectHandler soh, String mode, String map, AudioPlayer sfx, SocketClient socket){
		this.soh = soh; // default constructor
		this.mode = "Online";
		this.map = map;
		this.sfx = sfx;
		this.socket = socket;
		
		if (!socket.connection){
			JFrame frame = new JFrame("No Connection");
		    // show a joptionpane dialog using showMessageDialog
		    JOptionPane.showMessageDialog(frame,
		        "No game connection. Please challenge an user and enter the Home Screen.",
		        "No Connection",
		        JOptionPane.INFORMATION_MESSAGE);
		    System.exit(0);
		}
		
		System.out.println("Master: " + socket.master);
		try {
			outputStream = new PrintWriter("game_" + dateFormat.format(cal.getTime())+ ".log");
			bg = new Background("res/game_bg.gif");
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// multi-player or practise
	public GameScreen(ScreenObjectHandler soh, String mode, String map, AudioPlayer sfx) {
		this.soh = soh; // default constructor
		this.mode = mode;
		this.map = map;
		this.sfx = sfx;
		try {
			outputStream = new PrintWriter("game_" + dateFormat.format(cal.getTime())+ ".log");
			bg = new Background("res/game_bg.gif");
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	String idString = "00000000";
	public int idNo = 0;
	public void newID(){
		idNo++;
		idString = String.valueOf(idNo);
		String append = "";
		for (int k = 0; k < (8 - idString.length()); k++){
			append = append + '0';
		}
		idString = append + idString;
	}
	
	public void init() {
		loading = true;
		gameEnd = false;
		
		if (mode != "Online"){
			JFrame frame = new JFrame("Input Name");
			p1 = JOptionPane.showInputDialog(frame, "Please input player one name");
			if (p1 == null){
				soh.setScreen(ScreenObjectHandler.MENUSCREEN);
			}
			if (mode == "Multi Player"){
				p2 = JOptionPane.showInputDialog(frame, "Please input player two name");
				if (p2 == null){
					soh.setScreen(ScreenObjectHandler.MENUSCREEN);
				}
			}else if (mode == "Single Player"){
				p2 = "Bot";
			}
		}else{ // online
			p1 = "You";
			p2 = "Enemy";
		}
		hud = new HUD(mode,p1,p2);
		goh = new GameObjectHandler(hud);
		
		
		//loading images
		bulletImage = GameWindow.LoadImage("res/bullet.png");
		flare0 = GameWindow.LoadImage("res/bullet_pop/flare_0.png");
		flare1 = GameWindow.LoadImage("res/bullet_pop/flare_1.png");
		tank1Image = GameWindow.LoadImage("res/tank1.png");
		tank2Image = GameWindow.LoadImage("res/tank2.png");
		shieldImage = GameWindow.LoadImage("res/shield.png");
		powerUpImage = GameWindow.LoadImage("res/powerUp.png");
		wallImage = GameWindow.LoadImage("res/wall.png");
		
		// add audio player
		goh.addSfx(sfx);
		
		
		// figure out the objects to be added into the game
		tank1 = null;
		tank2 = null;
		boolean tank1Add = false;
		boolean tank2Add = false;
		
		
		String idString = "00000000";
		if (mode != "Online" || socket.master){
			// add the map into the game
			MapLoader ml = new MapLoader("res/maps/" + map + ".gif");
			for (int i = 0; i < 64; i++){
				for (int j = 0; j < 85; j++){
					if (ml.getR(j,i) == 0 && ml.getG(j,i) == 0 && ml.getB(j,i) == 0){ // add wall
						if (mode != "Online"){
							goh.addObject(new Wall(j * 12 + 2,i * 12,"Wall",wallImage));
						}else if (mode == "Online"){
							goh.addObject(new Wall(j * 12 + 2,i * 12,"Wall",wallImage, idString));
							newID();
						}
					}else if (ml.getR(j,i) == 0 && ml.getG(j,i) == 0 && ml.getB(j,i) == 255){ // add player 1
						if (mode != "Online"){
							if (!tank1Add){
								tank1Add = true;
								tank1 = new Tank(j * 12 + 2, i * 12, "Player1", tank1Image, shieldImage);
							}
						}else if (mode == "Online"){
							if (!tank1Add){
								tank1Add = true;
								tank1 = new Tank(idString, j * 12 + 2, i * 12, "Player1", tank1Image, shieldImage);
								newID();
							}
						}
					}else if (ml.getR(j,i) == 255 && ml.getG(j,i) == 0 && ml.getB(j,i) == 0){ // add player 2
						if (mode != "Online"){
							if (!tank2Add){
								tank2Add = true;
								if (mode != "Single Player"){
									tank2 = new Tank(j * 12 + 2, i * 12, "Player2", tank2Image, shieldImage);
								}else{
									ai = new AITank(j * 12 + 2, i * 12, "Player2", tank2Image, shieldImage,bulletImage, flare0, flare1, goh,sfx, tank1, difficulty);
								}
							}
						}else if (mode == "Online"){
							if (!tank2Add){
								tank2Add = true;
								tank2 = new Tank(idString, j * 12 + 2, i * 12, "Player2", tank2Image, shieldImage);
								newID();
							}
						}
					}
				}
			}
			// if no tank was found in map image, force add into game.
			if (!tank1Add){
				tank1 = new Tank(GameWindow.WIDTH/8 - 21, GameWindow.HEIGHT/2 - 21, "Player1", tank1Image, shieldImage);
			}
			if (!tank2Add){
				if (mode != "Single Player"){
					tank2 = new Tank(GameWindow.WIDTH * 7/8 - 21, GameWindow.HEIGHT/2 - 21, "Player2", tank2Image, shieldImage);
				}else{
					ai = new AITank(GameWindow.WIDTH * 7/8 - 21, GameWindow.HEIGHT/2 - 21, "Player2", tank2Image, shieldImage, bulletImage, flare0, flare1, goh, sfx, tank1, difficulty);
				}
			}
			goh.addObject(tank1);
			if (mode != "Single Player"){
				goh.addObject(tank2);
			}else{
				goh.addObject(ai);
			}
		}else{ // slave mode
			// request for master objects
			socket.askObjects();
		}
		// finish loading booleans		
		
		if (mode == "Online"){
			socket.setGameState(1);
			hud.addSocket(socket);
			if (socket.master){ // waiting for other user to load
				//while (socket.theirState != 1){}
			}
		}
		loading = false;
		gameStarted = true;
	}

	private int tick = 0;
	private int countDown = 3;
	private int numSpawn = 0;
	private int onlineTick = 0;
	
	public void update() {
		if (gameStarted){ // 3second count down
			tick++;
			if (tick >= GameWindow.secToTicks(1)){
				tick = 0;
				countDown--;
				if (countDown < 0){
					gameStarted = false;
					sfx.play("tankExplode");
					goh.pause();
					if (mode == "Online"){
						socket.setGameState(2);
					}
				}else{
					sfx.play("pop");
				}
			}
		}else{  // game running
			goh.update();
			if (mode != "Online" || socket.master){
				if (gameEnd){ // if time runs out, end game
					hud.blink();
				}else if (hud.getElapseTicks() >= GameWindow.secToTicks(120.0)){
					gameEnd = true;
					socket.setGameState(4);
					goh.pause();
				}else{ // add power ups into the game using game protocol rules
					tick++;
					if (numSpawn < 3){
						int r = rand.nextInt(GameWindow.secToTicks(10));
						if (r == 1){
							numSpawn++;
							PowerUp temp = new PowerUp(2500,2500,powerUpImage);
							goh.addObject(temp);
							goh.respawnObject(temp);
						}
					}
					if (tick >= GameWindow.secToTicks(30)){
						numSpawn = 0;
						tick = 0;
					}
				}
			}else{ // slave
				hud.set1(socket.slaveScore);
				hud.set2(socket.masterScore);
			}
			// time out to socket
			if (mode == "Online"){
				onlineTick++;
				if (onlineTick >= GameWindow.secToTicks(1)){
					onlineTick = 0;
					socket.setTime(hud.remainingSeconds());
					if (!socket.connection){
						JFrame frame = new JFrame("No Connection");
					    // show a joptionpane dialog using showMessageDialog
					    JOptionPane.showMessageDialog(frame,
					        "No game connection. Please challenge an user and enter the Home Screen.",
					        "No Connection",
					        JOptionPane.INFORMATION_MESSAGE);
					    System.exit(0);
					}
				}
				
			}
		}
	}

	public void draw(Graphics2D g) {
		if (!loading){ // draw when game is running
			bg.draw(g);
			goh.draw(g);
			// paused/not running
			if (!goh.isRunning() && !gameEnd && !gameStarted) {
				// shadow
				g.setColor(Color.BLACK);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 64));
				g.drawString("Paused", GameWindow.WIDTH / 2 - 103, GameWindow.HEIGHT / 2 - 8);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 32));
				g.drawString("Space to return to menu.", GameWindow.WIDTH / 2 - 178, GameWindow.HEIGHT / 2 + 62);
				g.drawString("Press P to un-pause.", GameWindow.WIDTH / 2 - 150, GameWindow.HEIGHT / 2 + 102);
				// text
				g.setColor(Color.WHITE);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 64));
				g.drawString("Paused", GameWindow.WIDTH / 2 - 105, GameWindow.HEIGHT / 2 - 10);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 32));
				g.drawString("Space to return to menu.", GameWindow.WIDTH / 2 - 180, GameWindow.HEIGHT / 2 + 60);
				g.drawString("Press P to un-pause.", GameWindow.WIDTH / 2 - 152, GameWindow.HEIGHT / 2 + 100);
			}else if (gameEnd){ // game end screen
				// shadow
				g.setColor(Color.BLACK);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 64));
				g.drawString("Times Up!", GameWindow.WIDTH / 2 - 140, GameWindow.HEIGHT / 2 - 8);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 32));
				g.drawString("Space to return to menu.", GameWindow.WIDTH / 2 - 178, GameWindow.HEIGHT / 2 + 62);
				// text
				g.setColor(Color.WHITE);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 64));
				g.drawString("Times Up!", GameWindow.WIDTH / 2 - 142, GameWindow.HEIGHT / 2 - 10);
				g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 32));
				g.drawString("Space to return to menu.", GameWindow.WIDTH / 2 - 180, GameWindow.HEIGHT / 2 + 60);
				if (hud.getScore1() > hud.getScore2()){
					g.setColor(Color.BLACK);
					g.drawString(p1 + " WINS!", GameWindow.WIDTH / 2 - 83, GameWindow.HEIGHT / 2 + 102);
					g.setColor(Color.BLUE);
					g.drawString(p1 + " WINS!", GameWindow.WIDTH / 2 - 85, GameWindow.HEIGHT / 2 + 100);
				}else if (hud.getScore1() < hud.getScore2()){
					g.setColor(Color.BLACK);
					g.drawString(p2 + " WINS!", GameWindow.WIDTH / 2 - 83, GameWindow.HEIGHT / 2 + 102);
					g.setColor(Color.RED);
					g.drawString(p2 + " WINS!", GameWindow.WIDTH / 2 - 85, GameWindow.HEIGHT / 2 + 100);
				}else{
					g.setColor(Color.BLACK);
					g.drawString("Tied! No Winner", GameWindow.WIDTH / 2 - 110, GameWindow.HEIGHT / 2 + 102);
					g.setColor(Color.WHITE);
					g.drawString("Tied! No Winner", GameWindow.WIDTH / 2 - 112, GameWindow.HEIGHT / 2 + 100);
				}
			}
		}else{ // loading screen
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
			g.setColor(Color.WHITE);
			g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 32));
			g.drawString("Loading...", 80, GameWindow.HEIGHT - 60);
		}
		if (gameStarted){ // count down running
			g.setColor(Color.WHITE);
			g.setFont(new Font(GameWindow.FONTSTYLE, Font.BOLD, 64));
			if (countDown != 0){
				g.drawString(countDown + "!", GameWindow.WIDTH / 2 - 16, GameWindow.HEIGHT / 2 - 10);
			}else{
				g.drawString("Start!", GameWindow.WIDTH / 2 - 80, GameWindow.HEIGHT / 2 - 10);
			}
		}
	}

	public void fire(Tank tank) { // fire function
		if (tank.isAbleFire()){
			tank.setAbleFire(false);
			sfx.play("gunShot");
			goh.addObject(new Bullet(tank, "Bullet", 9, tank.getId(), bulletImage, flare0, flare1));
		}
	}
	
	private Random rand = new Random();
	public void keyPressed(int k) { // key press and records to log
		
		if (!goh.isPaused()) { // only when no paused, receive commands
			// player 1
			if (mode != "Online" || socket.master){
				if (k == KeyEvent.VK_UP) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_UP);
					tank1.setVelY(1);
					
				}
				if (k == KeyEvent.VK_DOWN) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_DOWN);
					tank1.setVelY(-1);
				}
				if (k == KeyEvent.VK_LEFT) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_LEFT);
					tank1.turnLeft();
				}
				if (k == KeyEvent.VK_RIGHT) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_RIGHT);
					tank1.turnRight();
				}
				if (k == KeyEvent.VK_SPACE) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_CONTROL);
					fire(tank1);
				}
			}else if (!socket.master){
				// Online
				if (k == KeyEvent.VK_LEFT){
					socket.setKey(0, '1');
				}
				if (k == KeyEvent.VK_RIGHT){
					socket.setKey(1, '1');
				}
				if (k == KeyEvent.VK_UP) {
					socket.setKey(2, '1');
				}
				if (k == KeyEvent.VK_DOWN) {
					socket.setKey(3, '1');
				}
				if (k == KeyEvent.VK_SPACE){
					socket.setKey(4, '1');
				}
			}

			// player 2
			if (mode == "Multi Player"){ // only receive commands when multi-player
				if (k == KeyEvent.VK_W) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_W);
					tank2.setVelY(1);
				}
				if (k == KeyEvent.VK_S) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_S);
					tank2.setVelY(-1);
				}
				if (k == KeyEvent.VK_A) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_A);
					tank2.turnLeft();
				}
				if (k == KeyEvent.VK_D) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_D);
					tank2.turnRight();
				}
				if (k == KeyEvent.VK_G) {
					cal = Calendar.getInstance();
					outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_G);
					fire(tank2);
				}
			}
			
			if (mode == "Practise"){ // add practise mode perks
				if (k == KeyEvent.VK_F2){
					String[] type = {"pshield" , "pspeedUp" , "pspeedDown" , "pfireUp", "pfireDown"};
					int currentSelect = rand.nextInt(type.length);
					tank1.powered(type[currentSelect]);
					tank2.powered(type[currentSelect]);
				}
			}
			
			if (k == KeyEvent.VK_F5){ // respawn tanks if stuck
				goh.respawnObject(tank1);
				if (mode != "Single Player"){
					goh.respawnObject(tank2);
				}else{
					goh.respawnObject(ai);
				}
			}
			
		}
		if (goh.isPaused()) { // to return to menu screen
			if (k == KeyEvent.VK_SPACE){
				outputStream.close();
				sfx.stop("bgm");
				soh.setScreen(ScreenObjectHandler.MENUSCREEN);
			}
		}
		if (k == KeyEvent.VK_ESCAPE){ // exit game
			if (!gameEnd && mode != "Online"){
				goh.pause();
			}
			if (JOptionPane.showConfirmDialog(null, "Are you sure?", "Quitting?",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				outputStream.close();
				System.exit(1);
			} else {
				if (!gameEnd && mode != "Online"){
					goh.pause();
				}
			}
			
		}
	}
	
	public void keyReleased(int k) { // key releases also records to key logs
		if (k == KeyEvent.VK_A){
			socket.out("EObj");
			
		}
		if (mode != "Online" || socket.master){
			if (k == KeyEvent.VK_UP){
				cal = Calendar.getInstance();
				outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_UP + "-");
				tank1.setVelY(0);
				socket.setKey(2, '0');
				
			}
			if (k == KeyEvent.VK_DOWN){
				cal = Calendar.getInstance();
				outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_DOWN + "-");
				tank1.setVelY(0);
			}
		}else if (!socket.master){
			// Online
			if (k == KeyEvent.VK_LEFT){
				socket.setKey(0, '0');
			}
			if (k == KeyEvent.VK_RIGHT){
				socket.setKey(1, '0');
			}
			if (k == KeyEvent.VK_UP) {
				socket.setKey(2, '0');
			}
			if (k == KeyEvent.VK_DOWN) {
				socket.setKey(3, '0');
			}
			if (k == KeyEvent.VK_SPACE){
				socket.setKey(4, '0');
			}
		}
		
		if (mode == "Multi Player"){
			if (k == KeyEvent.VK_W){
				cal = Calendar.getInstance();
				outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_W + "-");
				tank2.setVelY(0);
			}
			if (k == KeyEvent.VK_S){
				cal = Calendar.getInstance();
				outputStream.println(keyFormat.format(cal.getTime()) + ": " + KeyEvent.VK_S + "-");
				tank2.setVelY(0);
			}
		}	
		
		if (k == KeyEvent.VK_P && !gameEnd && mode != "Online") // pause the game
			goh.pause();
		
	}

}
