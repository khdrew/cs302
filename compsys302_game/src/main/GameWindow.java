/* compsys302 2016 - group30
 * Game Window
 * Where the game base is set up using this class, and creates the screens
 * The screens are then placed into the panel and displayed.
 */
package main;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import screenState.ScreenObjectHandler;


public class GameWindow extends JPanel implements Runnable, KeyListener {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 1024, HEIGHT = 768;
	public static final int FPS = 30;
	public static final String FONTSTYLE = "Courier";
	private Thread thread;
	private boolean running;
	private long targetTime = 1000 / FPS;
	private BufferedImage image;
	public SocketClient socketClient;
	public Thread socketThread;
	private Graphics2D g;
	public String readLine;
	
	private ScreenObjectHandler soh;

	public GameWindow() {
		super();  // set dimensions
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus(); // make as the active screen
	}

	public void addNotify() {
		super.addNotify(); // insert thread
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}

	private void init() { // add graphics and screens
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		socketClient = new SocketClient();
		socketThread = new Thread(socketClient);
		socketThread.start();
		g = (Graphics2D) image.getGraphics();
		running = true;
		soh = new ScreenObjectHandler(socketClient);
	}

	public void run(){ // game loop
		init();

		long start, elapsed, wait;
		
		
		try{
			while (running) {
	
				start = System.nanoTime();
	
				update();
				draw();
				drawToScreen();
				
				elapsed = System.nanoTime() - start;
	
				wait = targetTime - elapsed / 1000000;
				
				
				if (wait < 0) wait = 5;
				
				try {
					Thread.sleep(wait);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			System.exit(0);
		}
	}

	private void update() { // update logic
		soh.update();
	}

	private void draw() { // draw onto screen
		soh.draw(g);
	}

	private void drawToScreen() { // draw everything on to the screen
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		g2.dispose();
	}


	public void keyPressed(KeyEvent k) { // active key press, set into screen
		soh.keyPressed(k.getKeyCode());
	}

	public void keyReleased(KeyEvent k) { // active key released, set into screen
		soh.keyReleased(k.getKeyCode());
	}
	
	public static BufferedImage LoadImage(String s) {  // static load image called by everything
		BufferedImage image = null;
		try {
			image = ImageIO.read(new FileInputStream(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static int secToTicks(double d){ // static function to convert seconds into ticks/frames
		return (int)(d * FPS);
	}

	public void keyTyped(KeyEvent k) {}
}
