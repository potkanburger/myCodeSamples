package com.potkanburger.sudoku_durif_genoux_hu;

import java.util.ArrayList;
import java.util.List;

/**
 * Sudoku grid.
 * 
 * @author Sylvain Veyrié - Key Consulting
 * 
 */
public class SudokuGrid {

	public final static byte SIZE = 9;

	private Element[][] grid = new Element[SIZE][SIZE];

	public SudokuGrid() {
		for (byte row = 0; row < SudokuGrid.SIZE; row++) {
			for (byte cell = 0; cell < SudokuGrid.SIZE; cell++) {
				set(row, cell, Element.NULL);
			}
		}
	}

	/**
	 * Gets the element given the coordinates.
	 * 
	 * @param numRow
	 *            row of the cell
	 * @param numCell
	 *            column of the cell
	 * @return value
	 */
	public Element get(byte numRow, byte numCell) {
		return grid[numRow][numCell];
	}

	/**
	 * Gets the element given the coordinates.
	 * 
	 * @param numRow
	 *            row of the cell
	 * @param numCell
	 *            column of the cell
	 * @param value
	 *            value to set to the cell
	 */
	public void set(byte numRow, byte numCell, Element value) {
		grid[numRow][numCell] = value;
	}

	/**
	 * Gives the right grid from the given 2D byte array.
	 * 
	 * @param tab
	 *            a 2D byte array, supposed to be 9x9
	 * @return a new grid
	 */
	public static SudokuGrid from(Byte[][] tab) {
		final SudokuGrid grid = new SudokuGrid();
		for (byte row = 0; row < SudokuGrid.SIZE; row++) {
			for (byte cell = 0; cell < SudokuGrid.SIZE; cell++) {
				grid.set(row, cell, Element.from(tab[row][cell]));
			}
		}
		return grid;
	}

	/**
	 * Gives the right grid from the given list.
	 * 
	 * @param tab
	 *            a list, supposed to have 81 elements
	 * @return a new grid
	 */
	public static SudokuGrid from(List<Element> list) {
		final SudokuGrid grid = new SudokuGrid();
		for (byte row = 0; row < SudokuGrid.SIZE; row++) {
			for (byte cell = 0; cell < SudokuGrid.SIZE; cell++) {
				grid.set(row, cell, list.get(getPosition(row, cell)));
			}
		}
		return grid;
	}

	/**
	 * Gives the 1D position of a cell given 2D coordinates
	 * 
	 * @param row
	 *            row of the cell
	 * @param cell
	 *            column of the cell
	 * @return 1D position in the grid
	 */
	public static int getPosition(byte row, byte cell) {
		return row * SIZE + cell;
	}

	/**
	 * Gives the row of a cell given its 1D position.
	 * 
	 * @param position
	 *            1D position
	 * @return row of the cell
	 */
	public static byte getRow(int position) {
		return (byte) (position / SIZE);
	}

	/**
	 * Gives the column of a cell given its 1D position.
	 * 
	 * @param position
	 *            1D position
	 * @return column of the cell
	 */
	public static byte getCell(int position) {
		return (byte) (position % SIZE);
	}

	/**
	 * Get a 1D list from the 2D grid.
	 * 
	 * @return list
	 */
	public List<Element> toList() {
		final List<Element> list = new ArrayList<Element>(SIZE * SIZE);
		for (byte row = 0; row < SudokuGrid.SIZE; row++) {
			for (byte cell = 0; cell < SudokuGrid.SIZE; cell++) {
				list.add(get(row, cell));
			}
		}

		return list;
	}
}
