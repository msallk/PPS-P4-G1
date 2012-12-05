package cell.g3;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapAnalyzer
{
	public static boolean MAPANALYZER_DEBUG = false;
	int[][] board;
	int radius;
	int[] minRequired;
	Set<Integer[]> visitedLocations;
	int[] currentLocation;
	Player player;
	Map<Map<Integer[], Integer>, Integer> history;
	Map<Integer[], Integer> radMap;

	MapAnalyzer(int[][] board, int[] currentLocation, Player p)
	{
		this.player = p;
		this.board = board;
		this.currentLocation = currentLocation;
		minRequired = new int[6];
		visitedLocations = new HashSet<Integer[]>();
		//visitedLocations.add(wrapIntToIntegerArray(currentLocation));
		radius = calculateRadius();
		history = new HashMap<Map<Integer[], Integer>, Integer>();
		//below line is needed for computing history:
		//radMap = neighborsAtDistance(currentLocation, radius); // = "history.getKey(radius);"
		computeThresholds(currentLocation);
	}

	/**
	 * To decide whether to keep marbles at a radius or just the direct path:
	 * if next trader is closer than this 'radius' -> marbles needed for direct path
	 * if next trader is further or equidistant -> radius = distance
	 * @return
	 */
	private int calculateRadius() {
		if(player.players.length == 1 || player.traders.length == 1)
		{
			return board.length+1;
		}
		
		int tiles = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board.length; j++) {
				if (board[i][j] != -1)
					tiles++;
			}
		}
		int players = 0;
		for(int[] i : player.players)
		{
			if(i != null)
				players++;
		}
		double density = (players + player.traders.length)/tiles;

		if (density > 0.5)
			return 2;
		else if (density > 0.25)
			return 3;
		else if (density > 0.025)
			return 4;
		else
			return 5;
	}

	Map<Integer[], Integer> neighbors(int[] location)
	{
		return neighbors(location, board);
	}

	static Map<Integer[], Integer> neighbors(int[] location, int[][] board)
	{
		Map<Integer[], Integer> returnList = new HashMap<Integer[], Integer>();

		for(Player.Direction d : Player.Direction.values())
		{
			int[] neighbor = Player.move(location, d);
			int color = (Player.color(neighbor, board)); 
			if(color != -1)
			{
				returnList.put(wrapIntToIntegerArray(neighbor), color);
			}
		}
		return returnList;
	}

	static Integer[] wrapIntToIntegerArray(int[] a)
	{
		Integer[] newArray = new Integer[a.length];
		int i = 0;
		for(int value : a)
		{
			newArray[i++] = Integer.valueOf(value);
		}
		return newArray;
	}

	static int[] wrapIntegerToIntArray(Integer[] a)
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


	Map<Integer[], Integer> neighborsAtDistance(int[] location, int radius)
	{
		if(radius == 0)
		{
			maLogln("can't call neighborsAtDistance() for radius 0");
			return null;
		}

		Map<Integer[], Integer> tempNeighborsMap;
		Map<Integer[], Integer> outerNeighborsMap = new HashMap<Integer[], Integer>();

		Map<Integer[], Integer> map = neighbors(location);
		for(Integer[] en : map.keySet())
		{
			visitedLocations.add(en);
		}
		history.put(map, 1);
		for(Integer[] e : map.keySet())
		{
			//maLogln(">>  "+e[0] + " " + e[1]);
		}

		for(int i = 0; i < radius-1; i++)
		{
			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				// maLogln(entry.getValue() + " radius : " + (i+1));
			}
			tempNeighborsMap = new HashMap<Integer[], Integer>();
			outerNeighborsMap = new HashMap<Integer[], Integer>();

			for(Map.Entry<Integer[], Integer> entry : map.entrySet())
			{
				tempNeighborsMap.clear();
				//tempNeighborsMap = new HashMap<Integer[], Integer>();
				tempNeighborsMap.putAll(neighbors(wrapIntegerToIntArray((Integer[])entry.getKey())));
				for(Map.Entry<Integer[], Integer> m : tempNeighborsMap.entrySet())
				{
					if(!Wrap.contains(visitedLocations, m.getKey()) && !Wrap.contains(outerNeighborsMap, m.getKey()))
					{
						outerNeighborsMap.put(m.getKey(), m.getValue());
						visitedLocations.add((Integer[])m.getKey());
						// maLogln("location: " + m.getKey()[1] + "," + m.getKey()[0] + " : color: " + m.getValue() + " ref:" + m);
					}
					//					else
					//					{
					//						int xx = 8; //for debugger/breakpoints
					//						xx = 7;
					//					}
				}
			}

			map = new HashMap<Integer[], Integer>();
			map.clear();
			//map = new HashMap<Integer[], Integer>();
			map.putAll(outerNeighborsMap);
			outerNeighborsMap.clear();
			//outerNeighborsMap = new HashMap<Integer[], Integer>();
			for(Integer[] e : map.keySet())
			{
				//maLogln(">>  "+e[0] + " " + e[1]);
			}
			history.put(map, i+1);
		}

		return map;
	}



	/**
	 * 3 cases:
	 * 1) next trader is closer than 'radius' -> marbles needed for direct path
	 * 2) next trader is further or equidistant -> radius = distance
	 * 3) if no next trader (go to center) -> radius = max diameter of board (board.length)
	 * @param currentLocation
	 * @param radius
	 */
	void computeThresholds(int[] currentLocation)
	{
		
		int[] nextTrader = closestTrader();
		maLogln("us: " + Arrays.toString(currentLocation));
		maLogln("next trader: " + Arrays.toString(nextTrader));
		if(nextTrader == null)
		{
			maLogln("case 1: no next trader");
			radMap = neighborsAtDistance(currentLocation, board.length);
			computeMinThreshold(currentLocation, board.length);
		}
		else if(Mover.distanceBetween(currentLocation, nextTrader, board) >= calculateRadius())
		{
			maLogln("case 2: next trader is further than (or equal) " + calculateRadius());
			radMap = neighborsAtDistance(currentLocation, Mover.distanceBetween(currentLocation, nextTrader, board));
			computeMinThreshold(currentLocation, Mover.distanceBetween(currentLocation, nextTrader, board));
		}
		else
		{
			maLogln("case 3: next trader is closer than  " + calculateRadius());
			//radMap = neighborsAtDistance(currentLocation, Mover.distanceBetween(currentLocation, nextTrader, board));
			if(straightPath(wrapIntToIntegerArray(nextTrader)))
			{
				maLogln("case 3 - straight path");
				maLogln("min required at beginning of case3 straight path: " + Arrays.toString(minRequired));
				Map<Integer[], Integer> tempMap = new HashMap<Integer[], Integer>();
				tempMap.put(wrapIntToIntegerArray(nextTrader), Player.color(nextTrader, board));
				for(Map.Entry<Integer[], Integer> singleEntry : tempMap.entrySet())
				{
					maLogln("single entry: " + Arrays.toString(singleEntry.getKey()));
					int[] count = player.copyI(countSameColorStraightPath(singleEntry));
					//maLogln(Arrays.toString(count));
					for(int i = 0; i < 6; i++)  // number of colors = 6
					{	
						//maLogln("Outside:  i: " + i + " singleEntry.getValue(): " + singleEntry.getValue());
						//maLogln("min required: " + Arrays.toString(minRequired));
						if(count[i] > minRequired[i])
						{
							//maLogln("Inside:  i: " + i + " singleEntry.getValue(): " + singleEntry.getValue());
							minRequired[i] = count[i];
						}
					}
				}
			}
			else
			{
				maLogln("case 3 - crooked path");
				Map<Integer[], Integer> tempMap = new HashMap<Integer[], Integer>();
				tempMap.put(wrapIntToIntegerArray(nextTrader), Player.color(nextTrader, board));
				for(Map.Entry<Integer[], Integer> singleEntry : tempMap.entrySet())
				{
					List<List<Integer[]>> levels = getLevels(wrapIntegerToIntArray(singleEntry.getKey()));
					for(int i = 0; i < 6; i++)
					{
						int color = i;
						maLogln("color " + color);
						int count = countSameColorMultiPath(levels, color);
						if(count > minRequired[color])
						{
							minRequired[color] = count;
						}
					}
				}
			}
		}
	}



	void computeMinThreshold(int[] currentLocation, int radius)
	{

		//straight paths
		//for(Map.Entry<Integer[], Integer> entry : map.entrySet())
		for(Map<Integer[], Integer> hist : history.keySet())
		{
			for(Map.Entry<Integer[], Integer> entry : hist.entrySet())
			{

				if(straightPath((Integer[])entry.getKey()))
				{
					//visitedLocations.add((Integer[])entry.getKey());
					int[] count = countSameColorStraightPath(entry);
					for(int i = 0; i < 6; i++)  // number of colors = 6
					{	
						if(count[i] > minRequired[i])
						{
							minRequired[i] = count[i];
						}
					}
				}
				else
					continue;
			}
		}
		//not straight paths
		int radDEBUG = 1;
		for(Map<Integer[], Integer> hist : history.keySet())
		{
			//maLogln("rad " + radDEBUG++);
			for(Map.Entry<Integer[], Integer> entry : hist.entrySet())
			{
				if(!straightPath((Integer[])entry.getKey()))
				{
					//maLogln(">not straight>  " + entry.getKey()[0] + " " + entry.getKey()[1]);
					//visitedLocations.add((Integer[])entry.getKey());
					List<List<Integer[]>> levels = getLevels(wrapIntegerToIntArray(entry.getKey()));
					for(List<Integer[]> outer : levels)
					{
						for(Integer[] inner : outer)
						{
							//maLog(inner[0] + " " + inner[1] + " | ");
						}
						//maLogln("");
					}
					int count = countSameColorMultiPath(levels, Player.color(wrapIntegerToIntArray(entry.getKey()), board));
					if(count > minRequired[(Integer)entry.getValue()])
					{
						minRequired[(Integer)entry.getValue()] = count;
					}
				}
				//				else
				//					maLogln("Problems in computeMinThreshold()");
			}
		}
	}

	/**
	 * For each entire level that is the same color as the target color, we add 1 to the threshold for that color marble
	 * Example of a level from a to z would be a row:
	 *    a
	 *   [][]
	 *  [][][]
	 *   [][]
	 *    z
	 * @param levels
	 * @param color Target color
	 * @return
	 */
	int countSameColorMultiPath(List<List<Integer[]>> levels, int color)
	{
		int count = 1;
		for(int i = 0; i < levels.size(); i ++)
		{
			boolean isSame = true;
			for(int j = 0; j < levels.get(i).size(); j ++)
			{
				if(Player.color(wrapIntegerToIntArray(levels.get(i).get(j)), player.board) != color)
				{
					isSame = false;
				}
			}
			if(isSame)
			{
				count ++;
			}
		}
		return count;
	}

	/**
	 * 
	 * 6\1| 2
	 *   \|
	 * -------
	 *    |\ 
	 *  5 |4\ 3
	 * 
	 * @param destination
	 * @return
	 */
	List<List<Integer[]>> getLevels(int[] destination) // returns the levels and their co-ordinates
	{
		List<List<Integer[]>> list = new ArrayList<List<Integer[]>>();
		int dy = destination[0] - currentLocation[0];  // switched dx,dy
		int dx = destination[1] - currentLocation[1];
		int noOfLevels;

		if(dx > 0 && dy < 0) // Case 2
		{
			noOfLevels = dx + (-dy) - 1;
			for(int i = 1; i <= noOfLevels; i ++)
			{
				List<Integer[]> levelList = new ArrayList<Integer[]>();
				for(int x_ = 0, y_ = i; (x_ + y_) == i && y_ >= 0; x_ ++, y_ --)
				{
					int[] point = new int[] {currentLocation[0] - y_, currentLocation[1] + x_};
					if(isContainedRectangle(currentLocation, destination, point))
						levelList.add(wrapIntToIntegerArray(point));
				}
				list.add(levelList);
			}
			return list;
		}

		else if(dx < 0 && dy > 0) // Case 5
		{
			noOfLevels = (-dx) + dy - 1;
			for(int i = 1; i <= noOfLevels; i ++)
			{
				List<Integer[]> levelList = new ArrayList<Integer[]>();
				for(int x_ = 0, y_ = i; (x_ + y_) == i && y_ >= 0; x_ ++, y_ --)
				{
					int[] point = new int[] {currentLocation[0] + y_, currentLocation[1] - x_};
					if(isContainedRectangle(destination, currentLocation, point))
						levelList.add(wrapIntToIntegerArray(point));
				}
				list.add(levelList);
			}
			return list;
		}

		else if(dx < 0 && dy < 0 && Math.abs(dy) > Math.abs(dx)) // Case 1
		{
			noOfLevels = Math.abs(dy) - 1;
			for(int i = 1; i <= noOfLevels; i ++)
			{
				List<Integer[]> levelList = new ArrayList<Integer[]>();
				for(int x_ = 0; x_ <= (-dx); x_ ++)
				{
					int[] point = new int[] {currentLocation[0] - i, currentLocation[1] - x_};
					if(isContainedParallelogramHorizontalLevels(destination, currentLocation, point))
						levelList.add(wrapIntToIntegerArray(point));
				}
				list.add(levelList);
			}
			return list;
		}

		else if(dx > 0 && dy > 0 && Math.abs(dy) > Math.abs(dx)) // Case 4
		{
			noOfLevels = Math.abs(dy) - 1;
			for(int i = 1; i <= noOfLevels; i ++)
			{
				List<Integer[]> levelList = new ArrayList<Integer[]>();
				for(int x_ = 0; x_ <= dx; x_ ++)
				{
					int[] point = new int[] {currentLocation[0] + i, currentLocation[1] + x_};
					if(isContainedParallelogramHorizontalLevels(currentLocation, destination, point))
						levelList.add(wrapIntToIntegerArray(point));
				}
				list.add(levelList);
			}
			return list;
		}

		else if(dx > 0 && dy > 0 && Math.abs(dx) > Math.abs(dy)) // Case 3
		{
			noOfLevels = Math.abs(dx) - 1;
			for(int i = 1; i <= noOfLevels; i ++)
			{
				List<Integer[]> levelList = new ArrayList<Integer[]>();
				for(int y_ = 0; y_ <= (dy); y_ ++)
				{
					int[] point = new int[] {currentLocation[0] + y_, currentLocation[1] + i};
					if(isContainedParallelogramVerticalLevels(currentLocation, destination, point))
						levelList.add(wrapIntToIntegerArray(point));
				}
				list.add(levelList);
			}
			return list;
		}

		else if(dx < 0 && dy < 0 && Math.abs(dx) > Math.abs(dy)) // Case 6
		{
			noOfLevels = Math.abs(dx) - 1;
			for(int i = 1; i <= noOfLevels; i ++)
			{
				List<Integer[]> levelList = new ArrayList<Integer[]>();
				for(int y_ = 0; y_ <= (-dy); y_ ++)
				{
					int[] point = new int[] {currentLocation[0] - y_, currentLocation[1] - i};
					if(isContainedParallelogramVerticalLevels(destination, currentLocation, point))
						levelList.add(wrapIntToIntegerArray(point));
				}
				list.add(levelList);
			}
			return list;
		}
		else
		{
			maLogln("Problems in getLevels()");
			return null;
		}
	}

	/**
	 * Is point in parallelogram formed by corners.  Shape:
	 * |\
	 * | \
	 * \ |
	 *  \|
	 *  Used for cases 1,4
	 * @param topLeft Case 1: dest.  Case 4: currentLocation
	 * @param bottomRight Case 1: currentLocation.  Case 4: destination
	 * @param point
	 * @return True if contained in parallemaLogram, false otherwise. not defined if one of the corners
	 */
	boolean isContainedParallelogramHorizontalLevels(int[] topLeft, int[] bottomRight, int[] point)
	{
		//make sure point is not one of the two corners
		if((point[0] == topLeft[0] && point[1] == topLeft[1]) || (point[0] == bottomRight[0] && point[1] == bottomRight[1]))
		{
			maLogln("Problems in isContained()");
			System.exit(1);
		}
		//if point is out of bounds of containing rectangle
		if(point[0] <= topLeft[0] || point[0] >= bottomRight[0] || point[1] < topLeft[1] || point[1] > bottomRight[1])
			return false;

		int DY = Math.abs(topLeft[0] - bottomRight[0]);
		int DX = Math.abs(topLeft[1] - bottomRight[1]);
		int DIFFERENCE = Math.abs(DX - DY);

		int Dy = Math.abs(point[0] - bottomRight[0]);
		int Dx = Math.abs(point[1] - bottomRight[1]);
		int difference = Dy-Dx;

		if(difference <= DIFFERENCE && difference >= 0)
			return true;
		return false;
	}

	/**
	 * Is point in parallelogram formed by corners.  Shape:
	 * ______
	 * \     \
	 *  \_____\
	 * Used for cases 3,6
	 * @param topLeft Case 6: dest.  Case 3: currentLocation
	 * @param bottomRight Case 6: currentLocation.  Case 3: destination
	 * @param point
	 * @return True if contained in parallemaLogram, false otherwise. not defined if one of the corners
	 */
	boolean isContainedParallelogramVerticalLevels(int[] topLeft, int[] bottomRight, int[] point)
	{
		//make sure point is not one of the two corners
		if((point[0] == topLeft[0] && point[1] == topLeft[1]) || (point[0] == bottomRight[0] && point[1] == bottomRight[1]))
		{
			maLogln("Problems in isContained()");
			System.exit(1);
		}
		//if point is out of bounds of containing rectangle
		if(point[0] < topLeft[0] || point[0] > bottomRight[0] || point[1] <= topLeft[1] || point[1] >= bottomRight[1])
			return false;


		int DY = Math.abs(topLeft[0] - bottomRight[0]);
		int DX = Math.abs(topLeft[1] - bottomRight[1]);
		int DIFFERENCE = Math.abs(DX - DY);

		int Dy = Math.abs(point[0] - bottomRight[0]);
		int Dx = Math.abs(point[1] - bottomRight[1]);
		int difference = Dx-Dy;

		if(difference <= DIFFERENCE && difference >= 0)
			return true;
		return false;
	}

	/**
	 * Is point in the rectangle formed by corners
	 * primarily used for hexdrants 2,5
	 * If point is one of the corners, undefined behavior
	 * @param bottomLeft For case 2: currentLocation. For case 5: destination
	 * @param topRight For case 5: currentLocation. For case 2: destination
	 * @param point Point to check
	 * @return True if point is contained in rectangle, false otherwise 
	 */
	boolean isContainedRectangle(int[] bottomLeft, int[] topRight, int[] point)
	{
		//make sure point is not one of the two corners
		if((point[0] == bottomLeft[0] && point[1] == bottomLeft[1]) || (point[0] == topRight[0] && point[1] == topRight[1]))
		{
			maLogln("Problems in isContained()");
			System.exit(1);
		}
		if(point[1] >= bottomLeft[1] && point[1] <= topRight[1] && point[0] >= topRight[0] && point[0] <= bottomLeft[0]) //switched 0/1
			return true;
		return false;
	}


	int[] countSameColorStraightPath(Map.Entry<Integer[], Integer> entry)
	{
		maLogln("countSameColorStraightPath currentLoc " + Arrays.toString(currentLocation) + " argument " + Arrays.toString(entry.getKey()));
		int[] count = new int[6];
		int dy = entry.getKey()[0] - currentLocation[0]; //we've switched dx,dy
		int dx = entry.getKey()[1] - currentLocation[1];
		int color = entry.getValue();

		if(dx == dy)
		{
			if(dx < 0) //NW
			{
				for(int i = 1; i <= (-dx); i ++)
				{
					int[] x = new int[] {currentLocation[0] - i, currentLocation[1] - i}; 
					//if(Player.color(x, board) == color)
					count[Player.color(x, board)] ++;
				}
			}
			else if(dx > 0) //SE
			{
				for(int i = 1; i <= dx; i ++)
				{
					int[] x = new int[] {currentLocation[0] + i, currentLocation[1] + i}; 
					//if(Player.color(x, board) == color)
					count[Player.color(x, board)] ++;
				}
			}
		}
		else if (dx == 0)
		{
			if(dy < 0) //N
			{
				for(int i = 1; i <= Math.abs(dy); i ++)
				{
					int[] x = new int[] {currentLocation[0]-i, currentLocation[1]}; 
					//if(Player.color(x, board) == color)
					count[Player.color(x, board)] ++;
				}
			}
			else if(dy > 0) //S
			{
				for(int i = 1; i <= dy; i ++)
				{
					int[] x = new int[] {currentLocation[0]+i, currentLocation[1]}; 
					//if(Player.color(x, board) == color)
					count[Player.color(x, board)] ++;
				}
			}
		}
		else if(dy == 0)
		{
			if(dx < 0) //W
			{
				for(int i = 1; i <= Math.abs(dx); i ++)
				{
					int[] x = new int[] {currentLocation[0], currentLocation[1]-i}; 
					//if(Player.color(x, board) == color)
					count[Player.color(x, board)] ++;
				}
			}
			else if(dx > 0) //E
			{
				for(int i = 1; i <= dx; i ++)
				{
					int[] x = new int[] {currentLocation[0], currentLocation[1]+i}; 
					//if(Player.color(x, board) == color)
					count[Player.color(x, board)] ++;
				}
			}
		}
		else
			maLogln("Problems in countSameColorStraighPath()");
		maLogln("COUNT: " + Arrays.toString(count));
		return count;
	}

	boolean straightPath(Integer[] locs)
	{
		int dy = locs[0] - currentLocation[0];
		int dx = locs[1] - currentLocation[1];
		if(dx == dy || dx == 0 || dy == 0)
			return true;
		return false;
	}

	public void maLog(Object o)
	{
		if(MAPANALYZER_DEBUG && Player.DEBUG)
		{
			System.out.print("maLogDEBUG<P-" + player.name() + "><C-" + Player.color(currentLocation, board) + "> " + o);
		}
	}
	public void maLogln(Object o)
	{
		if(MAPANALYZER_DEBUG && Player.DEBUG)
		{
			System.out.println("maLogDEBUG<" + player.name() + "> " + o);
		}
	}

	private int[] closestTrader()
	{
		int[] targetLocation = null; //new int[]{player.board.length/2, player.board.length/2};
		Set<Integer[]> visitedLocations = new HashSet<Integer[]>();
		visitedLocations.add(MapAnalyzer.wrapIntToIntegerArray(currentLocation));

		Map<Integer[], Integer> tempNeighborsMap;
		Map<Integer[], Integer> outerNeighborsMap = new HashMap<Integer[], Integer>();

		///first layer of neighbors
		Map<Integer[], Integer> map = MapAnalyzer.neighbors(currentLocation, board);
		for(Map.Entry<Integer[], Integer> en : map.entrySet())
		{
			visitedLocations.add(en.getKey());
			targetLocation = MapAnalyzer.wrapIntegerToIntArray(en.getKey());
			if(isTrader(targetLocation) //&& player.savedSack[en.getValue()] > 0 
					&& !isCloserOrTiedOpponent(targetLocation) 
					&& (currentLocation[0] != targetLocation[0] || currentLocation[1] != targetLocation[1]))
			{
				maLogln("is closer opp: " + isCloserOrTiedOpponent(targetLocation) + " our dist: " + Mover.distanceBetween(currentLocation, targetLocation, board) + " rad: " + 1);
				maLogln("target locations: " + Arrays.toString(targetLocation) + " prev locations: " + Arrays.toString(currentLocation) + " distance " + 1);
				return targetLocation;
			}
		}

		//additional layers of neighbors
		for(int i = 0; i <= player.board.length; i++)
		{
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
						targetLocation = MapAnalyzer.wrapIntegerToIntArray(m.getKey());
						if(isTrader(targetLocation) //&& player.savedSack[m.getValue()] > 0 
								&& !isCloserOrTiedOpponent(targetLocation)
								&& (currentLocation[0] != targetLocation[0] || currentLocation[1] != targetLocation[1]))
						{
							maLogln("is closer opp: " + isCloserOrTiedOpponent(targetLocation) + " our dist: " + Mover.distanceBetween(currentLocation, targetLocation, board) + " i+2: " + (i+2));
							maLogln("target locations: " + Arrays.toString(targetLocation) + " prev locations: " + Arrays.toString(currentLocation) + " distance " + (i+2));
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

		return null;
	}

	private boolean isTrader(int[] location)
	{
		for(int i = 0; i < player.traders.length; i ++)
			if(location[0] == player.traders[i][0] && location[1] == player.traders[i][1])
				return true;
		return false;
	}

	boolean isCloserOrTiedOpponent(int[] traderLoc)
	{
		int[] distances = new int[player.players.length];
		for(int i = 0; i < player.players.length; i++)
		{
			if(player.players[i] == null)
			{
				distances[i] = Integer.MAX_VALUE;
			}
			else
			{
				distances[i] = Mover.distanceBetween(traderLoc, player.players[i], board);
			}
		}
		int ourDist = Mover.distanceBetween(traderLoc, currentLocation, board);
		int count = 0;
		for (int i: distances) {
			if (i <= ourDist)
				count++;
		}

		if (count == 1)
			return false;
		else
			return true;
	}

	private boolean onSameSpot()
	{
		int count = 0;
		for(int[] pl : player.players)
		{
			if(pl != null && pl[0] == currentLocation[0] && pl[1] == currentLocation[1])
				count ++;
			if(count == 2)
				return true;
		}
		return false;
	}


} // end MapAnalyzer class

class Wrap
{
	Integer[] data;
	public Wrap(Integer[] d)
	{
		data = new Integer[d.length];
		for(int i = 0; i < d.length; i++)
		{
			data[i] = d[i];
		}
	}

	static boolean contains(Set<Integer[]> hs, Integer[] entry)
	{
		for(Integer[] y : hs)
		{
			if(new Wrap(y).equals(new Wrap(entry)))
			{
				return true;
			}
		}
		return false;
	}

	static boolean contains(Map<Integer[], Integer> hs, Integer[] key)
	{
		for(Integer[] y : hs.keySet())
		{
			if(new Wrap(y).equals(new Wrap(key)))
				return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wrap other = (Wrap) obj;
		if (!Arrays.equals(data, other.data))
			return false;
		return true;
	}
}