package cell.g3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Player implements cell.sim.Player {

	private Random gen = new Random();
	private int[] savedSack;
	private static int versions = 0;
	private int version = ++versions;

	private int quadCount = 0;
	private int[] tradeRequest;
	private int[] tradeGive;
	
	//TODO: Map-analysis, so as to generate the nextThreshold --> threshold for getting to the immediate next leprechaun
											//	globalThreshod --> depends on the state of the sack and location on map
											// 	to decide what to use.. next or global...
	private int[] maxNextThreshold = new int[] {5,5,5,5,5,5};
	private int[] minNextThreshold = new int[] {3,3,3,3,3,3};
	

	public String name() { return "G3" + (version != 1 ? " v" + version : ""); }

	public Direction move(int[][] board, int[] location, int[] sack,
	                      int[][] players, int[][] traders)
	{
		Mover mover = new Mover(copyII(board));
		
		savedSack = copyI(sack);
		if (quadCount == 0)
			quadCount = 4 * sack[0];

		for (;;) {
			Direction dir = randomDirection();
			int[] new_location = move(location, dir);
			int color = color(new_location, board);
			if (color >= 0 && sack[color] != 0) {
				savedSack[color]--;
				return dir;
			}
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
		Trader trader = new Trader();
		
		//Winning trade... the final step to victory !
		if (trader.winningTrade(rate, request, give))
			return;
		
		//Threshold decision -- next or global
		//Use sortedIndices(rate) to get an array of indices of rate, sorted by rate in ascending order. 
		//TODO: Jama implementation for more than 1 color trading
		//TODO: Need a combination of saving and greedy trades. Save some marbles, and still be greedy about others.
		
		if (trader.savingTrade(rate, request, give))
			return;

		//Greedy trade
		trader.greedyTrade(rate, request, give);
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

	class Trader
	{
		
		// If there is a winningTrade, return true and set player-level request and give arrays accordingly.
		// Else return false.
		public Boolean winningTrade(double[] rate, int[] request, int[] give)
		{
			double giveVal = 0;
			double requestVal = 0;

			for (int i = 0; i < rate.length; i++) {
				if (savedSack[i] > quadCount) {
					give[i] = savedSack[i] - quadCount;
					request[i] = 0;
				} else {
					give[i] = 0;
					request[i] = quadCount - savedSack[i];
				}

				giveVal += give[i] * rate[i];
				requestVal += request[i] * rate[i];
			}

			if (giveVal >= requestVal && giveVal > 0) {
				System.out.println("DEBUG Winning Trade " + giveVal + " " + requestVal);
				return true;
			}

			return false;
		}

		// If some marbles are below threshold, we need to save the player even if it is a lossy trade.
		//    return true and set player-level request and give arrays accordingly.
		// Else return false.
		public Boolean savingTrade(double[] rate, int[] request, int[] give)
		{
			Boolean save = false;
			double giveVal = 0;
			double requestVal = 0;

			for (int i = 0; i < rate.length; i++) {
				if (savedSack[i] <= minNextThreshold[i]) {
					save = true;
					break;
				}
			}

			if (save) {
				for (int i = 0; i < rate.length; i++) {
					if (savedSack[i] <= minNextThreshold[i]) {
						request[i] = maxNextThreshold[i] - savedSack[i];
						requestVal += request[i] * rate[i];
						give[i] = 0;
					} else {
						request[i] = 0;
						give[i] = 0;
					}
				}

				for (int i = 0; i < rate.length; i++) {
					if (savedSack[i] > maxNextThreshold[i]) {
						give[i] = (int)((requestVal - giveVal) / rate[i]) + 1;

						if (give[i] > savedSack[i] - maxNextThreshold[i])
							give[i] = savedSack[i] - maxNextThreshold[i];

						giveVal += give[i] * rate[i];
					}
				}

				if (giveVal >= requestVal && giveVal > 0) {
					System.out.println("DEBUG Saving Trade " + giveVal + " " + requestVal);
					return true;
				}
			}

			return false;
		}

		public void greedyTrade(double[] rate, int[] request, int[] give)
		{
			int minIndex = minIndex(rate);
			double giveVal = 0;
			double requestVal = 0;

			for(int i = 0; i < rate.length; i ++) {
				if(i == minIndex || savedSack[i] <= maxNextThreshold[i])
					give[i] = 0;
				else
					give[i] = savedSack[i] - maxNextThreshold[i];

				request[i] = 0;
				giveVal += give[i] * rate[i];
			}

			request[minIndex] = (int)(giveVal / rate[minIndex]);
			requestVal = request[minIndex] * rate[minIndex];
			System.out.println("DEBUG Greedy Trade " + giveVal + " " + requestVal);
		}
	}
	
	class Mover
	{
		int[][] board;
		Mover(int[][] board)
		{
			this.board = board;  
		}
		private int[] marblesForRoute(int[] currentlocation, Direction[] directions)
		{
			int[] marbles = new int[] {0,0,0,0,0,0};
			int currentColor = color(currentlocation, board);
			
			for(int i = 0; i != directions.length; i ++)
			{
				int[] loc = move(currentlocation, directions[i]);
				int colorIndex = color(loc, board);
				marbles[colorIndex] ++;
				currentlocation = loc;
			}
			
			return marbles;
		}
	}
	
	class MapAnalyzer
	{
		int[][] board;
		int radius = 2;
		int[] minRequired;
		Set<Integer[]> visitedLocations;
		int[] currentLocation;
		
		MapAnalyzer(int[][] board, int[] currentLocation)
		{
			this.board = board;
			this.currentLocation = currentLocation;
			minRequired = new int[6];
			visitedLocations = new HashSet<Integer[]>();
			visitedLocations.add(wrapIntToIntegerArray(currentLocation));
			computeMinThreshold(currentLocation);
		}
		
		Map<Integer[], Integer> neighbors(int[] location)
		{
			Map<Integer[], Integer> returnList = new HashMap<Integer[], Integer>();
			
			for(Player.Direction d : Direction.values())
			{
				int[] neighbor = move(location, d);
				int color = (color(neighbor, board)); 
				if(color != -1)
				{
					returnList.put(wrapIntToIntegerArray(neighbor), color);
				}
			}
			return returnList;
		}
		
		Integer[] wrapIntToIntegerArray(int[] a)
		{
			Integer[] newArray = new Integer[a.length];
			int i = 0;
			for(int value : a)
			{
				newArray[i++] = Integer.valueOf(value);
			}
			return newArray;
		}
		
		int[] wrapIntegerToIntArray(Integer[] a)
		{
			int[] newArray = new int[a.length];
			int i = 0;
			for(int value : a)
			{
				newArray[i++] = Integer.valueOf(value);
			}
			return newArray;
		}
		
		public int[] getMinRequired() {
			return minRequired;
		}
		
		Map<Integer[], Integer> initialize(int[] location)
		{
			Map<Integer[], Integer> map = neighbors(location);
			
			for(Map.Entry entry : map.entrySet())
			{
				minRequired[(Integer)entry.getValue()] = 1;
				visitedLocations.add((Integer[])entry.getKey());
			}
			return map;
		}
		
		void computeMinThreshold(int[] currentLocation)
		{
			Map<Integer[], Integer> map = initialize(currentLocation);
			map = expandRadius(map);
		}
		
		Map<Integer[], Integer> expandRadius(Map<Integer[], Integer> map)
		{
			Map<Integer[], Integer> tempNeighborsMap = new HashMap<Integer[], Integer>();
			Map<Integer[], Integer> outerNeighborsMap = new HashMap<Integer[], Integer>();
			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				tempNeighborsMap = neighbors(wrapIntegerToIntArray((Integer[])entry.getKey()));
				
				for(Map.Entry<Integer[], Integer> m : tempNeighborsMap.entrySet())
				{
					if(!visitedLocations.contains(m) && !outerNeighborsMap.containsKey(m.getKey()))
					{
						outerNeighborsMap.put(m.getKey(), m.getValue());
					}
				}
			}	
			//check for straight paths
			for(Map.Entry<Integer[], Integer> entry : outerNeighborsMap.entrySet())
			{		
					if(straightPath((Integer[])entry.getKey()))
					{
						visitedLocations.add((Integer[])entry.getKey());
						int count = countSameColorStraightPath(entry);
						if(count > minRequired[(Integer)entry.getValue()])
						{
							minRequired[(Integer)entry.getValue()] = count;
						}
					}
					else
						continue;
			}
			
			//Done with straight paths... add code for outer neighbors that have more than 1 shortest paths to dem thru currentLocation
			//for(Map.Entry<Integer[], Integer> entry : outerNeighborsMap.entrySet())
			//{
			//	if(!visitedLocations.contains(m))
//			{
//				
//			}
			//}
			
			
			return map;
		}
		
		int countSameColorStraightPath(Map.Entry<Integer[], Integer> entry)
		{
			int count = 0;
			int dx = entry.getKey()[0] - currentLocation[0];
			int dy = entry.getKey()[1] - currentLocation[1];
			int color = entry.getValue();
			
			if(dx == dy)
			{
				if(dx < 0) //NW
				{
					for(int i = 1; i <= dx; i --)
					{
						int[] x = new int[] {currentLocation[0] - i, currentLocation[1] - i}; 
						if(color(x, board) == color)
							count ++;
					}
				}
				else if(dx > 0) //SE
				{
					for(int i = 1; i <= dx; i ++)
					{
						int[] x = new int[] {currentLocation[0] + i, currentLocation[1] + i}; 
						if(color(x, board) == color)
							count ++;
					}
				}
			}
			else if (dx == 0)
			{
				if(dy < 0) //N
				{
					for(int i = 1; i <= Math.abs(dy); i ++)
					{
						int[] x = new int[] {currentLocation[0], currentLocation[1] - i}; 
						if(color(x, board) == color)
							count ++;
					}
				}
				else if(dy > 0) //S
				{
					for(int i = 1; i <= dy; i ++)
					{
						int[] x = new int[] {currentLocation[0], currentLocation[1] + i}; 
						if(color(x, board) == color)
							count ++;
					}
				}
			}
			else if(dy == 0)
			{
				if(dx < 0) //W
				{
					for(int i = 1; i <= Math.abs(dx); i ++)
					{
						int[] x = new int[] {currentLocation[0] - i, currentLocation[1]}; 
						if(color(x, board) == color)
							count ++;
					}
				}
				else if(dx > 0) //E
				{
					for(int i = 1; i <= dx; i ++)
					{
						int[] x = new int[] {currentLocation[0] + i, currentLocation[1]}; 
						if(color(x, board) == color)
							count ++;
					}
				}
			}
			else
				System.err.println("DEBUG Problems in countSameColorStraighPath()");
			return count;
		}
		
		boolean straightPath(Integer[] locs)
		{
			int dx = locs[0] - currentLocation[0];
			int dy = locs[1] - currentLocation[1];
			if(dx == dy || dx == 0 || dy == 0)
				return true;
			return false;
		}
		
		
	}
	
	private int maxIndex(double[] a)
	{
		int maxIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] > a[maxIndex])
				maxIndex = i;
		}
		return maxIndex;
	}
	
	private int minIndex(double[] a)
	{
		int minIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] < a[minIndex])
				minIndex = i;
		}
		return minIndex;
	}
	
	private int maxIndex(int[] a)
	{
		int maxIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] > a[maxIndex])
				maxIndex = i;
		}
		return maxIndex;
	}
	
	private int minIndex(int[] a)
	{
		int minIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] < a[minIndex])
				minIndex = i;
		}
		return minIndex;
	}
	
	private int total(int[] a)
	{
		int total = 0;
		for(int i = 0; i < a.length; i ++)
		{
			total += a[i];
		}
		return total;
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
	
	private int[][] copyII(int[][] a)
	{
		int[][] b = new int [a.length][a[0].length];
		for (int i = 0 ; i != a.length ; ++i)
			for(int j = 0; j != a[i].length; ++j)
				b[i][j] = a[i][j];
		return b;
	}

	private int[] sortedIndices(double[] a)
	{
		int[] indices = new int [a.length];
		double[] b = new double [a.length];
		int i;

		for (i = 0; i < a.length; i++)
			b[i] = a[i];

		for (i = 0; i < a.length; i++) {
			indices[i] = minIndex(b);
			b[indices[i]] = 3;	// Assuming that this will be called for rates, which are between 1 and 2.
		}

		return indices;
	}
}