package cell.g4;

public class Util {
	public static int[] copyI(int[] a) {
		int[] b = new int[a.length];
		for (int i = 0; i != a.length; ++i)
			b[i] = a[i];
		return b;
	}
	
	public static int[][] copyII(int[][] a) {
		int[][] b = new int[a.length][];
		for (int i = 0; i != a.length; ++i)
			b[i] = copyI(a[i]);
		return b;
	}
}
