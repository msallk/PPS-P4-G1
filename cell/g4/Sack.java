package cell.g4;

import java.util.Arrays;

public class Sack {
	protected int[] sacks;
	protected int[] reserves;
	protected int nTrader;
	protected int nPlayer;
	protected Board board;
	
	public static int InitialMarble = 0;
	public static int WinningStock = 0;
	
	public Sack(int[] sack, Board board, int nTrader, int nPlayer) {
		InitialMarble = sack[0];
		WinningStock = sack[0] * 4;
		this.sacks = Arrays.copyOf(sack, sack.length);
		this.board = board;
		
		double[] dist = board.getColorDistribution();
		
		reserves = new int[6];
		for (int i = 0; i < 6; i++)
			reserves[i] = (int) (dist[i] * board.dimension() / nTrader * Math.max(nPlayer, 16));
		
		this.nTrader = nTrader;
	}
	
	public void setReserves(int[] reserves) {
		this.reserves = reserves;
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
