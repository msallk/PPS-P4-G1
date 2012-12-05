package cell.g4.movement;

import cell.g4.Board;
import cell.g4.Player;

public abstract class TraderFinder {
	protected Player player;
	
	protected Board board;
	
	public TraderFinder(Board board, Player player) {
		this.board = board;
		this.player = player;
	}
	
	public abstract int findBestTrader(int[] location, int[][] teams, int[][] traders);
}
