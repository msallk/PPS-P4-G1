package cell.g5;

public class Maths {
	/* Untested */
	public static int[] scale(int[] a, int n) {
		int[] ret = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			ret[i] = n * a[i];
		}
		return ret;
	}
}
