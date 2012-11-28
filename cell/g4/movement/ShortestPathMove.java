package cell.g4.movement;

import java.util.List;

import cell.g4.Board;
import cell.g4.Sack;
import cell.sim.Player.Direction;

public class ShortestPathMove extends MoveAlgo {
	
	public ShortestPathMove(Board board, Sack sack) {
		super(board, sack);
	}
	
	@Override
	public Direction move(int[] location, int[][] players, int[][] traders) {
		
		assert(board != null);
		
		int nearestTrader = findNearestTrader(location, traders);
		
		List<Direction> dirs = board.nextMove(location, traders[nearestTrader]);
		
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
	

	private int findNearestTrader(int[] location, int[][] traders) {
		int mindist = Integer.MAX_VALUE;
		int nearest = -1;
		for (int i = 0; i < traders.length; i++) {
			int dist = board.mindist(location, traders[i]);
			if (dist < mindist) {
				mindist = dist;
				nearest = i;
			}
		}
		return nearest;
	}
}
