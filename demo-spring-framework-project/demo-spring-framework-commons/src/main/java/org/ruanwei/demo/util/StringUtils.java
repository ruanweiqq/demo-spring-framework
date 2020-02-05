package org.ruanwei.demo.util;

import java.util.Iterator;

public class StringUtils {
	public static void main(String[] args) {

	}

	public static boolean isDigit(String input) {
		if (input != null) {
			return input.matches("[+-]?\\d+");
		}
		return false;
	}

	public static <T> String toString(Iterable<T> ids) {
		if (ids == null)
			return "null";

		StringBuilder b = new StringBuilder();

		Iterator<T> iterator = ids.iterator();
		if (!iterator.hasNext()) {
			return "";
		}
		b.append(iterator.next());

		while (iterator.hasNext()) {
			b.append(", ");
			b.append(iterator.next());
		}
		return b.toString();
	}
}
