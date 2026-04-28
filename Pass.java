//Chris Nesbitt
//2026-04-10
//A pass class that contains all the relevant information for one round of the game


import java.util.ArrayList;

//One round of craps
public class Pass {
	private int shooterID;
	private int actionAmount;
	private int actionAmountCovered;
	private boolean shooterWin;

	//3 arg constructor --> shooterWin defaults to false
	public Pass(int shooterID, int actionAmount, int actionAmountCovered) {
		this.shooterID = shooterID;
		this.actionAmount = actionAmount;
		this.actionAmountCovered = actionAmountCovered;
		this.shooterWin = false;

	}

	//getter
	public int getShooterID() {
		return shooterID;
	}

	//setter
	public void setShooterID(int shooterID) {
		this.shooterID = shooterID;
	}

	//getter
	public int getActionAmount() {
		return actionAmount;
	}

	//setter
	public void setActionAmount(int actionAmount) {
		this.actionAmount = actionAmount;
	}

	//getter
	public int getActionAmountCovered() {
		return actionAmountCovered;
	}

	//setter
	public void setActionAmountCovered(int actionAmountCovered) {
		this.actionAmountCovered = actionAmountCovered;
	}

	//getter
	public boolean getShooterWinOrLose() {
		return shooterWin;
	}

	//setter
	public void setShooterWinOrLose(boolean winOrLose) {
		this.shooterWin = winOrLose;
	}

	//edits the bank balances based on who won the bet
	//each player from the playerList has 4 outcomes:
	//1 - its the shooter and they won the bet -->player wins money
	//2 - its the shooter and they lost the bet -->player loses money
	//3 - its not the shooter and the shooter lost the bet -->player wins money
	//4 - its not the shooter and the shooter won the bet -->player loses their money
	public void settleBets(ArrayList<Player> playerList) {
		for (int i = 0; i < playerList.size(); i++) {
			Player player = playerList.get(i);
			if (i == shooterID) {
				if (shooterWin) {
					player.setBankBalance(player.getBankBalance() + getActionAmountCovered());
				}

				else {
					player.setBankBalance(player.getBankBalance() - getActionAmountCovered());

				}
			}

			else {
				if (shooterWin) {
					player.setBankBalance(player.getBankBalance() - player.getBetAmount());
				}

				else {
					player.setBankBalance(player.getBankBalance() + player.getBetAmount());

				}
			}
		}

	}

	/*
	 * This function has been placed into the GUI class because it will involve some
	 * GUI elements public boolean shootOrPass() {
	 * 
	 * }
	 */
}