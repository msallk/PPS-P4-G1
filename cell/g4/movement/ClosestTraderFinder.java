package cell.g4.movement;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.movement.TraderFinder;


/*
 *  in top 3 nearest traders, find a trader that we are the closest
 *  if we cannot find any, take the nearest one
 *  
 *  Tested against NearestTradeFinder, better
 */

/*
 * TODO: check if it is feasible to reach that trader
 */

public class ClosestTraderFinder extends TraderFinder {
	public final static int NoClosestTrader = 5050;
	private final static int topK = 3;
	
	public ClosestTraderFinder(Board board, Player player) {
		super(board, player);
	}
	
	protected boolean isClosest2(int[][] dists, int tid, int ourId) {
		int ourdist = dists[tid][ourId];
		for (int i = 0; i < dists[tid].length; i++) {
			if (i == ourId)
				continue;
			if (dists[tid][i] < ourdist && isClosest(dists, tid, i))
				return false;
		}
		return true;
	}
	
	protected boolean isClosest(int[][] dists, int tid, int ourId) {
		int ourdist = dists[tid][ourId];
		for (int i = 0; i < dists[tid].length; i++) {
			if (i == ourId)
				continue;
			
			if (dists[tid][i] < ourdist)
				return false;
		}
		return true;
	}
	
	protected void sort(int[] dists, int[] indices) {
		for (int i = 1; i < dists.length; i++) {
			int value = dists[i];
			int j = i - 1;
			while (j >=0 && dists[j] > value) {
				dists[j+1] = dists[j];
				indices[j+1] = indices[j];
				j--;
			}
			dists[j+1] = value;
			indices[j+1] = i;
		}
	}
	
	private boolean isConflict(int[][] dists, int tid, int ourId) {
		for (int i = 0; i < dists[tid].length; i++) {
			if (i == ourId)
				continue;
			if (dists[tid][i] == dists[tid][ourId])
				return true;
		}
		return false;
	}
	
	@Override
	public int findBestTrader(int[] location, int[][] teams, int[][] traders) {
		int[][] dists = new int[traders.length][teams.length];
		int[] ourdists = new int[traders.length];
		int[] indices = new int[traders.length];

		for (int i = 0; i < traders.length; i++) {
			indices[i] = i;
			for (int j = 0; j < teams.length; j++) {
				if (teams[j] == null)
					dists[i][j] = Integer.MAX_VALUE;
				else
					dists[i][j] = board.mindist(traders[i], teams[j]);
			}
			ourdists[i] = board.mindist(traders[i], location);
		}
		
		sort(ourdists, indices);
		
		for (int k = 0; k < Math.min(topK, traders.length); k++) {
			int index = indices[k];
			if (isClosest2(dists, index, player.getOurIndex())) {
				if (!isConflict(dists, index, player.getOurIndex()))
					return index;
				else
					return -index;
			}
		}
		
		// still chasing
		return indices[Math.min(1, traders.length - 1)];
	}
}