package cell.g4.movement;

import java.util.List;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;
import cell.sim.Player.Direction;

/*
 * When we are controlling more than one traders
 * Find a best order to reach maximum number of traders
 * For simplicity, only consider there are two traders
 * We are control A,B:
 * 	We should take A->B, if d(A)+d(A,B) < mindist(B, other player) and d(B)+d(A,B) > mindist(A, other player)
 */
public class LocalBestOrderMove extends MoveAlgo {

	private SafeTraderFinder finder;
	
	public LocalBestOrderMove(Board board, Sack sack, Player player) {
		super(board, sack, player);
		finder = new SafeTraderFinder(board, player);
	}

	@Override
	public Direction move(int[] location, int[][] players, int[][] traders) {
		List<Integer> ourTraders = finder.findOurTrader(location, players, traders, false);
		
		assert(ourTraders.size() >= 2);
		
		// we go to the first then the second
		int dist1 = board.mindist(location, traders[ourTraders.get(0)]) + 
				board.mindist(traders[ourTraders.get(0)], traders[ourTraders.get(1)]);
		
		// we go to the second then the first
		int dist2 = board.mindist(location, traders[ourTraders.get(1)]) + 
				board.mindist(traders[ourTraders.get(0)], traders[ourTraders.get(1)]);
		
		// by default, we should take the first path
		// however, if some team is close to the second trader...
		if (!canReach(players, traders, ourTraders.get(1), dist1)
			&& canReach(players, traders, ourTraders.get(0), dist2)) {
			// we should go to the second first
			System.err.println("DETOUR: GO TO THE SECOND");

			List<Direction> dirs = board.nextMove(location, traders[ourTraders.get(1)]);
			
			Direction dir = pickDir(location, dirs);
			
			return dir;
		}		
		
		return null;
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
	
	private boolean canReach(int[][] players, int[][] traders, int secondTid, int dist) {
		for (int i = 0; i < players.length; i++) {
			if (i == player.getOurIndex())
				continue;
			
			// player will go for the second trader, and it is faster than us
			if (isNext(i, secondTid) && board.mindist(players[i], traders[secondTid]) < dist) {
				return false;
			}
		}
		return true;
	}
	
	// return true if the trader is the player's next target
	// based on our prediction
	private boolean isNext(int playerId, int traderId) {
		return false;
	}
}
