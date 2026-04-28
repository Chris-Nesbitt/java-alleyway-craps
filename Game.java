//Chris Nesbitt
//2026-04-10
//a game class that stores the list of players and the total amount of money in the game

import java.util.ArrayList;

public class Game {
	private int totalPotAmount;
	private ArrayList<Player> playerList;
	private String winner;
	
	//0 arg constructor --> these values will be initialized in populatePlayerList
	public Game() {
		this.totalPotAmount = 0;
		this.playerList = new ArrayList<Player>();
		this.setWinner("");
	}

	//getter
	public int getTotalPotAmount() {
		return totalPotAmount;
	}
	
	//getter
	public ArrayList<Player> getPlayerList(){
		return playerList;
	}
	
	//takes a list of name and makes a player object for each of them using the player constructor
	public void populatePlayerList(String [] playerNames) {
		for(int i = 0; i < playerNames.length; i++) {
			playerList.add(new Player(playerNames[i]));
		}
		
		//sets the totalPotAmount
		totalPotAmount = playerList.size() * playerList.get(0).getBankBalance();
	}
	
	//checks to see if anyone is hoarding all the money
	public boolean checkForGameWinner() {
		for(Player x : playerList) {
			if(x.getBankBalance() == totalPotAmount) {
				this.setWinner(x.getName());
				return true;
			}
		}
		return false;
	}

	public String getWinner() {
		return winner;
	}

	private void setWinner(String winner) {
		this.winner = winner;
	}
	
}

// End of class