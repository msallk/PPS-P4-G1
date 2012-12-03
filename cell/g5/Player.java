package cell.g5;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Player implements cell.sim.Player {
	private static final Logger logger = Logger.getLogger(Player.class);
	
	private Random gen = new Random();
	private int[] savedSack;
	private static int versions = 0;
	private int version = ++versions;
	
	private int startMarbles = -1;
	private boolean startInit = false;

	private int[][] savedPlayers;
	private int[][] savedTraders;
	private int[][] savedBoard;
	private int[] storedLocation;
	
	public String name() { return "g5" + (version != 1 ? " v" + version : ""); }

	public Direction move(int[][] board, int[] location, int[] sack,
	                      int[][] players, int[][] traders)
	{
		if (!startInit) {
			startInit = true;
			startMarbles = sack[0];
		}
		
		savedSack = copyI(sack);
		savedPlayers = players;
		savedTraders = traders;
		savedBoard = board;
		
		Direction dir = randomDirection();

		/* TODO: perhaps identify which index is us. */
		List<int[]> exclusiveTraders = Board.getExclusiveTraders(location, players, traders);
		if (exclusiveTraders.size() == 0) {
			logger.log("No exclusive leprechaun. Moving to the center.");
			/* Fallback. */
			boolean canMoveToCenter = true; 
			for (;;) {
				int[] center = Board.getCenter(board);
				if(Arrays.equals(center, location) || !canMoveToCenter)
					dir = randomDirection();
				else dir = Board.makeNaiveProgressToward(location, center);
				int[] new_location = move(location, dir);
				int color = color(new_location, board);
				if (color >= 0 && sack[color] != 0) {
					logger.log("Moved toward the center.");
					savedSack[color]--;
					return dir;
				}
				else canMoveToCenter = false;
			}			
		}
		
		for(int[] trader : exclusiveTraders) {
			logger.log("Trader: " + Arrays.toString(trader));
		}
		
		logger.log("We have an exclusive trader.");
		
		int[] closest = Board.getClosest(location, exclusiveTraders);
		dir = Board.makeNaiveProgressToward(location, closest /*, board, sack */);
		int[] new_location = move(location, dir);
		int color = color(new_location, board);
		if (color >= 0 && sack[color] != 0) {
			savedSack[color]--;
			logger.log("Moved toward the exclusive trader.");
			storedLocation = new_location;
			return dir;
		}
		
		logger.log("We could not take the step toward the exclusive. Moving to the center.");

		/* Fallback. */
		boolean canMoveToCenter = true; 
		for (;;) {
			int[] center = Board.getCenter(board);
			if(Arrays.equals(center, location)  || !canMoveToCenter)
				dir = randomDirection();
			else dir = Board.makeNaiveProgressToward(location, center);
			new_location = move(location, dir);
			color = color(new_location, board);
			if (color >= 0 && sack[color] != 0) {
				savedSack[color]--;
				storedLocation = new_location;
				return dir;
			}
			else canMoveToCenter = false;
		}
	}

	private Direction randomDirection()
	{
		switch(gen.nextInt(6)) {
			case 0: return Direction.E;
			case 1: return Direction.W;
			case 2: return Direction.SE;
			case 3: return Direction.S;
			case 4: return Direction.N;
			case 5: return Direction.NW;
			default: return null;
		}
	}

	public void trade(double[] rate, int[] request, int[] give)
	{
		/* What is actually implemented: keep {previously:10} now, a few or so of each marble type, trade the rest for cheapest. */
		
		if(Trading.tradeVictoryAndCheck(request, give, savedSack, startMarbles, rate))
			return;
		
//		int[] keep = new int[] {10, 10, 10, 10, 10, 10};
		/* Keep double as many of each marble marbles as needed to reach any square on the board
		 * using naive movement. 
		 */
		int[] keep = Maths.scale(Board.getGlobalTravelCostsNaive(savedBoard, storedLocation), 2);
		Trading.tradeAllBut(request, give, savedSack, keep, rate);
		
		
		/* Idea: (not what is implemented)
		 * If has exclusive leprechaun from here:
		 * 	keep only enough to get there. Trade everything else for cheapest.
		 * Else: [not yet implemented, so NOOP]
		 *  redistibute marbles to have the number required to reach anywhere on the board
		 *  Note that other heuristics might be better here. Such as redistribute value equally into
		 *  all marble types, or prioritize according to surroundings.
		 */
		
		
//		/* Remove the trader at the current location since we just stepped on him. */
//		int[][] savedTradersCopy = new int[savedTraders.length - 1][];
//		int copyIdx = 0;
//		
//		for (int[] trader : savedTraders) {
//			if (Arrays.equals(trader, storedLocation))
//				continue;
//			savedTradersCopy[copyIdx] = trader;
//			copyIdx++;
//		}
//		savedTraders = savedTradersCopy;
//		
//		
//		/* Note: not guaranteed to be exclusive anymore. Think about it later. */
//		/* Should also probably make it so we don't recompute this all and risk getting different results than what we do. */
//		List<int[]> exclusive = Board.getExclusiveTraders(storedLocation, savedPlayers, savedTraders);
//		if (exclusive.size() == 0)
//			return;
//
//		int[] target = Board.getClosest(storedLocation, exclusive);
//		int[] saveToMove = Board.getNaiveCostsBetween(storedLocation, target, savedBoard);
//		
//		logger.log("Cost to next exclusive: " + Arrays.toString(saveToMove));
//		Trading.tradeAllBut(request, give, savedSack, saveToMove, rate);
//		
		
		
		
		
//		for (int r = 0 ; r != 6 ; ++r)
//			request[r] = give[r] = 0;
//		double rv = 0.0, gv = 0.0;
//		for (int i = 0 ; i != 10 ; ++i) {
//			int j = gen.nextInt(6);
//			if (give[j] == savedSack[j]) break;
//			give[j]++;
//			gv += rate[j];
//		}
//		for (;;) {
//			int j = gen.nextInt(6);
//			if (rv + rate[j] >= gv) break;
//			request[j]++;
//			rv += rate[j];
//		}
	}

	private static int[] move(int[] location, Player.Direction dir)
	{
		int di, dj;
		int i = location[0];
		int j = location[1];
		if (dir == Player.Direction.W) {
			di = 0;
			dj = -1;
		} else if (dir == Player.Direction.E) {
			di = 0;
			dj = 1;
		} else if (dir == Player.Direction.NW) {
			di = -1;
			dj = -1;
		} else if (dir == Player.Direction.N) {
			di = -1;
			dj = 0;
		} else if (dir == Player.Direction.S) {
			di = 1;
			dj = 0;
		} else if (dir == Player.Direction.SE) {
			di = 1;
			dj = 1;
		} else return null;
		int[] new_location = {i + di, j + dj};
		return new_location;
	}

	private static int color(int[] location, int[][] board)
	{
		int i = location[0];
		int j = location[1];
		int dim2_1 = board.length;
		if (i < 0 || i >= dim2_1 || j < 0 || j >= dim2_1)
			return -1;
		return board[i][j];
	}

	private int[] copyI(int[] a)
	{
		int[] b = new int [a.length];
		for (int i = 0 ; i != a.length ; ++i)
			b[i] = a[i];
		return b;
	}
}
