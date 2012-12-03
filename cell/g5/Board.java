package cell.g5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cell.sim.Player;
import cell.sim.Player.Direction;

public class Board {
	private static final Logger logger = Logger.getLogger(Board.class);

	/* Gloriously untested */
	public static int[] getGlobalTravelCostsNaive(int[][] board, int[] source) {
		int[] maxEach = new int[6];

		for (int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				if(board[i][j] < 0)
					continue;

				int[] dest = new int[] {i, j};

				if (Arrays.equals(source, dest))
					continue;

				int[] costs = getNaiveCostsBetween(source, dest, board);
				for (int idx = 0; idx < costs.length; idx++) {
					maxEach[idx] = Math.max(maxEach[idx], costs[idx]);
				}
			}
		}

		return maxEach;
	}

	/**
	 * Get travel costs within a certain cutoff search radius.
	 * @return
	 */
	public static int[] getLocalTravelCosts(int[][] board, int[] source, int cutOff) {
		int[] result = new int[6];

		int[][] testBoard = new int[board.length][board[0].length];

		int minRow = Math.max(source[0] - cutOff, 0);
		int maxRow = Math.min(source[0] + cutOff, board.length - 1);

		int sourceRow = source[0];

		for(int row = minRow ; row <= maxRow; row++) {
			int firstTile = source[1] - cutOff;
			int lastTile = source[1] + cutOff;
			int lastOffset = Math.min(row - sourceRow, 0);
			int firstOffset = Math.max(row - sourceRow, 0);
			lastTile += lastOffset;
			firstTile += firstOffset;

			firstTile = Math.max(firstTile, 0); // Ensure we don't get fall below 0

			for(int col = 0; col < board[0].length; col++) {
				if(board[row][col] == -1) {
					testBoard[row][col] = -1;
					continue;
				} else if(source[0] == row && source[1] == col) {
					continue;
				}

				if(col >= firstTile && col <= lastTile) {
					testBoard[row][col] = 1;
					result[board[row][col]]++;

					int[] costs = getNaiveCostsBetween(source, new int[]{row, col}, board);
					for (int idx = 0; idx < costs.length; idx++) {
						result[idx] = Math.max(result[idx], costs[idx]);
					}
				}
			}
		}

//				for(int[] row : testBoard) {
//					for(int num : row) {
//						String printNum = (num == -1) ? " X" : " " + num;
//						logger.log("" + printNum);
//					}
//					logger.log("");
//				}
//				
//				logger.log("");

		return result;
	}

	/* Tested. */
	public static int shortestDistance(int[] source, int[] dest) {
		int dx = dest[0] - source[0];
		int dy = dest[1] - source[1];

		if (dx > 0 && dy > 0) {
			int diag = Math.min(dx, dy);
			int edge = Math.max(dx - diag, dy - diag);
			return diag + edge;
		} else if (dx < 0 && dy < 0) {
			int diag = Math.min(-dx, -dy);
			int edge = Math.max(-diag - dx, -diag - dy);
			return diag + edge;
		} else {
			return Math.abs(dx) + Math.abs(dy);
		}
	}

	/* Tested. */
	public static int[] getClosest(int[] source, Collection<int[]> candidates) {
		if (candidates.size() == 0)
			throw new IllegalArgumentException("Zero-sized list.");

		int[] closest = null;
		int dist = Integer.MAX_VALUE;

		for (int[] candidate : candidates) {
			int testDist = shortestDistance(source, candidate);
			if (testDist < dist) {
				dist = testDist;
				closest = candidate;
			}
		}

		return closest;
	}

	public static int[] getCenter(int[][] board) {
		return new int[] { board.length / 2 , board.length / 2};
	}
	
	public static class RouteCost {
		int nextPos[];
		int cost[];
		
		public RouteCost(int[] nextPos, int cost[]) {
			this.nextPos = nextPos;
			this.cost = cost;
		}
		
		@Override
		public String toString() {
			return Arrays.toString(nextPos) + ":" + Arrays.toString(cost);
		}
	}
	
	public static class RouteCandidate {
		int[] firstMove;
		Direction firstDirection;
		int travelDist = 1;
		int[] pos;
		int[] cost = new int[6];
		
		public RouteCandidate(int[] firstMove, Direction firstDirection, int[] pos) {
			this.firstMove = firstMove;
			this.firstDirection = firstDirection;
			this.pos = pos;
		}
		
		public RouteCandidate(RouteCandidate c, int[] pos) {
			this.firstMove = c.firstMove;
			this.firstDirection = c.firstDirection;
			this.cost = Arrays.copyOf(c.cost, 6);
			this.travelDist += c.travelDist;
			this.pos = pos;
		}
		
		public void addCost(int color) {
			this.cost[color]++;
		}
	}
	
	public static boolean isOnBoard(int[] pos, int[][] board) {
		return (pos[0] >= 0 && pos[0] < board.length) &&
				(pos[1] >= 0 && pos[1] < board.length) &&
				(board[pos[0]][pos[1]] >= 0);
	}
	
	public static class PosWrapper {
		int[] pos;
		
		public PosWrapper(int[] pos) {
			this.pos = pos;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof PosWrapper[]) {
				PosWrapper o = (PosWrapper) obj;
				return Arrays.equals(pos, o.pos);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return Arrays.hashCode(pos);
		}
	}
	
	public static RouteCost[] getAlternateRouteCost(int[][] board, int[] source, int[] dest) {
		int sourceDistance = shortestDistance(source, dest);
		
		List<RouteCost> costs = new ArrayList<RouteCost>();
		
		Stack<RouteCandidate> possibles = new Stack<RouteCandidate>();
		Set<PosWrapper> visitedPos = new HashSet<PosWrapper>();
		
		RouteCandidate newCan;
		int[] nextPos;
		PosWrapper wrapper = new PosWrapper(source);
		visitedPos.add(wrapper);
		for(Direction d: Direction.values()) {
			nextPos = move(source, d);
			
			if(!isOnBoard(nextPos, board)) {
				continue;
			}
			
			// Just make sure we're not going backwards.
			if(shortestDistance(nextPos, dest) <= sourceDistance) {
				newCan = new RouteCandidate(nextPos, d, nextPos);
				newCan.addCost(board[nextPos[0]][nextPos[1]]);
				possibles.push(newCan);
			}
		}
		
		while(possibles.size() > 0) {
			RouteCandidate currentCan = possibles.pop();
			
			for(Direction d : Direction.values()) {
				nextPos = move(currentCan.pos, d);
				wrapper = new PosWrapper(nextPos);
				if(!visitedPos.add(wrapper)) {
					continue;
				}
				
				if(!isOnBoard(nextPos, board)) {
					continue;
				}
				
				// Arrived at destination... add cost and add route.
				int sDFromNext = shortestDistance(nextPos, dest);
				
				if(nextPos[0] == dest[0] && nextPos[1] == dest[1]) {
					currentCan.addCost(board[nextPos[0]][nextPos[1]]);
					costs.add(new RouteCost(currentCan.firstMove, currentCan.cost));
				}
				// If the distance minus the amount we've traveled is less than the original distance.
				else if(sDFromNext + currentCan.travelDist <= sourceDistance) {
					
					newCan = new RouteCandidate(currentCan, nextPos);
					newCan.addCost(board[nextPos[0]][nextPos[1]]);
					possibles.push(newCan);
				}
				
//				cost = new RouteCost()
			}
		}
		
		
		return costs.toArray(new RouteCost[0]);
	}

	/**
	 * Returns traders we're confident we'll reach first.
	 *  Tested. 
	 */
	public static List<int[]> getExclusiveTraders(int[] source, int[][] players, int[][] traders) {
		List<int[]> playerExclusives = new LinkedList<int[]>();

		/* Part 1: Determining if two or more players are at the source.
		 * If so, neither of them has an exclusive trader and we exit early.
		 */
		int sameCount = 0;

		for (int[] playerLocation : players) {
			if (playerLocation != null && Arrays.equals(source, playerLocation))
				sameCount++;
			else if(playerLocation == null)
				logger.log("null player");
		}

		if (sameCount >= 2)
			return playerExclusives;
		/* End part 1. */

		/* Part 2: For each trader, determine the closest player. If that player's location
		 * is our location, add it to our exclusives unless there is another equally close
		 * player.
		 */
		for (int[] traderLoc : traders) {
			int minimumDistance = Integer.MAX_VALUE;

			logger.log("Trader: " + Arrays.toString(traderLoc));

			/* Find the shortest distance among players that are not us. */
			for (int[] playerLoc : players) {
				if(playerLoc != null) {
					if (Arrays.equals(playerLoc, source)) {
						logger.log("Ourselves: " + Arrays.toString(playerLoc));
						continue;
					} else {
						logger.log("Another team: " + Arrays.toString(playerLoc));
					}

					int dist = shortestDistance(playerLoc, traderLoc);
					logger.log("Their distance: " + dist);
					minimumDistance = Math.min(minimumDistance, dist);
				}
				else logger.log("null player");
			}

			logger.log("We're at: " + Arrays.toString(source));
			final int playerDist = shortestDistance(source, traderLoc);
			logger.log("Our distance: " + playerDist);
			if (playerDist < minimumDistance) {
				logger.log("Our exclusive: " + Arrays.toString(traderLoc));
				playerExclusives.add(traderLoc);
			} else {
				logger.log("Not ours: " + Arrays.toString(traderLoc));
			}
		}

		return playerExclusives;
	}

	/* Tested. */
	public static Direction makeNaiveProgressToward(int[] source, int[] destination /*, int[][] board, int[] marbles */) {
		if (Arrays.equals(source, destination))
			throw new IllegalArgumentException("source=dest");

		/* TODO: ensure we have enough marbles. With time, though. */
		LinkedHashMap<int[], Direction> locsToDirs = new LinkedHashMap<int[], Direction>();
		for (Player.Direction dir : Player.Direction.values()) {
			locsToDirs.put(move(source, dir), dir);
		}

		int[] closest = getClosest(destination, locsToDirs.keySet());
		return locsToDirs.get(closest);
	}

	/* Jacked from Orestis' code. Presumably it works. */
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
		} else if (dir == Player.Direction.N) { // is north if flipped
			di = -1;
			dj = 0;
		} else if (dir == Player.Direction.S) { // is south if flipped
			di = 1;
			dj = 0;
		} else if (dir == Player.Direction.SE) {
			di = 1;
			dj = 1;
		} else
			throw new RuntimeException("Invalid movement");
		int[] new_location = {i + di, j + dj};
		return new_location;
	}
	
	/* Beware: this will only work with the movement specified by makeNaiveProgressToward.
	 * Not entirely confident this method workd.
	 */
	public static int[] getNaiveCostsBetween(int[] source, int[] dest, int[][] board) {
		if (Arrays.equals(source, dest))
			throw new IllegalArgumentException("source=dest");

		int[] required = new int[6];
		int[] cur = source;

		do {
//			logger.log("PreStep: " + Arrays.toString(cur));
//			logger.log("Moving to: " + Arrays.toString(dest));
			cur = move(cur, makeNaiveProgressToward(cur, dest));
//			logger.log("PostStep: " + Arrays.toString(cur));
			int colorIdx = board[cur[0]][cur[1]];
			required[colorIdx]++;
		} while (!(Arrays.equals(cur, dest)));

		return required;
	}
}