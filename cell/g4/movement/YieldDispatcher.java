package cell.g4.movement;

import java.util.Random;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;

public class YieldDispatcher {
	private MinCostYieldMove mincost;
	
	private Random rnd = new Random();
	
	public YieldDispatcher(Board board, Sack sack, Player player) {
		mincost = new MinCostYieldMove(board, sack, player);
	}
	
	public YieldMove pickYieldMove() {
		return mincost;
	}
}
