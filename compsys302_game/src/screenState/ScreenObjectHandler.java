/* compsys302 2016 - group30
 * ScreenObjectHandler
 * Controls which screen is to be placed into the screen.
 * This loads and unloads screens to be displayed.
 */

package screenState;

import java.awt.Graphics2D;
import audio.AudioPlayer;
import main.SocketClient;

public class ScreenObjectHandler {
	
	private int currentScreen;
	private ScreenObject[] screenStates;
	private String mode, map, difficulty;
	private AudioPlayer sfx;
	public String readLine;
	public SocketClient socketClient;
	
	public static final int NUMGAMESCREENS = 4;
	public static final int MENUSCREEN = 0;
	public static final int GAMESCREEN = 1;
	public static final int ONLINEGAMESCREEN = 3;
	public static final int HELPSCREEN = 2;

	public ScreenObjectHandler(SocketClient socketClient) { // create first menu screen as first screen
		screenStates = new ScreenObject[NUMGAMESCREENS];
		sfx = new AudioPlayer();
		this.socketClient = socketClient;
		currentScreen = MENUSCREEN;
		loadScreen(currentScreen);
		
	}
	
	public void setMode(String mode){ // set mode for the game
		this.mode = mode;
	}
	
	public void setMap(String map){ // set map for the game
		this.map = map;
	}
	
	public void setDifficulty (String difficulty){ // set the difficulty setting for single player
		this.difficulty = difficulty;
	}

	private void loadScreen(int screen){ // creates new instance of games
		if (screen == MENUSCREEN)
			screenStates[screen] = new MenuScreen(this, sfx);
		if (screen == GAMESCREEN)
			screenStates[screen] = new GameScreen(this, mode, map, sfx, difficulty);
		if (screen == ONLINEGAMESCREEN){
			mode = "Online";
			map = "Tennis";
			screenStates[screen] = new GameScreen(this, mode, map, sfx, socketClient);
		}
	}
	
	private void unloadScreen(int screen){ // ends instance of the unused screen
		screenStates[screen] = null;
	}
	
	
	public void setScreen(int screen) { // set the screen to be used
		unloadScreen(currentScreen);
		currentScreen = screen;
		loadScreen(screen);		
	}

	public void update() { // update the screen currently active
		try{
			screenStates[currentScreen].update();
		}catch(Exception e){}
		
	}

	public void draw(Graphics2D g) { // draw the screen currently active
		try{
		screenStates[currentScreen].draw(g);
		}catch(Exception e){}
	}

	public void keyPressed(int k) {  // use the key press logic from the screen currently active
		try{
		if (screenStates[currentScreen] != null)
		screenStates[currentScreen].keyPressed(k);
		}catch(Exception e){}
	}

	public void keyReleased(int k) { // use the key release logic from the screen currently active
		try{
		if (screenStates[currentScreen] != null)
		screenStates[currentScreen].keyReleased(k);
		}catch(Exception e){}
		
	}

}
