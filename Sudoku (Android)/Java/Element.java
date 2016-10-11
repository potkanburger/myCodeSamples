package com.potkanburger.sudoku_durif_genoux_hu;

import android.util.SparseArray;

/**
 * An element of a Sudoku grid, from 1 to 9, with NULL.
 * 
 * @author Sylvain Veyrié - Key Consulting
 * 
 */
public enum Element {

	NULL(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), HEIGHT(
			8), NINE(9);

	private final Byte num;

	public Byte getNum() {
		return num;
	}

	private static final SparseArray<Element> MAP_BYTE = new SparseArray<Element>(
			9);
	static {
		for (Element el : Element.values()) {
			MAP_BYTE.put(el.getNum(), el);
		}
	}

	private Element(int num) {
		this.num = (byte) num;
	}

	@Override
	public String toString() {
		return this == NULL ? "" : Byte.toString(this.num);
	}

	/**
	 * Gives the right element from the given byte.
	 * 
	 * @param b
	 *            byte
	 * @return element
	 */
	public static Element from(Byte b) {
		return b == null ? NULL : MAP_BYTE.get(b);
	}

	/**
	 * Gives the right element from the given string.
	 * 
	 * @param string
	 *            string
	 * @return element
	 */
	public static Element from(String string) {
		return string == null || "".equals(string) ? NULL : from(Byte
				.valueOf(string));
	}

	/**
	 * Checks if the element is null (Java "null" or Element "NULL").
	 * 
	 * @param e
	 *            element
	 * @return true if null
	 */
	public static boolean isNull(Element e) {
		return e == null || NULL.equals(e);
	}

}
