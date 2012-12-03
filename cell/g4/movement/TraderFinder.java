package cell.g4.movement;

import cell.g4.Board;

public abstract class TraderFinder {
	protected Board board;
	
	public TraderFinder(Board board) {
		this.board = board;
	}
	
	public abstract int findBestTrader(int[] location, int[][] teams, int[][] traders);
}
