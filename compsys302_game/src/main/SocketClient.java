package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class SocketClient implements Runnable{
	String FromServer;
	String ToServer = "temp";
	BufferedReader inFromUser ;
	PrintWriter outToServer;
	BufferedReader inFromServer;
	static Socket clientSocket;
	public boolean master = false;
	public boolean connection = false;
	public int gameState = 0;
	public int theirState = 0;
	public int time = 120;
	String keys = "00000";
	public int masterScore = 0;
	public int slaveScore = 0;
	ArrayList<ArrayList<String>> list;
	
	public SocketClient(){
		
	}
	
	public void out(String text){
		outToServer.println(text);
	}
	
	private void init(){
		try{
			clientSocket = new Socket("0.0.0.0", 10031);
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}catch(Exception e){
			System.out.println("Error");
		}
	}
	
	public void setKey(int type, char value){
		char[] keyChars = keys.toCharArray();
		if (keyChars[type] != value){
			keyChars[type] = value;
			keys = String.valueOf(keyChars);
			String text = String.valueOf(type) + value;
			String outStr = "Keys" + text;
			outToServer.println(outStr);
		}
	}
	
	
	public void sendScoreA(int master){
		outToServer.println("ScoA" + String.valueOf(master));
	}
	
	public void sendScoreB(int slave){
		outToServer.println("ScoB" + String.valueOf(slave));
	}
	
	public void setGameState(int state){
		this.gameState = state;
		String outStr = "gSta" + String.valueOf(state);
		outToServer.println(outStr);
	}
	
	public void askObjects(){
		outToServer.println("objectsPlease");
	}
	
	public void setTime(int time){
		this.time = time;
		String outStr = "Time" + String.valueOf(time);
		outToServer.println(outStr);
	}
	
	public void run(){
		try {
			init();
			while (true) {
				FromServer = inFromServer.readLine();
				{
					if (FromServer.contains("Welcome: Master")){
						master = true;
						connection = true;
					}else if (FromServer.contains("Welcome: Slave")){
						master = false;
						connection = true;
					}else if (FromServer.contains("Score")){
						FromServer = inFromServer.readLine();{
							this.masterScore = Integer.parseInt(FromServer.trim()); 
						}
						FromServer = inFromServer.readLine();{
							this.slaveScore = Integer.parseInt(FromServer.trim()); 
						}
					}else if (FromServer.contains("TheirState")){
						FromServer = inFromServer.readLine();{
							this.theirState = Integer.parseInt(FromServer.trim()); 
						}
					}

					System.out.println("RECIEVED: " + FromServer);				
					
				}
			}
		} catch (Exception e) {
			System.out.println("Error");
			connection = false;
		}
	}
}
