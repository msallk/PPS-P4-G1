package cell.g4.movement;

import java.util.ArrayList;
import java.util.List;

import cell.g4.Board;
import cell.g4.Player;

public class SafeTraderFinder {
	private Board board;
	private Player player;
	private final static int topK = 3;
	
	public SafeTraderFinder(Board board, Player player) {
		this.board = board;
		this.player = player;
	}

	private void sort(int[] dists, int[] indices) {
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
	
	// find the trader that is occupied by us
	public List<Integer> findOurTrader(int[] location, int[][] teams, int[][] traders, boolean inTrading) {
		List<Integer> ourTraders = new ArrayList<Integer>();
		
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
		
		// increasing order of distance
		for (int i = 0; i < Math.min(topK, traders.length); i++) {
			if (isOurTrader(traders[indices[i]], dists[indices[i]], ourdists[i], inTrading)) {
				ourTraders.add(indices[i]);				
			}
		}
		return ourTraders;				
	}

	// a trader is ours, if we are closer to it than any other team
	// NOTE: it is called during trade, however, in trade, we only know the location in the last round
	// which means we have to predict one step ahead
	private boolean isOurTrader(int[] trader, int[] dists, int ourdist, boolean inTrading) {
		int relax = inTrading ? 1 : 0; 
				
		for (int i = 0; i < dists.length; i++) {
			if (player.getOurIndex() == i)
				continue;
						
			if (dists[i] <= ourdist + relax) // because we don't other group's move in this round, release constraint
				return false;			
		}
		
		return true;
	}

}
