package cell.g4;

import java.util.Arrays;

public class Sack {
	private int[] sacks;
	private int[] reserves = {5,5,5,5,5,5};
	
	public static int WinningStock = 0;
	
	public Sack(int[] sack) {
		WinningStock = sack[0] * 4;
		this.sacks = Arrays.copyOf(sack, sack.length);
	}
	
	public void update(int[] sack) {
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
