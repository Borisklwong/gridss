package au.edu.wehi.idsv.util;

import java.util.Arrays;

public class ArrayHelper {
	private static int[] EMPTY_ARRAY = new int[0];
	/**
	 * Adds the values from the second array to the first,
	 * reusing the first array if possible
	 * The result will be expanded to fit the maximum number of array elements in either array
	 */
	public static int[] add(int[] first, int[] second) {
		if (first == null && second == null) return EMPTY_ARRAY;
		if (second == null) return first;
		if (first == null) return Arrays.copyOf(second, second.length);
		if (first.length < second.length) {
			first = Arrays.copyOf(first, second.length);
		}
		int nOverlap = Math.min(first.length, second.length);
		for (int i = 0; i < nOverlap; i++) {
			first[i] += second[i];
		}
		return first;
	}
	/**
	 * Adds the values from the second array to the first,
	 * reusing the first array if possible
	 * The result will be expanded to fit the maximum number of array elements in either array
	 */
	public static int[] subtract(int[] first, int[] second) {
		if (first == null && second == null) return EMPTY_ARRAY;
		if (second == null) return first;
		if (first == null) first = new int[second.length];
		if (first.length < second.length) {
			first = Arrays.copyOf(first, second.length);
		}
		int nOverlap = Math.min(first.length, second.length);
		for (int i = 0; i < nOverlap; i++) {
			first[i] -= second[i];
		}
		return first;
	}
}
