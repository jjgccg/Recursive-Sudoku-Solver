import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SudokuSolver {
	private int[][] myClue;
	private int[][] mySolution;
	/** Symbol used to indicate a blank grid position */
	public static final int BLANK = 0;
	/** Overall size of the grid */
	public static final int DIMENSION = 9;
	/** Size of a sub region */
	public static final int REGION_DIM = 3;

	// For debugging purposes -- see solve() skeleton.
	private Scanner kbd;
	private static final boolean DEBUG = false;

	/**
	 * Run the solver. If args.length >= 1, use args[0] as the name of a file
	 * containing a puzzle, otherwise, allow the user to browse for a file.
	 */
	public static void main(String[] args) {
		String filename = null;
		if (args.length < 1) {
			// file dialog
			// filename = args[0];
			JFileChooser fileChooser = new JFileChooser();
			try {
				File f = new File(new File(".").getCanonicalPath());
				fileChooser.setCurrentDirectory(f);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}

			int retValue = fileChooser.showOpenDialog(new JFrame());

			if (retValue == JFileChooser.APPROVE_OPTION) {
				File theFile = fileChooser.getSelectedFile();
				filename = theFile.getAbsolutePath();
			} else {
				System.out.println("No file selected: exiting.");
				System.exit(0);
			}
		} else {
			filename = args[0];
		}

		SudokuSolver s = new SudokuSolver(filename);
		if (DEBUG)
			s.print();

		if (s.solve(0, 0)) {
			// Pop up a window with the clue and the solution.
			s.display();
		} else {
			System.out.println("No solution is possible.");
		}

	}

	/**
	 * Create a solver given the name of a file containing a puzzle. We expect
	 * the file to contain nine lines each containing nine digits separated by
	 * whitespace. A digit from {1...9} represents a given value in the clue,
	 * and the digit 0 indicates a position that is blank in the initial puzzle.
	 */
	public SudokuSolver(String puzzleName) {
		myClue = new int[DIMENSION][DIMENSION];
		mySolution = new int[DIMENSION][DIMENSION];
		// Set up keyboard input if we need it for debugging.
		if (DEBUG)
			kbd = new Scanner(System.in);

		File pf = new File(puzzleName);
		Scanner s = null;
		try {
			s = new Scanner(pf);
		} catch (FileNotFoundException f) {
			System.out.println("Couldn't open file.");
			System.exit(1);
		}

		for (int i = 0; i < DIMENSION; i++) {
			for (int j = 0; j < DIMENSION; j++) {
				myClue[i][j] = s.nextInt();
			}
		}

		// Copy to solution
		for (int i = 0; i < DIMENSION; i++) {
			for (int j = 0; j < DIMENSION; j++) {
				mySolution[i][j] = myClue[i][j];
			}
		}
	}

	/**
	 * Starting at a given grid position, generate values for all remaining grid
	 * positions that do not violate the game constraints of Sudoku.  If a value works for 
	 * a position in the grid and a solution is found, then the method returns true.  If no
	 * solution is found, the method returns false.
	 *
	 * @param row The row of the position to begin with
	 * @param col The column of the position to begin with.
	 * @return true if a solution was found that works for specific position,
	 *         and false if not.
	 */
	public boolean solve(int row, int col) {
		// This code will print the solution array and then wait for
		// you to type "Enter" before proceeding. Helpful for debugging.
		// Set the DEBUG constant to true at the top of the class
		// declaration to turn this on.
		if (DEBUG) {
			System.out.println("solve(" + row + ", " + col + ")");
			print();
			kbd.nextLine();
		}

		int[] possibleNumbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };//array of potential numbers that work for a given position

		if (row == DIMENSION) { // base case, when the end of the grid is reached
			return true;

		} else if (myClue[row][col] != BLANK) { // there is a number already here, continue on 
			if (row < DIMENSION) {

				if (col < DIMENSION - 1) {

					return solve(row, col + 1);

				} else {

					return solve(row + 1, 0);

				}

			}

		} else { // cell is blank - find number that works for this location

			for (int i = 0; i < possibleNumbers.length; i++) { // check all
																// values from
																// the array
				boolean conditionCheck = allConditionsMet(row, col, possibleNumbers[i]); //makes sure all conditions are met before placing value

				if (conditionCheck == true) { // all conditions were met
					mySolution[row][col] = possibleNumbers[i];

					if (col < DIMENSION - 1) { // solve recursively column by column

						if (solve(row, col + 1) != true) {

							mySolution[row][col] = BLANK; //number didn't work for this location, backtrack and set to BLANK

						} else {

							return true;
						}
					} else { // reached end of column, continue solving through
								// next row etc

						if (solve(row + 1, 0) != true) {

							mySolution[row][col] = BLANK;

						} else {

							return true;
						}

					}

				}

			}

		}
		return false; // no solution was found

	}

	/**
	 * Print a character-based representation of the solution array on standard
	 * output.
	 */
	public void print() {
		System.out.println("+---------+---------+---------+");
		for (int i = 0; i < DIMENSION; i++) {
			System.out.println("|         |         |         |");
			System.out.print("|");
			for (int j = 0; j < DIMENSION; j++) {
				System.out.print(" " + mySolution[i][j] + " ");
				if (j % REGION_DIM == (REGION_DIM - 1)) {
					System.out.print("|");
				}
			}
			System.out.println();
			if (i % REGION_DIM == (REGION_DIM - 1)) {
				System.out.println("|         |         |         |");
				System.out.println("+---------+---------+---------+");
			}
		}
	}

	/**
	 * Pop up a window containing a nice representation of the original puzzle
	 * and out solution.
	 */
	public void display() {
		JFrame f = new DisplayFrame();
		f.pack();
		f.setVisible(true);
	}

	/**
	 * GUI display for the clue and solution arrays.
	 */
	private class DisplayFrame extends JFrame implements ActionListener {
		private JPanel mainPanel;

		private DisplayFrame() {
			mainPanel = new JPanel();
			mainPanel.add(buildBoardPanel(myClue, "Clue"));
			mainPanel.add(buildBoardPanel(mySolution, "Solution"));
			add(mainPanel, BorderLayout.CENTER);

			JButton b = new JButton("Quit");
			b.addActionListener(this);
			add(b, BorderLayout.SOUTH);
		}

		private JPanel buildBoardPanel(int[][] contents, String label) {
			JPanel holder = new JPanel();
			JLabel l = new JLabel(label);
			BorderLayout b = new BorderLayout();
			holder.setLayout(b);
			holder.add(l, BorderLayout.NORTH);
			JPanel board = new JPanel();
			GridLayout g = new GridLayout(9, 9);
			g.setHgap(0);
			g.setVgap(0);
			board.setLayout(g);
			Color[] colorChoices = new Color[2];
			colorChoices[0] = Color.WHITE;
			colorChoices[1] = Color.lightGray;
			int colorIdx = 0;
			int rowStartColorIdx = 0;

			for (int i = 0; i < DIMENSION; i++) {
				if (i > 0 && i % REGION_DIM == 0)
					rowStartColorIdx = (rowStartColorIdx + 1) % 2;
				colorIdx = rowStartColorIdx;
				for (int j = 0; j < DIMENSION; j++) {
					if (j > 0 && j % REGION_DIM == 0)
						colorIdx = (colorIdx + 1) % 2;
					JTextField t = new JTextField("" + contents[i][j]);
					if (contents[i][j] == 0)
						t.setText("");
					t.setPreferredSize(new Dimension(35, 35));
					t.setEditable(false);
					t.setHorizontalAlignment(JTextField.CENTER);
					t.setBackground(colorChoices[colorIdx]);
					board.add(t);
				}
			}
			holder.add(board, BorderLayout.CENTER);
			return holder;
		}

		public void actionPerformed(ActionEvent e) {
			System.exit(0);
		}
	}

	/**
	 * Checks to see if the given number passed in is inside of a certain row in
	 * the Sudoku puzzle, and returns true if it is found that the particular
	 * number is in the row and false if it is not.
	 *
	 * @param row the row index to start with
	 * @param column the index of the column to start with
	 * @param element the number to find inside of the row
	 * @return true if the element is found in the row and false if it is not
	 */
	private boolean isInRow(int row, int column, int element) {
		for (int i = 0; i < DIMENSION; i++) {

			if (mySolution[i][column] == element) { // keeps column constant while looking through available rows
				return true;

			}
			
		}
		return false; // is not in the row - return false

	}

	/**
	 * Checks to see if the given number passed in is inside of a certain column in
	 * the Sudoku puzzle, and returns true if it is found that the particular
	 * number is in the column and false if it is not.
	 *
	 * @param row the row index to start with
	 * @param column the column index to start with
	 * @param element the number to find inside of the column
	 * @return true if the element is found in the column and false if it is not
	 */
	private boolean isInColumn(int row, int column, int element) {
		for (int i = 0; i < DIMENSION; i++) {

			if (mySolution[row][i] == element) { //keeps row constant while searching through columns
				return true;

			}

		}

		return false; //it is not in the column, return false

	}

	/**
	 * Checks if the number of interest is anywhere in a grid box of varied size. If
	 * the element is indeed contained in here, the method returns true, and if
	 * not, returns false.
	 *
	 * @param row the current row location index in the puzzle
	 * @param column the current column location index in the puzzle
	 * @param element the number to find inside of a square grid on the puzzle
	 * @return true if the number is found to exist in the square grid and false if it is not
	 */
	private boolean isInBox(int row, int column, int element) {

		// gives you the row and column associated with the start of a square
		// grid sub portion of the puzzle
		int tempRow = (row / REGION_DIM) * REGION_DIM;
		int tempCol = (column / REGION_DIM) * REGION_DIM;

		//for loop goes through the square box, checking each index to see if it has the element
		for (int i = 0; i < REGION_DIM; i++) {
			for (int j = 0; j < REGION_DIM; j++) {
				if (mySolution[i + tempRow][j + tempCol] == element) {
					return true; // element was found in the box

				}

			}

		}

		return false; //element was not found in the box

	}

	/**
	 * Checks to see that the integer to be placed in the puzzle is not in the
	 * row, column, or square box sub-portion of the associated location, and returns true if all of these
	 * conditions are met and false otherwise - i.e. returns true if a number is a viable solution
	 * to a specific location in the puzzle and false if it is not.
	 *
	 * @param row the current row in the puzzle
	 * @param col the current column in the puzzle
	 * @param element the number that will be checked to see if it works in the specific location
	 * @return true if the number works in the location in the puzzle, and false if it does not work
	 */
	private boolean allConditionsMet(int row, int col, int element) {
		//series of booleans call other private helper methods to chack all conditions
		boolean inRow = isInRow(row, col, element);
		boolean inCol = isInColumn(row, col, element);
		boolean inBox = isInBox(row, col, element);

		if (inRow == false && inCol == false && inBox == false) {
			return true; // all conditions met, okay to place number
		}

		return false; // one condition not met, must use a different number

	}

}