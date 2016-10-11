package com.potkanburger.sudoku_durif_genoux_hu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import android.util.Log;

/**
 * Some functions to generate and check Sudoku grids, with naive implementation.
 * 
 * This is not optimal, and we do not want to, for the purpose of the exercise.
 * 
 * @author Sylvain Veyrié - Key Consulting
 * 
 */
public class Sudoku {

	private static final Byte[][] EXAMPLE = new Byte[][] {
			{ 1, null, null, null, 3, 7, null, null, null },
			{ null, null, 6, null, null, null, null, null, null },
			{ null, null, 8, null, 6, null, null, 2, 3 },
			{ null, null, null, null, null, null, null, null, 5 },
			{ 9, 4, null, 8, null, null, 7, null, null },
			{ null, 5, null, null, 9, 4, null, 8, null },
			{ null, 7, 9, null, null, null, null, null, null },
			{ null, null, null, null, null, null, 4, null, 1 },
			{ null, null, 3, null, 8, null, null, null, null }, };

	/**
	 * Checks if the given sudoku grid is valid.
	 * 
	 * @param g
	 *            grid (may not be full)
	 * @return true if valid (for the non-null elements)
	 */
	public static Collection<Coord> checkGrid(SudokuGrid g) {
		final Collection<Coord> inConflit = new HashSet<Coord>();
		for (byte row = 0; row < SudokuGrid.SIZE; row++) {
			for (byte cell = 0; cell < SudokuGrid.SIZE; cell++) {
				inConflit.addAll(checkValue(g, row, cell));
			}
		}

		return inConflit;
	}

	/**
	 * Check if the given cell is valid (has no direct conflicts with other
	 * cells), given sudoku rules.
	 * 
	 * @param g
	 *            grid
	 * @param row
	 *            row of the cell
	 * @param cell
	 *            column of the cell
	 * @return list of coordinates of cell in conflict with it
	 */
	public static Collection<Coord> checkValue(SudokuGrid g, byte row, byte cell) {
		return checkValue(g, row, cell, g.get(row, cell));
	}

	/**
	 * Check if the given cell is valid (has no direct conflicts with other
	 * cells), given sudoku rules, if it contains the given value
	 * 
	 * @param g
	 *            grid
	 * @param row
	 *            row of the cell
	 * @param cell
	 *            column of the cell
	 * @param value
	 *            the target value of the cell
	 * @return list of coordinates of cell in conflict with it
	 */
	public static Collection<Coord> checkValue(SudokuGrid g, byte row,
			byte cell, Element target) {

		final Collection<Coord> inConflit = new ArrayList<Coord>();

		// ignore nulls
		if (Element.isNull(target)) {
			return inConflit;
		}

		// same row
		for (byte cellNum = 0; cellNum < SudokuGrid.SIZE; cellNum++) {
			if (cellNum != cell) {
				if (target.equals(g.get(row, cellNum))) {
					inConflit.add(new Coord(row, cellNum));
				}
			}
		}

		// same column
		for (byte rowNum = 0; rowNum < SudokuGrid.SIZE; rowNum++) {
			if (rowNum != row) {
				if (target.equals(g.get(rowNum, cell))) {
					inConflit.add(new Coord(rowNum, cell));
				}
			}
		}

		// same square
		{
			final byte firstRow = getFirstFromBlockOf3(row);
			final byte firstCell = getFirstFromBlockOf3(cell);
			for (byte rowNum = firstRow; rowNum < firstRow + 3; rowNum++) {
				for (byte cellNum = firstCell; cellNum < firstCell + 3; cellNum++) {
					if (rowNum != row || cellNum != cell) {
						if (target.equals(g.get(rowNum, cellNum))) {
							inConflit.add(new Coord(rowNum, cellNum));
						}
					}
				}
			}
		}

		return inConflit;
	}

	private static byte getFirstFromBlockOf3(byte b) {
		byte div = (byte) (b / 3);
		return (byte) (div * 3);
	}

	/**
	 * Returns an example grid.
	 * 
	 * @return example grid.
	 */
	public static SudokuGrid getExampleGrid() {
		return SudokuGrid.from(EXAMPLE);
	}

	/**
	 * Generates a valid sudoku grid. THIS IS SLOW.
	 * 
	 * @return a valid grid
	 */
	public static SudokuGrid generateValidGrid() {
		// naive algorithm
		SudokuGrid g;
		do {
			Log.d("Sudoku", "Try to generate grid!");
			g = generateGrid(28);
		} while (!checkGrid(g).isEmpty());
		Log.d("Sudoku", "Grid ok!");
		return g;
	}

	private static SudokuGrid generateGrid(int numberOfValues) {
		final Random ran = new Random(System.currentTimeMillis());

		final SudokuGrid grid = new SudokuGrid();

		for (int i = 0; i < numberOfValues; i++) {
			grid.set(randomValue(ran), randomValue(ran), randomElement(ran));
		}

		return grid;
	}

	private static byte randomValue(Random ran) {

		return (byte) ran.nextInt(SudokuGrid.SIZE);

	}

	private static Element randomElement(Random ran) {

		return Element.from(randomValue(ran));

	}

	/**
	 * Sudoku coordinates.
	 * 
	 */
	public static class Coord {
		public Coord(byte row, byte cell) {
			this.row = row;
			this.cell = cell;
		}

		public byte row;
		public byte cell;

		public String toString() {
			return "" + row + " - " + cell;
		}
	}

}
