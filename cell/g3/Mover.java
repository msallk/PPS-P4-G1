package cell.g3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cell.sim.Player.Direction;

public 	class Mover
{
	public static boolean MOVER_DEBUG = false;
	int[][] board;
	int[] location;
	int[][] traders;
	int[] targetLocation;
	int[] sack;
	Player player;
	private Direction nextStep;

	Mover(Player player)
	{
		this.player = player;
		this.board = player.copyII(player.board);
		this.sack = player.copyI(player.savedSack);
		this.location = player.copyI(player.currentLocation);
		this.traders = player.copyII(player.traders);
		targetLocation = closestTrader();
		nextStep = nextStep();
	}

	public Direction getNextStep() {
		mLogln("nextstep");
		return nextStepWrapper(nextStep);
	}

	//returns number of each marbles required for a given set of directions from a given location
	private int[] marblesForRoute(int[] currentlocation, Direction[] directions)
	{
		int[] marbles = new int[] {0,0,0,0,0,0};
		int currentColor = Player.color(currentlocation, board);

		for(int i = 0; i != directions.length; i ++)
		{
			int[] loc = Player.move(currentlocation, directions[i]);
			int colorIndex = Player.color(loc, board);
			marbles[colorIndex] ++;
			currentlocation = loc;
		}

		return marbles;
	}

	private boolean isTrader(int[] location)
	{
		for(int i = 0; i < traders.length; i ++)
			if(location[0] == traders[i][0] && location[1] == traders[i][1])
				return true;
		return false;
	}

	
	private int distanceBetween(int[] location, int[] targetLocation)
	{
		if(location[0] == targetLocation[0] && location[1] == targetLocation[1])
		{
			return 0;
		}
		
		//int[] targetLocation;		
		Set<Integer[]> visitedLocations = new HashSet<Integer[]>();
		visitedLocations.add(MapAnalyzer.wrapIntToIntegerArray(location));

		Map<Integer[], Integer> tempNeighborsMap;
		Map<Integer[], Integer> outerNeighborsMap = new HashMap<Integer[], Integer>();

		///first layer of neighbors
		Map<Integer[], Integer> map = MapAnalyzer.neighbors(location, board);
		for(Integer[] en : map.keySet())
		{
			visitedLocations.add(en);
			if(en[0] == targetLocation[0] && en[1] == targetLocation[1])
				return 1; //targetLocation = MapAnalyzer.wrapIntegerToIntArray(en);
		}

		for(int i = 2; i <= 2*board.length+2; i++)  // loose upper bound (we want farthest possible distance)
		{
			/*if(i > 1000)
			{
				mLogln("Problem in Looping closestTrader()");
				System.exit(1);
			}*/
			
			/*for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				mLogln(entry.getValue() + " radius : " + (i+1));
			}*/

			tempNeighborsMap = new HashMap<Integer[], Integer>();
			outerNeighborsMap = new HashMap<Integer[], Integer>();

			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				tempNeighborsMap.clear();
				//tempNeighborsMap = new HashMap<Integer[], Integer>();
				tempNeighborsMap.putAll(MapAnalyzer.neighbors(MapAnalyzer.wrapIntegerToIntArray((Integer[])entry.getKey()), board));
				for(Map.Entry<Integer[], Integer> m : tempNeighborsMap.entrySet())
				{
					if(!Wrap.contains(visitedLocations, m.getKey()) && !Wrap.contains(outerNeighborsMap, m.getKey()))
					{
						outerNeighborsMap.put(m.getKey(), m.getValue());
						visitedLocations.add((Integer[])m.getKey());
						//mLogln(" location: " + m.getKey()[0] + "," + m.getKey()[1] + " : color: " + m.getValue() + " ref:" + m);
						if(m.getKey()[0] == targetLocation[0] && m.getKey()[1] == targetLocation[1])
							return i;
					}
				}
			}

			map.clear();  //maybe make new map instead as in MapAnalyzer
			//map = new HashMap<Integer[], Integer>();
			map.putAll(outerNeighborsMap);
			outerNeighborsMap.clear();
			//outerNeighborsMap = new HashMap<Integer[], Integer>();
		}
		mLogln("problem in distanceBetween():  distance not found");
		return -1;
	}
	
	int closestOpponent(int[] traderLoc)
	{
		int minDistance = Integer.MAX_VALUE;
		for(int[] pl : player.players)
		{
			if(pl != null)
			{
				int dist = distanceBetween(traderLoc, pl);
				if(dist < minDistance)
				{
					minDistance = dist;
				}
			}
		}
		return minDistance;
	}
	
	//TODO: Rank the leprechauns based on proximity to other leps or other players around it and color needed.  dont just pick arbitrarily  
	/**
	 * Pick arbitrary trader that we have the color for, that is closest, and is not closer or as close to another player
	 * If no traders qualify, go towards middle of board
	 * @return
	 */
	private int[] closestTrader()
	{
		mLogln("closest trader");
		
		targetLocation = new int[]{player.board.length/2, player.board.length/2};		
		Set<Integer[]> visitedLocations = new HashSet<Integer[]>();
		visitedLocations.add(MapAnalyzer.wrapIntToIntegerArray(location));

		Map<Integer[], Integer> tempNeighborsMap;
		Map<Integer[], Integer> outerNeighborsMap = new HashMap<Integer[], Integer>();

		///first layer of neighbors
		Map<Integer[], Integer> map = MapAnalyzer.neighbors(location, board);
		for(Map.Entry<Integer[], Integer> en : map.entrySet())
		{
			visitedLocations.add(en.getKey());
			if(isTrader(MapAnalyzer.wrapIntegerToIntArray(en.getKey())) && sack[en.getValue()] > 0 && closestOpponent(MapAnalyzer.wrapIntegerToIntArray(en.getKey())) > 1)
			{
				targetLocation = MapAnalyzer.wrapIntegerToIntArray(en.getKey());
				return targetLocation;
			}
		}

		for(int i = 0; i <= 2*player.board.length; i++)
		{
			/*if(i > 1000)
			{
				mLogln("Problem in Looping closestTrader()");
				System.exit(1);
			}*/
			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				mLogln(entry.getValue() + " radius : " + (i+1));
			}

			tempNeighborsMap = new HashMap<Integer[], Integer>();
			outerNeighborsMap = new HashMap<Integer[], Integer>();

			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				tempNeighborsMap.clear();
				//tempNeighborsMap = new HashMap<Integer[], Integer>();
				tempNeighborsMap.putAll(MapAnalyzer.neighbors(MapAnalyzer.wrapIntegerToIntArray((Integer[])entry.getKey()), board));
				for(Map.Entry<Integer[], Integer> m : tempNeighborsMap.entrySet())
				{
					if(!Wrap.contains(visitedLocations, m.getKey()) && !Wrap.contains(outerNeighborsMap, m.getKey()))
					{
						outerNeighborsMap.put(m.getKey(), m.getValue());
						visitedLocations.add((Integer[])m.getKey());
						mLogln(" location: " + m.getKey()[0] + "," + m.getKey()[1] + " : color: " + m.getValue() + " ref:" + m);
						if(isTrader(MapAnalyzer.wrapIntegerToIntArray(m.getKey())) && sack[m.getValue()] > 0 && closestOpponent(MapAnalyzer.wrapIntegerToIntArray(m.getKey())) > i+2)
						{
							targetLocation = MapAnalyzer.wrapIntegerToIntArray(m.getKey());
							return targetLocation;
						}
					}
				}
			}

			map.clear();  //maybe make new map instead as in MapAnalyzer
			//map = new HashMap<Integer[], Integer>();
			map.putAll(outerNeighborsMap);
			outerNeighborsMap.clear();
			//outerNeighborsMap = new HashMap<Integer[], Integer>();
		}

		return targetLocation;
	}

	private Direction randomStep()
	{
		Random gen = new Random();
		Direction dir;

		do 
		{
			dir = Player.randomDirection(gen);
			if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
				return dir;
		}
		while(sack[Player.color(Player.move(location, dir), board)] <= 0);

		return null;
	}

	/**
	 * If don't have the marble needed to make the move, keep on randomly picking new direction
	 * @param firstAttempt Is null if attempted to move onto same space in nextStep function
	 * @return
	 */
	private Direction nextStepWrapper(Direction firstAttempt)
	{
		Random gen = new Random();
		if(firstAttempt == null)
			return randomStep();
		if(sack[Player.color(Player.move(location, firstAttempt), board)] <= 0)
		{
			switch(firstAttempt)
			{
				case N: 
				{
					if(Player.color(Player.move(location, Direction.NW), board) >= 0 && sack[Player.color(Player.move(location, Direction.NW), board)] > 0)
						return Direction.NW;
					else if(Player.color(Player.move(location, Direction.E), board) >= 0 && sack[Player.color(Player.move(location, Direction.E), board)] > 0)
						return Direction.E;
					else 
						return randomStep();
				}
				case S: 
				{
					if(Player.color(Player.move(location, Direction.SE), board) >= 0 && sack[Player.color(Player.move(location, Direction.SE), board)] > 0)
						return Direction.SE;
					else if(Player.color(Player.move(location, Direction.W), board) >= 0 && sack[Player.color(Player.move(location, Direction.W), board)] > 0)
						return Direction.W;
					else 
						return randomStep();
				}
				case W: 
				{
					if(Player.color(Player.move(location, Direction.NW), board) >= 0 && sack[Player.color(Player.move(location, Direction.NW), board)] > 0)
						return Direction.NW;
					else if(Player.color(Player.move(location, Direction.S), board) >= 0 && sack[Player.color(Player.move(location, Direction.S), board)] > 0)
						return Direction.S;
					else 
						return randomStep();
				}
				case E: 
				{
					if(Player.color(Player.move(location, Direction.N), board) >= 0 && sack[Player.color(Player.move(location, Direction.N), board)] > 0)
						return Direction.N;
					else if(Player.color(Player.move(location, Direction.SE), board) >= 0 && sack[Player.color(Player.move(location, Direction.SE), board)] > 0)
						return Direction.SE;
					else 
						return randomStep();
				}
				case NW: 
				{
					if(Player.color(Player.move(location, Direction.N), board) >= 0 && sack[Player.color(Player.move(location, Direction.N), board)] > 0)
						return Direction.N;
					else if(Player.color(Player.move(location, Direction.W), board) >= 0 && sack[Player.color(Player.move(location, Direction.W), board)] > 0)
						return Direction.W;
					else 
						return randomStep();
				}
				case SE: 
				{
					if(Player.color(Player.move(location, Direction.E), board) >= 0 && sack[Player.color(Player.move(location, Direction.E), board)] > 0)
						return Direction.E;
					else if(Player.color(Player.move(location, Direction.S), board) >= 0 && sack[Player.color(Player.move(location, Direction.S), board)] > 0)
						return Direction.S;
					else 
						return randomStep();
				}
				default:
				{
					mLogln("problem in switch case");
					return null;
				}
			}//end switch
		}
		else
			return firstAttempt;

	}

	private Direction nextStep() //computes the hexdrant of the leprechaun and calculates the step...
	{
		int dy = targetLocation[0] - location[0]; /////switched dx and dy
		int dx = targetLocation[1] - location[1];

		if(dx == 0 & dy == 0) //same spot
		{
			mLogln("problem with nextStep()");
			return null;
		}

		if(dx == dy)
		{
			if(dx < 0) //NW
			{
				return Direction.NW;
			}
			else if(dx > 0) //SE
			{
				return Direction.SE;
			}
			else
			{
				mLogln("Problems in nextStep Case dx == dy");
				return null;
			}
		}
		else if (dx == 0)
		{
			if(dy < 0) //N
			{
				return Direction.N;
			}
			else if(dy > 0) //S
			{
				return Direction.S;
			}
			else
			{
				mLogln("Problems in nextStep Case dx == 0");
				return null;
			}
		}
		else if(dy == 0)
		{
			if(dx < 0) //W
			{
				return Direction.W;
			}
			else if(dx > 0) //E
			{
				return Direction.E;
			}
			else
			{
				mLogln("Problems in nextStep Case dy == 0");
				return null;
			}
		}

		else if(dx > 0 && dy < 0) // Case 2
		{
			int colorN = Player.color(new int[]{location[0]-1, location[1]}, board);
			int colorE = Player.color(new int[]{location[0], location[1]+1}, board);
			if(sack[colorE] >= sack[colorN])
				return Direction.E;
			return Direction.N;
		}

		else if(dx < 0 && dy > 0) // Case 5
		{
			int colorS = Player.color(new int[]{location[0]+1, location[1]}, board);
			int colorW = Player.color(new int[]{location[0], location[1]-1}, board);
			if(sack[colorS] >= sack[colorW])
				return Direction.S;
			return Direction.W;
		}

		else if(dx < 0 && dy < 0 && Math.abs(dy) > Math.abs(dx)) // Case 1
		{
			int colorN = Player.color(new int[]{location[0]-1, location[1]}, board);
			int colorNW = Player.color(new int[]{location[0]-1, location[1]-1}, board);
			if(sack[colorNW] >= sack[colorN])
				return Direction.NW;
			return Direction.N;
		}

		else if(dx > 0 && dy > 0 && Math.abs(dy) > Math.abs(dx)) // Case 4
		{
			int colorS = Player.color(new int[]{location[0]+1, location[1]}, board);
			int colorSE = Player.color(new int[]{location[0]+1, location[1]+1}, board);
			if(sack[colorS] >= sack[colorSE])
				return Direction.S;
			return Direction.SE;
		}

		else if(dx > 0 && dy > 0 && Math.abs(dx) > Math.abs(dy)) // Case 3
		{
			int colorE = Player.color(new int[]{location[0], location[1]+1}, board);
			int colorSE = Player.color(new int[]{location[0]+1, location[1]+1}, board);
			if(sack[colorE] >= sack[colorSE])
				return Direction.E;
			return Direction.SE;
		}

		else if(dx < 0 && dy < 0 && Math.abs(dx) > Math.abs(dy)) // Case 6
		{
			int colorW = Player.color(new int[]{location[0], location[1]-1}, board);
			int colorNW = Player.color(new int[]{location[0]-1, location[1]-1}, board);
			if(sack[colorW] >= sack[colorNW])
				return Direction.W;
			return Direction.NW;
		}

		else
		{
			mLogln("Problems in nextStep()");
			return null;
		}
	}
	
	public void mLog(Object o)
	{
		if(MOVER_DEBUG && Player.DEBUG)
		{
			System.out.print("mLogDEBUG<P-" + player.name() + "><C-" + Player.color(player.currentLocation, board) + "> " + o);
		}
	}
	public void mLogln(Object o)
	{
		if(MOVER_DEBUG && Player.DEBUG)
		{
			System.out.println("mLogDEBUG<" + player.name() + "> " + o);
		}
	}
}
