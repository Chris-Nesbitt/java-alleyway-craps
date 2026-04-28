//Chris Nesbitt
//2026-04-10
//A player class that contains all the relevant information for a player (and the getters and setters for said data)

public class Player {
	private String name;
	private int bankBalance;
	private boolean isShooter;
	private int betAmount;
	private boolean passCompleted;

	// I added this variable as a way to more efficiently check to see if the player
	// was out of the game
	private boolean bankrupt;

	// 1 arg constructor --> take a name, make a player (default $100 and no bets
	// placed)
	public Player(String name) {
		this.name = name;
		this.bankBalance = 100;
		this.betAmount = 0;

		// These two variables are here, but I cannot understand why
		// I find it simpler to have them in the pass instead (shooterID was already
		// there)
		this.isShooter = false;
		this.passCompleted = false;
	}

	// getter
	public String getName() {
		return name;
	}

	// getter
	public int getBankBalance() {
		return bankBalance;
	}

	// setter
	public void setBankBalance(int bankBalance) {
		this.bankBalance = bankBalance;
	}

	// getter
	public boolean getIsShooter() {
		return isShooter;
	}

	// setter
	public void setIsShooter(boolean isShooter) {
		this.isShooter = isShooter;
	}

	// getter
	public int getBetAmount() {
		return betAmount;
	}

	// setter
	public void setBetAmount(int betAmount) {
		this.betAmount = betAmount;
	}

	// getter
	public boolean isPassCompleted() {
		return passCompleted;
	}

	// setter
	public void setPassCompleted(boolean passCompleted) {
		this.passCompleted = passCompleted;
	}

	// getter
	public boolean isBankrupt() {
		return bankrupt;
	}

	// setter
	public void setBankrupt(boolean bankrupt) {
		this.bankrupt = bankrupt;
	}

}

// End of class
