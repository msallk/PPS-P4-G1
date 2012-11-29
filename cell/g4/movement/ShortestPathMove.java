package cell.g4.movement;

import java.util.List;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;
import cell.sim.Player.Direction;

public class ShortestPathMove extends MoveAlgo {
	private TraderFinder traderFinder;	
	
	public ShortestPathMove(Board board, Sack sack) {
		super(board, sack);
		traderFinder = new ClosestTraderFinder(board);
	}
	
	@Override
	public Direction move(int[] location, int[][] players, int[][] traders) {
		
		assert(board != null);
		int nextTrader = traderFinder.findBestTrader(location, players, traders);
		
		List<Direction> dirs = board.nextMove(location, traders[nextTrader]);
		
		Direction dir = pickDir(location, dirs);
		
		return dir;
	}

	private Direction pickDir(int[] location, List<Direction> dirs) {
		int maxcolor = 0;
		Direction dir = null;
		for (Direction d : dirs) {
			int[] loc = board.nextLoc(location, d);
			if (sack.getStock(board.getColor(loc)) >= maxcolor) {
				maxcolor = board.getColor(loc);
				dir = d;
			}
		}
		return dir;
	}

}
