package cell.g4.trade;

import cell.g4.Board;
import cell.g4.Sack;

/**
 * An interface for trading algorithm
 */
public abstract class TradeAlgo {
	protected Sack sack = null;
	protected Board board = null;
	
	public TradeAlgo(Board board, Sack sack) {
		this.board = board;
		this.sack = sack;
	}
	
	public abstract void trade(double[] rate, int[] request, int[] give);
}
