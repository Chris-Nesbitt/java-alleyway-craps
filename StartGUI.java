//Chris Nesbitt
//2026-04-10
//The starter GUI that gets the number of players and their names before launching the main GUI


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class StartGUI extends JFrame {

	int players;
	String[] playerNames;
	ArrayList<JTextField> nameBoxes = new ArrayList<JTextField>();
	JLabel request, nameResponseQuestion;
	JTextField response, nameResponseAnswer;
	JButton startGame;
	JPanel nameResponses, nameResponse;

	// What happens when the Start button is pressed
	private void startListener(ActionEvent e) {
		playerNames = new String[players];
		int counter = 0;
		int successes = 0;

		// for each of the name boxes
		for (JTextField box : nameBoxes) {

			String name = box.getText().trim();

			if (!name.equals("")) {
				boolean duplicate = false;

				// check previously entered names
				for (int i = 0; i < counter; i++) {
					if (playerNames[i] != null && playerNames[i].equals(name)) {
						duplicate = true;
						break;
					}
				}

				if (duplicate) {
					box.setBorder(BorderFactory.createLineBorder(Color.RED));
				}

				else {
					playerNames[counter] = name;
					successes++;
					box.setBorder(null);
				}
			}

			else {
				box.setBorder(BorderFactory.createLineBorder(Color.RED));
			}
			counter++;
		}

		// Creates the game, populates the Game's Player List, and opens the game gui
		// --> deletes this gui
		if (successes == players) {
			Game game = new Game();
			game.populatePlayerList(playerNames);
			new GUI(game);
			this.dispose();
		}

		// Resets the name array if a box was left empty
		else {
			playerNames = new String[0];
		}
	}

	private void textFieldListener(ActionEvent e) {

		// tries turning the text in the box into an int
		try {
			players = Integer.parseInt(response.getText().trim());

			// giving errors if the number is too big or too small
			if (players > 6) {
				request.setText("Error: Too Many Players --> Please enter a valid number of players (2-6)");
			} else if (players < 2) {
				request.setText("Error: Too Few Players --> Please enter a valid number of players (2-6)");
			} else {

				// if a successful number was entered it resets the frame
				this.remove(response);
				this.remove(request);

				// A panel of panels that contain 1 text field and a corresponding label for
				// each player
				nameResponses = new JPanel();

				// creating each panel for nameResponses and adding each text field to an
				// arraylist to use later
				for (int i = 0; i < players; i++) {
					nameResponse = new JPanel();
					nameResponse.setLayout(new GridLayout(2, 1));
					nameResponseQuestion = new JLabel("What is the name of player " + (i + 1));
					nameResponseAnswer = new JTextField();
					nameResponse.add(nameResponseQuestion);
					nameResponse.add(nameResponseAnswer);

					nameResponses.add(nameResponse);

					nameBoxes.add(nameResponseAnswer);

				}

				// adds a button that runs the StartListener method if pressed
				startGame = new JButton("Start Game");
				startGame.addActionListener((event) -> startListener(event));

				// adding panels to frame and repainting so they show up
				this.add(nameResponses);
				this.add(startGame);
				this.revalidate();
				this.repaint();

			}

		}

		// Returns an error if the text was not a valid number
		catch (NumberFormatException err) {
			request.setText("Error: Invalid Input --> Please enter a valid number of players (2-6)");
		}

		// refreshes the TextField after a wrong answer
		response.setText("");

	}

	public StartGUI() {
		this.setTitle("Alleyway Craps - Begin");
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new GridLayout(2, 1));

		request = new JLabel("Welcome to the Alleyway Casino. How many players do you have? (2-6)",
				SwingConstants.CENTER);
		response = new JTextField();

		// making the font typed in centered and large
		response.setFont(new Font("Serif", Font.BOLD, 40));
		response.setHorizontalAlignment(SwingConstants.CENTER);

		// When enter is pressed on the text field it runs the TextFieldListener method
		response.addActionListener((ev) -> textFieldListener(ev));

		// adding the panels
		this.add(request);
		this.add(response);

		this.setVisible(true);
	}

}

// End of class