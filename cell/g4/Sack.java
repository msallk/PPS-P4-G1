package cell.g4;

import java.util.Arrays;

public class Sack {
	protected int[] sacks;
	protected int[] reserves;
	protected Board board;
	
	protected final static int MinReserve = 3;
	protected final static double MinRatio = 0.2;
	
	public static int InitialMarble = 0;
	public static int WinningStock = 0;
	
	public Sack(int[] sack,Board board) {
		InitialMarble = sack[0];
		WinningStock = sack[0] * 4;
		this.sacks = Arrays.copyOf(sack, sack.length);
		this.board = board;
		
		reserves = new int[6];
		for (int i = 0; i < 6; i++)
			reserves[i] = MinReserve;
	}
	
	public void update(int[] sack, int[] loc) {
		this.sacks = Arrays.copyOf(sack, sack.length);
	}
	
	public int getStock(int color) {
		return sacks[color];
	}
	
	public int maxColor() {
		int maxcount = 0;
		int color = -1;
		for (int i = 0; i < sacks.length; i++) {
			if (sacks[i] >= maxcount) {
				maxcount = sacks[i];
				color = i;
			}
		}
		return color;
	}
	
	public int getReserve(int color) {
		return reserves[color];
	}
	
	public void decrease(int color) {
		sacks[color]--;
	}
}
