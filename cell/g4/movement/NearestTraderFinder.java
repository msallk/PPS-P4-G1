package cell.g4.movement;

import cell.g4.Board;
import cell.g4.movement.TraderFinder;

public class NearestTraderFinder extends TraderFinder {

	public NearestTraderFinder(Board board) {
		super(board);
	}

	@Override
	public int findBestTrader(int[] location, int[][] teams, int[][] traders) {
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