//Chris Nesbitt
//2026-04-10
//A die class whose only purpose is to simulate rolling a die

import java.util.Random;

public class Die {
	private int rollValue;
	
	public Die() {
		this.rollValue = 0;
	}
	
	//rolls the dice, returns the value... easy, breezy, beautiful...covergirl?
	public int rollDice(){
		Random rand = new Random();
		this.rollValue = rand.nextInt(1, 7);
		return rollValue;
	}
}
