//Chris Nesbitt
//2026-04-10
//The main GUI class (more details below)

import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

import java.util.ArrayList;
import java.util.Random;
import java.awt.event.*;

/*
 * This class follows a general loop structure as seen below to run the game:
 * 
 *			 initialShooterPicker
 *			 		|
 *			 pickedShooterGUIChange <-------
 *			 		|						|
 *			 getShooterBet					|
 * 					|						|
 * 			assignBet <---------			|
 *			 		|			|			| 
 *			 getOpponentBets ---			|
 *			 		|						|
 *	 ------- runPass						|
 * 	|				|						|
 *	|		 keepOrPass						|
 * 	|				|						|
 * 	|		turnChoice ---------------------
 * 	|				
 * 	 ---->  winnerWinnerChickenDinner -------> celebrationListener
 * 
 * getOpponentBets and assignBets call each other repeatedly until 
 * 		getOpponentBets loops back to the shooter's index (at which point it goes to runPass)
 * 
 * runPass ONLY goes to winnerWinnerChickenDinner if there is... you guessed it... a winner
 * 
 * 
 */
@SuppressWarnings("serial")
public class GUI extends JFrame {

	JButton instructions, about;
	Color background = Color.WHITE;
	ArrayList<JPanel> playerIcons = new ArrayList<JPanel>();
	JLabel playerName, playerBalance, playerBet, inputLabel;
	JPanel playersAndMenus, players, player, menus, main, inputPanel;
	JTextPane textOutput;
	JTextField inputField;

	int shooterID;

	String displayText;

	int nextPlayer;

	// timer for the shooter picker
	Timer timer;
	boolean untimed = false;

	int bet, remainingAction, actionAmount, shooterAction;

	int frameW = 800, frameH = 500;

	int delay = 0, timerCounter = 0, loops;

	int roll;

	Color[] confetti = { Color.PINK, Color.YELLOW, Color.GREEN, Color.CYAN };
	int colorTracker = 0;

	// used for inputListener (allows the one listener to branch into many results)
	enum InputState {
		SHOOTER_BET, OPPONENT_BET, TURN_CHOICE, LOST_ROUND
	}

	// inputListener assistant variables
	private InputState inputState;
	private int listenerPlayerID;

	// a sort of listener "hub"... based on the InputState, it calls the appropriate
	// method
	private void inputListener(Game game) {
		
		if(inputState == null) {
			return;
		}

		switch (inputState) {

		case SHOOTER_BET:
			assignBet(game, shooterID);
			break;

		case OPPONENT_BET:
			assignBet(game, listenerPlayerID);
			break;

		case TURN_CHOICE:
			turnChoice(game);
			break;

		case LOST_ROUND:
			lossDelay(game);
			break;
		}
	}

	// detects when a button is pressed
	private void buttonListener(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();
		// if the instruction button was pressed --> print instructions
		if (clicked == instructions) {
			String rules = "OK, HERE ARE THE BASIC RULES OF THE GAME OF CRAPS:\n\n One round of play with one person shooting the dice is called a 'pass'.\n\n1) The person with the dice is the 'shooter'. The shooter decides how much they want to bet, which is called the 'action'.\n\n2) The shooter must bet at least $10, and the bet must be a multiple of $10. They may bet up to the amount in their bank roll.\n\n3) The other players may 'take a piece of the action', meaning they can bet against the shooter up to the action amount. For example, if the action is $50, players might take $30, $10, and $10 until the full $50 is covered. Once the action is covered, no more bets are allowed for that pass.\n\nAfter betting is finished, the shooter rolls the dice for the 'come out roll'. Possible outcomes are:\n\n\t1) Rolling 7 or 11 is called a 'natural'. The shooter wins and collects the opponents' bets.\n\n\t2) Rolling 2 ('snake-eyes'), 3 ('ace-deuce'), or 12 ('box-cars') means the shooter 'craps out' and loses the pass.\n\n\t3) Rolling 4, 5, 6, 8, 9, or 10 establishes a 'point'. The shooter continues rolling until they either roll the point again (win) or roll a 7 (lose).\n\n\t4) After a pass ends, a winning shooter may roll again or pass the dice to the next player. If the shooter loses, the dice automatically pass to the next player.\n\n\t5) If a player loses all their money they are 'cleaned out' and are out of the game. The game ends when one player has all the money ('cleaned house').";

			// creating a scroll pane (the rules text is way too long and would clip off the
			// screen)
			JTextArea textArea = new JTextArea(rules);
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setEditable(false);

			JScrollPane scrollPane = new JScrollPane(textArea);
			scrollPane.setPreferredSize(new Dimension(1000, 400));

			JOptionPane.showMessageDialog(this, scrollPane, "Instructions", JOptionPane.INFORMATION_MESSAGE);
		}

		// if the about button was pressed --> print the program name and coder name
		else if (clicked == about) {
			JOptionPane.showMessageDialog(this, "Alleyway Craps by Christopher Nesbitt");
		}
	}

	// validates the bet based on the player given (used in shooter and opponent
	// bets)
	private void assignBet(Game game, int player) {
		bet = 0;

		// if there is remainingAction (ie. they aren't the shooter) the maximum bet is
		// the lesser of the remainingAction and their bankBalance
		// if there isn't remainingAction (ie. they are the shooter) the maximum bet is
		// their bank balance

		final int MAXIMUM_BET = remainingAction > 0
				? Math.min(remainingAction, game.getPlayerList().get(player).getBankBalance())
				: game.getPlayerList().get(player).getBankBalance();

		// minimum bet is always 10
		final int MINIMUM_BET = 10;

		// same as in StartGUI... makes sure they enter an int and not some other
		// character/string
		// all failed requirements send a unique error message to the user
		try {
			bet = Integer.parseInt(inputField.getText().trim());

			// makes sure the bet is a multiple of $10
			if (bet % MINIMUM_BET != 0) {
				textOutput.setText("Error: Not Multiple of 10 --> Please enter a valid bet amount (" + MINIMUM_BET + "-"
						+ MAXIMUM_BET + ")");

			}

			// makes sure the bet is >= $10
			else if (bet < MINIMUM_BET) {
				textOutput.setText("Error: Below Minimum Bet --> Please enter a valid bet amount (" + MINIMUM_BET + "-"
						+ MAXIMUM_BET + ")");
			}

			// makes sure the bet is less than the max (as determined earlier)
			else if (bet > MAXIMUM_BET) {
				textOutput.setText("Error: Above Maximum Bet --> Please enter a valid bet amount (" + MINIMUM_BET + "-"
						+ MAXIMUM_BET + ")");
			}

			// the bet is acceptable
			else {
				// updating their balance in the playerList and updating their icon
				game.getPlayerList().get(player).setBetAmount(bet);
				((JLabel) playerIcons.get(player).getComponent(2)).setText("Bet: $" + bet);

				// set it as the remaining action if the player was the shooter
				if (player == shooterID) {
					shooterAction = bet;
					remainingAction = bet;
				}

				// otherwise subtract it from the remaining action
				else {
					remainingAction -= bet;
				}

				getOpponentBets(game, player);
			}

			// Checks to make sure the user can at least read enough to enter an integer
		} catch (NumberFormatException err) {
			textOutput.setText(
					"Error: Invalid Bet --> Please enter a valid bet amount (" + MINIMUM_BET + "-" + MAXIMUM_BET + ")");
		}

		// resets the textField and redraws the frame
		inputField.setText("");
		this.repaint();
	}

	// the implementation of shootOrPass from the Pass class
	// if the user won, it asks if they'd like to shoot again or pass
	private void turnChoice(Game game) {

		try {
			char choice = inputField.getText().toUpperCase().charAt(0);

			inputField.setText("");

			// they want to shoot again
			if (choice == 'Y') {
				pickedShooterGUIChange(game);

			}

			// they want to pass
			else if (choice == 'N') {
				shooterID = nextPlayer(game, shooterID);
				pickedShooterGUIChange(game);

			}

			// they entered some nonsense
			else {
				textOutput.setText(
						"Error: Not Y/N --> Please enter a valid option \nWould you like to shoot again? (Y/N)");
			}
		}

		// in case they just pressed enter without typing anything
		catch (StringIndexOutOfBoundsException e) {
			textOutput.setText(
					"Error: No Character Detected --> Please enter a valid option \nWould you like to shoot again? (Y/N)");
		}

		// redrawing the frame
		this.repaint();
	}

	// this method only exists so that the player has to press enter to move to the
	// next turn --> that way it actually shows the result of their dice rolling
	private void lossDelay(Game game) {
		inputField.setText("");
		
		if(game.checkForGameWinner()) {
			winnerWinnerChickenDinner(game);
			return;
		}
		
		pickedShooterGUIChange(game);
	}

	private void celebrationListener(ActionEvent e, int winnerIndex) {
		
		for(JPanel player : playerIcons) {
			player.setBorder(null);
		}
		
		colorTracker = (colorTracker + 1) % confetti.length;
		playerIcons.get(winnerIndex).setBackground(confetti[colorTracker]);
		this.repaint();
	}

	// a fun little visual display of it randomly picking a starting shooter
	private void initialShooterPicker(Game game, ActionEvent e) {

		// moves through the list
		int i = timerCounter % game.getPlayerList().size();

		// text for the next line
		displayText = game.getPlayerList().get(i).getName();

		// shows who is currently being chosen (swaps rapidly)
		textOutput.setText("Picking the starting shooter!\n\n" + displayText);

		// slowly making the timer take longer between
		// --> looks like the picker is slowing down (kind of like a spinner wheel)
		delay += 2;

		timerCounter++;

		// redrawing the frame
		this.repaint();

		// Stopping the timer
		if (timerCounter == loops) {
			timer.stop();

			// setting the chosen shooter
			shooterID = i;

			// moving forward in the loop
			pickedShooterGUIChange(game);
			return;

		} else {
			// updating the delay on the timer
			timer.setDelay(delay);
		}
	}

	// updates the GUI to clearly show who the shooter is
	private void pickedShooterGUIChange(Game game) {

		// changing the shooter to have a blue border
		playerIcons.get(shooterID).setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));

		// outputting who the first shooter is
		textOutput.setText("Our first shooter is: " + game.getPlayerList().get(shooterID).getName());
		this.repaint();

		// moving forward in the loop
		getShooterBet(game);
		return;
	}

	// the shooter makes a bet
	private void getShooterBet(Game game) {
		// used in the "hub" as detailed earlier
		inputState = InputState.SHOOTER_BET;
		listenerPlayerID = shooterID;

		// resetting any variables from the previous pass
		remainingAction = 0;
		shooterAction = 0;

		// asking the player to bet
		textOutput.setText("Alright " + game.getPlayerList().get(shooterID).getName()
				+ "! You're the shooter! \n\n How much would you like to bet? \n(The bet must be a multiple of $10, with a minimum bet of $10 and maximum bet of $"
				+ game.getPlayerList().get(shooterID).getBankBalance() + ")");
	}

	// gets the bets from the non-shooters
	private void getOpponentBets(Game game, int player) {

		// picks the next player
		nextPlayer = nextPlayer(game, player);

		// used in the "hub" as detailed earlier
		inputState = InputState.OPPONENT_BET;
		listenerPlayerID = nextPlayer;

		// stops the cycle if either all the action is covered or it has looped back to
		// the shooter
		if (nextPlayer == shooterID || remainingAction == 0) {
			runPass(game); // moving forward in the loop
			return;
		}

		// resetting all the borders
		for (JPanel p : playerIcons) {
			p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		}

		// drawing the colored borders --> blue for shooter, orange for current better
		playerIcons.get(shooterID).setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
		playerIcons.get(nextPlayer).setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));

		// prompting the player for a bet
		textOutput.setText("Alright " + game.getPlayerList().get(nextPlayer).getName()
				+ "! Your turn to get a piece of the action!  \n\n How much would you like to bet? (The current uncovered action is $"
				+ remainingAction
				+ ") \n(The bet must be a multiple of $10, with a minimum bet of $10 and maximum bet of $"
				+ remainingAction + ")");
	}

	// the main part of the game now that players have made their bet
	private void runPass(Game game) {
		// getting the actionAmount for creating the pass
		actionAmount = game.getPlayerList().get(shooterID).getBetAmount();

		// creating a pass for settleBets
		Pass pass = new Pass(shooterID, actionAmount, actionAmount - remainingAction);

		// creating a die and rolling it twice for the total roll
		Die die = new Die();
		roll = die.rollDice() + die.rollDice();

		// determining the result of the roll and setting shooterWinOrLose accordingly
		switch (roll) {
		// rolled a natural -> WIN
		case 7:
		case 11:
			textOutput.setText("Congratulations, you rolled a natural! (" + roll + ")");
			pass.setShooterWinOrLose(true);
			break;

		// crapped out -> LOSS
		case 2:
		case 3:
		case 12:
			textOutput.setText("Bad luck, you crapped out! (" + roll + ")");
			pass.setShooterWinOrLose(false);
			break;

		// needs to roll point
		default:
			int point = roll;
			boolean resolved = false;
			textOutput.setText("Time to roll point! Your point is " + point + "\nYour rolls are:");
			while (!resolved) {
				roll = die.rollDice() + die.rollDice();
				textOutput.setText(textOutput.getText() + " " + roll);

				// failed to roll point
				if (roll == 7) {
					textOutput.setText(textOutput.getText() + "\nLooks like you rolled a 7... you lose!");
					resolved = true;
					pass.setShooterWinOrLose(false);
				}

				// rolled point
				else if (roll == point) {
					textOutput.setText(textOutput.getText() + "\nLooks like you rolled your point... you win!");
					resolved = true;
					pass.setShooterWinOrLose(true);
				}
			}
			break;
		}

		// settling the bets
		pass.settleBets(game.getPlayerList());

		// resetting important bet variables
		remainingAction = 0;
		actionAmount = 0;

		// traversing the playerIcons and playerList arrayLists in one loop
		for (int i = 0; i < playerIcons.size(); i++) {

			// resetting all bets to 0
			game.getPlayerList().get(i).setBetAmount(0);

			// resetting borders
			playerIcons.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK));

			// checking bankruptcy
			if (game.getPlayerList().get(i).getBankBalance() == 0) {
				playerIcons.get(i).setBackground(Color.RED);
				game.getPlayerList().get(i).setBankrupt(true);
			}

			// updating each players balance on the GUI
			((JLabel) playerIcons.get(i).getComponents()[1])
					.setText("Bal: $" + game.getPlayerList().get(i).getBankBalance());
			((JLabel) playerIcons.get(i).getComponents()[2])
					.setText("Bet: $" + game.getPlayerList().get(i).getBetAmount());
		}

		// moving forward in the loop
		keepOrPass(game, pass);
		return;
	}

	// deciding the next shoort
	public void keepOrPass(Game game, Pass pass) {
		
		// checking the opposite of bankruptcy
		if (game.checkForGameWinner()) {
		    inputState = InputState.LOST_ROUND;
		    textOutput.setText(textOutput.getText() + "\nThe game is over! Press Enter to see the final results.");
		    return;
		}

		// used for the "hub" as detailed above
		inputState = InputState.TURN_CHOICE;

		// if they won they get a chouce
		if (pass.getShooterWinOrLose()) {
			textOutput.setText(textOutput.getText() + "\n Would you like to shoot again? (Y/N)");

		}

		// if they lost they don't
		else {
			shooterID = nextPlayer(game, shooterID);
			inputField.setText("");
			textOutput.setText(textOutput.getText() + "\n Press enter to continue");

			// used for the "hub" as detailed above
			inputState = InputState.LOST_ROUND;
		}
	}

	// finds the next player --> wraps when needed and ignores bankrupt players
	public int nextPlayer(Game game, int player) {
		int next = (player + 1) % game.getPlayerList().size();
		while (game.getPlayerList().get(next).isBankrupt()) { // no brokies
			next = (next + 1) % game.getPlayerList().size();
		}
		return next;
	}

	// the big celebration for our high roller!
	public void winnerWinnerChickenDinner(Game game) {
		
		textOutput.setText("Congratulations " + game.getWinner() + "!\n YOU WON THE GAME!");
		inputField.setText("");

		for (int i = 0; i < game.getPlayerList().size(); i++) {
			if (game.getPlayerList().get(i).getName() == game.getWinner()) {
				int winnerIndex = i;
				Timer celebrationTimer = new Timer(150, (ev) -> celebrationListener(ev, winnerIndex));
				celebrationTimer.start();
			}

			else {
				playerIcons.get(i).setBackground(Color.RED);
			}
			
			
		}
	}

	// the main game GUI
	public GUI(Game game) {

		// commonly used variable so just declaring it here
		int numPlayers = game.getPlayerList().size();

		// general setup
		this.setTitle("Alleyway Craps");
		this.setSize(frameW, frameH);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		main = new JPanel();
		main.setLayout(new GridLayout(2, 1));

		// the first row of the main frame
		playersAndMenus = new JPanel();
		playersAndMenus.setLayout(new GridBagLayout()); // left column is players, right are the 2 buttons
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.weighty = 1;
		
		// the left panel in playersAndMenus
		players = new JPanel();
		players.setLayout(new GridLayout(1, numPlayers)); // each player gets their own column

		// each player gets their name, and their bank balance as labels
		for (int i = 0; i < numPlayers; i++) {
			player = new JPanel();
			player.setLayout(new GridLayout(3, 1));
			playerName = new JLabel(game.getPlayerList().get(i).getName(), SwingConstants.CENTER);
			playerBalance = new JLabel("Bal: $" + game.getPlayerList().get(i).getBankBalance(), SwingConstants.CENTER);
			playerBet = new JLabel("Bet: $" + game.getPlayerList().get(i).getBetAmount(), SwingConstants.CENTER);

			// adding the labels to the frame
			player.add(playerName);
			player.add(playerBalance);
			player.add(playerBet);

			// making the border and background color
			// (will be changed later based on whose turn it is/if they are out)
			player.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			player.setBackground(background);

			// adding each player panel to an arraylist
			playerIcons.add(player);

			// adding the player frames to the players frame (container)
			players.add(player);
		}

		// the second (right column) half of the playerAndMenus panel
		menus = new JPanel();
		menus.setLayout(new GridLayout(2, 1));

		// instructions button with the general buttonlistener
		instructions = new JButton("Instructions");
		instructions.addActionListener((ev) -> buttonListener(ev));

		// about button with the general buttonlistener
		about = new JButton("About");
		about.addActionListener((ev) -> buttonListener(ev));
		menus.add(instructions);
		menus.add(about);

		// adding the 2 subpanels to the playersAndMenus panel
		gbc.gridx = 0;
		gbc.weightx = 2;   
		playersAndMenus.add(players, gbc); // 2/3 of the row
		
		gbc.gridx = 1;
		gbc.weightx = 1;   
		playersAndMenus.add(menus, gbc); // 1/3 of the row

		// The text that will be displayed to the players
		textOutput = new JTextPane();
		textOutput.setFont(new Font("Serif", Font.BOLD, 15));
		textOutput.setEditable(false);
		textOutput.setBorder(null);
		textOutput.setBackground(background);

		// The worlds most annoying way to get centered multi-line text
		StyledDocument doc = textOutput.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		// where the users enter text
		inputPanel = new JPanel();

		// making it clear there is a textField there
		inputLabel = new JLabel("Enter input (Omit $ for bets)", SwingConstants.CENTER);
		inputLabel.setPreferredSize(new Dimension(frameW, 20));

		// creating a field that when the user presses enter it sends an ActionEvent to
		// the "hub"
		// which then deals with it according to the InputState
		inputField = new JTextField();
		inputField.addActionListener((e) -> inputListener(game));

		// styling preferences
		inputField.setPreferredSize(new Dimension(frameW, 80));
		inputField.setFont(new Font("Serif", Font.BOLD, 15));
		inputField.setHorizontalAlignment(SwingConstants.CENTER);
		inputField.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		// adding the components to the panel
		inputPanel.add(inputLabel);
		inputPanel.add(inputField);
		inputPanel.setBackground(background);

		// adding the 2 main components to the panel
		main.add(playersAndMenus);
		main.add(textOutput);

		// adding the input and output panels to the frame
		this.add(main, BorderLayout.CENTER);
		this.add(inputPanel, BorderLayout.SOUTH);

		// displaying the frame
		this.setVisible(true);

		// for the timer (it loops somewhere between 50-75 times to ensure the starting
		// shooter is actually random)
		Random rand = new Random();
		loops = rand.nextInt(50, 75);

		// launching the timer
		timer = new Timer(delay, (ev) -> initialShooterPicker(game, ev));
		timer.start();
	}

}

// End of class