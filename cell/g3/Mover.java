package cell.g3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cell.sim.Player.Direction;

public 	class Mover
{
	int[][] board;
	int[] location;
	int[][] traders;
	int[] targetLocation;
	int[] sack;
	Direction nextStep;

	Mover(int[] location, int[][] board, int[][] traders, int[] savedSack)
	{
		this.board = board;
		this.sack = savedSack;
		this.location = location;
		this.traders = traders;
		targetLocation = closestTrader();
		nextStep = nextStep();
	}

	public Direction getNextStep() {
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

	//TODO: Rank the leprechauns based on proximity to other leps or other players around it and color needed.  dont just pick arbitrarily  
	private int[] closestTrader()
	{
		int[] targetLocation;
		Set<Integer[]> visitedLocations = new HashSet<Integer[]>();
		visitedLocations.add(MapAnalyzer.wrapIntToIntegerArray(location));

		Map<Integer[], Integer> tempNeighborsMap;
		Map<Integer[], Integer> outerNeighborsMap = new HashMap<Integer[], Integer>();

		///first layer of neighbors
		Map<Integer[], Integer> map = MapAnalyzer.neighbors(location, board);
		for(Integer[] en : map.keySet())
		{
			visitedLocations.add(en);
			if(isTrader(MapAnalyzer.wrapIntegerToIntArray(en)))
				return targetLocation = MapAnalyzer.wrapIntegerToIntArray(en);
		}

		for(int i = 0; ; i++)
		{
			if(i > 1000)
			{
//				System.out.println("DEBUG Problem in Looping closestTrader()");
				System.exit(1);
			}
			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
//				System.out.println(entry.getValue() + " radius : " + (i+1));
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
//						System.out.println("location: " + m.getKey()[0] + "," + m.getKey()[1] + " : color: " + m.getValue() + " ref:" + m);
						if(isTrader(MapAnalyzer.wrapIntegerToIntArray(m.getKey())) && sack[m.getValue()] > 0)
							return targetLocation = MapAnalyzer.wrapIntegerToIntArray(m.getKey());
					}
				}
			}

			map.clear();
			//map = new HashMap<Integer[], Integer>();
			map.putAll(outerNeighborsMap);
			outerNeighborsMap.clear();
			//outerNeighborsMap = new HashMap<Integer[], Integer>();
		}

		//return targetLocation;
	}

	/**
	 * If don't have the marble needed to make the move, keep on randomly picking new direction
	 * @param firstAttempt
	 * @return
	 */
	private Direction nextStepWrapper(Direction firstAttempt)
	{
		Random gen = new Random();
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
					{
						Direction dir;
						do 
						{
							dir = Player.randomDirection(gen);
							if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
								return dir;
						}
						while(sack[Player.color(Player.move(location, dir), board)] <= 0);
					}
				}
				case S: 
				{
					if(Player.color(Player.move(location, Direction.SE), board) >= 0 && sack[Player.color(Player.move(location, Direction.SE), board)] > 0)
						return Direction.SE;
					else if(Player.color(Player.move(location, Direction.W), board) >= 0 && sack[Player.color(Player.move(location, Direction.W), board)] > 0)
						return Direction.W;
					else 
					{
						Direction dir;
						do 
						{
							dir = Player.randomDirection(gen);
							if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
								return dir;
						}
						while(sack[Player.color(Player.move(location, dir), board)] <= 0);
					}
				}
				case W: 
				{
					if(Player.color(Player.move(location, Direction.NW), board) >= 0 && sack[Player.color(Player.move(location, Direction.NW), board)] > 0)
						return Direction.NW;
					else if(Player.color(Player.move(location, Direction.S), board) >= 0 && sack[Player.color(Player.move(location, Direction.S), board)] > 0)
						return Direction.S;
					else 
					{
						Direction dir;
						do 
						{
							dir = Player.randomDirection(gen);
							if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
								return dir;
						}
						while(sack[Player.color(Player.move(location, dir), board)] <= 0);
					}
				}
				case E: 
				{
					if(Player.color(Player.move(location, Direction.N), board) >= 0 && sack[Player.color(Player.move(location, Direction.N), board)] > 0)
						return Direction.N;
					else if(Player.color(Player.move(location, Direction.SE), board) >= 0 && sack[Player.color(Player.move(location, Direction.SE), board)] > 0)
						return Direction.SE;
					else 
					{
						Direction dir;
						do 
						{
							dir = Player.randomDirection(gen);
							if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
								return dir;
						}
						while(sack[Player.color(Player.move(location, dir), board)] <= 0);
					}
				}
				case NW: 
				{
					if(Player.color(Player.move(location, Direction.N), board) >= 0 && sack[Player.color(Player.move(location, Direction.N), board)] > 0)
						return Direction.N;
					else if(Player.color(Player.move(location, Direction.W), board) >= 0 && sack[Player.color(Player.move(location, Direction.W), board)] > 0)
						return Direction.W;
					else 
					{
						Direction dir;
						do 
						{
							dir = Player.randomDirection(gen);
							if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
								return dir;
						}
						while(sack[Player.color(Player.move(location, dir), board)] <= 0);
					}
				}
				case SE: 
				{
					if(Player.color(Player.move(location, Direction.E), board) >= 0 && sack[Player.color(Player.move(location, Direction.E), board)] > 0)
						return Direction.E;
					else if(Player.color(Player.move(location, Direction.S), board) >= 0 && sack[Player.color(Player.move(location, Direction.S), board)] > 0)
						return Direction.S;
					else 
					{
						Direction dir;
						do 
						{
							dir = Player.randomDirection(gen);
							if(Player.color(Player.move(location, dir), board) >= 0 && sack[Player.color(Player.move(location, dir), board)] > 0)
								return dir;
						}
						while(sack[Player.color(Player.move(location, dir), board)] <= 0);
					}
				}
				default:
				{
//					System.out.println("DEBUG problem in switch case");
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
//			System.out.println("DEBUG problem with nextStep()");
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
//				System.out.println("DEBUG Problems in nextStep Case dx == dy");
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
//				System.out.println("DEBUG Problems in nextStep Case dx == 0");
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
//				System.out.println("DEBUG Problems in nextStep Case dy == 0");
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
//			System.out.println("DEBUG Problems in nextStep()");
			return null;
		}
	}
}
