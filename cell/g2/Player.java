package cell.g2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import cell.sim.Player.Direction;

public class Player implements cell.sim.Player 
{
	private Random gen = new Random();
	private int[] savedSack;
	private int[] initialSack;
	private boolean checked = false;
	private static int versions = 0;
	private int version = ++versions;
	private Floyd shortest;
	private Trader t;
	int turn_number = 1;
	static int board_size=0;
	private int threshold[] = new int[6];
	private int base_thresh[] = new int[6];
	private int walk_thresh[] = new int[6];
	Direction directions[] = new Direction[5];
	Direction all_dir[] = {Direction.W, Direction.NW, Direction.N, Direction.E, Direction.SE, Direction.S};
	
	//protected OtherPlayers opponents;
	private ArrayList<Integer> possiblePlayerIndex = new ArrayList();
	
	private int[] lockStep;
	
	//private int isolateCount = 0;
	private boolean initialize2 = false;
    //private boolean locatedPlayerIndex = false;
    private int playerIndex;

    private final int MAX_LOCKSTEP_COUNT = 4;

	public String name() 
	{ 
		return "g2" + (version != 1 ? " v" + version : ""); 
	}

	public Direction move(int[][] board, int[] location, int[] sack, int[][] players, int[][] traders)
	{
		//Board[][] contains color of each of the squares in the map
		//location[] contains our location
		//sack[] contains number of ball of each color
		//player[][] contains location of all players at that point of the game when u are moving
		//ie if you are the last player of the game player[][] will contain the location of the players after they have moved for the turn
		//traders[][] contains location of all lep when it is our turn to move
		
		
		if(!initialize2)
        {
            lockStep = new int[players.length];
            for(int i = 0; i < lockStep.length; i++) {
    			if(players[i][0] == location[0] && players[i][1] == location[1]) {
    				playerIndex = i;
    				break;
    			}	
    		}
            initialize2 = true;
        }
		for(int i = 0; i < lockStep.length; i++) {
			if(i != playerIndex && players[i][0] == location[0] && players[i][1] == location[1]) {
				lockStep[i]++;
				if(lockStep[i] > MAX_LOCKSTEP_COUNT)
                {
                    //in lockstep
                    //pick random location
                    for (;;) {
                        Direction dir = randomDirection();
                        int[] new_location = move(location, dir);
                        int color = color(new_location, board);
                        if (color >= 0 && sack[color] != 0) 
                        {
                                savedSack[color]--;
                                return dir;
                        }
                    }
                }
			}
			else
				lockStep[i] = 0;
		}
        /*if(!locatedPlayerIndex)
        {
            locatedPlayerIndex = true;
            
            for(int i = 0; i < players.length; i++)
            {
                if(players[i][0] == location[0] && players[i][1] == location[1])
                    possiblePlayerIndex.add(i);
            }
            if(possiblePlayerIndex.size() < 2)
            {
                //found unique position
                playerIndex = possiblePlayerIndex.get(0);
                locatedPlayerIndex = true;
            }
            else
            {
                //not unique
                isolateCount++;
            }
            
            if(isolateCount > 4)
            {
                //pick random location
                for (;;) {
                    Direction dir = randomDirection();
                    int[] new_location = move(location, dir);
                    int color = color(new_location, board);
                    if (color >= 0 && sack[color] != 0) 
                    {
                            savedSack[color]--;
                            isolateCount = 0;
                            possiblePlayerIndex.clear();
                            return dir;
                    }
                }
                
            }
        }

        if(locatedPlayerIndex)
        {
            for(int i = 0; i < players.length; i++)
            {
                if(i == playerIndex)
                    continue;
                
                if(players[i][0] == location[0] && players[i][1] == location[1])
                {
                    lockStep[i]++;
                }
            }
            for(int i = 0; i < lockStep.length; i++)
            {
                if(lockStep[i] > 0)
                {
                    if(players[i][0] != location[0] && players[i][1] != location[1])
                    {
                        lockStep[i]=0;
                    }
                    if(lockStep[i] > MAX_LOCKSTEP_COUNT)
                    {
                        //in lockstep
                        //pick random location
                        for (;;) {
                            Direction dir = randomDirection();
                            int[] new_location = move(location, dir);
                            int color = color(new_location, board);
                            if (color >= 0 && sack[color] != 0) 
                            {
                                    savedSack[color]--;
                                    isolateCount = 0;
                                    return dir;
                            }
                        }       
                    }
                }
            }
        }*/
		int next_location[]=new int[2];
		/*
		if(turn_number == 1)
		{
			//need to call this only once
			opponents = new OtherPlayers(players.length);
		}
		opponents.addCurrentPosition(players , location);
		*/
		Direction d;
		savedSack = copyI(sack);

		shortest = new Floyd();
		shortest.getShortestPaths(board);
		shortest.getPossiblePaths(board, sack);
		board_size = (board.length+1)/2;
        if(!checked)
        {
        	initialSack = copyI(sack);
        	base_thresh = shortest.getThreshold();
        	if (board_size >= 10 && initialSack[0] < 15) {
    			for (int i=0; i< base_thresh.length; ++i) {
    				if (initialSack[i] > base_thresh[i]) {
    					base_thresh[i] = base_thresh[i] - 4;
    				}
    				else {
    					base_thresh[i] = initialSack[i] - 4;
    				}
    			}
    		}
        	checked = true;
        }
		
		t = new Trader(traders);
		int op_trader = t.getBestTrader(location , shortest);
		int next_node = getBestPath(location, traders[op_trader][0], traders[op_trader][1]);
		next_location = shortest.getCoordinates(next_node);
		d = getDirection(location[0],location[1],next_location[0],next_location[1]);
		/*
		int op_trader = t.getBestTrader(location , shortest,opponents,turn_number);
		int next_node = getBestPath(location, traders[op_trader][0], traders[op_trader][1]);
		next_location = shortest.getCoordinates(next_node);
		*/
		if (sack[color(next_location, board)] == 0) {
			for(int i = 0; i < traders.length; i++) {
				if(traders[i][0] == location[0] && traders[i][1] == location[1]) {
					for (;;) {
                        Direction dir = randomDirection();
                        int[] new_location = move(location, dir);
                        int color = color(new_location, board);
                        if (color >= 0 && sack[color] != 0) 
                        {
                                savedSack[color]--;
                                walk_thresh[color]++;
                                return dir;
                        }
                    }
				}
			}
			getClosestDir(d);
			for (int i=0; i<directions.length; ++i) {
				Direction dir = directions[i];
				int[] new_location = move(location, dir);
				int color = color(new_location, board);
				if (color >= 0 && sack[color] != 0) {
					savedSack[color]--;
					walk_thresh[color]++;
					return dir;
				}
			}
			return d;
		}
		turn_number++;
		savedSack[color(next_location, board)]--;
		walk_thresh[color(next_location, board)]++;
		return d;	
	}
	
	private void getClosestDir(Direction d)
	{
		int d_index = dirIndex(d);
		directions[0] = all_dir[(6 + d_index + 1) % 6];
		directions[1] = all_dir[(6 + d_index - 1) % 6];
		directions[2] = all_dir[(6 + d_index + 2) % 6];
		directions[3] = all_dir[(6 + d_index - 2) % 6];
		directions[4] = all_dir[(6 + d_index + 3) % 6];
	}
	
	private int dirIndex(Direction d)
	{
		if(d == Direction.W)
			return 0;
		if(d == Direction.NW)
			return 1;
		if(d == Direction.N)
			return 2;
		if(d == Direction.E)
			return 3;
		if(d == Direction.SE)
			return 4;
		if(d == Direction.S)
			return 5;
		return 0;
	}

	private int getBestPath(int src_location[],int dest_location1,int dest_location2)
	{
		//Need to check for availability of colors marbles
		//Need to add another function to calculate another path in case the shortest path requires a color for which we do not contain the marble
		int next_node = 0;
		Vector<Integer> v = shortest.getShortestPossiblePath(src_location[0], src_location[1], dest_location1, dest_location2);
		if(v.size() == 0)
			next_node = shortest.getMapping(dest_location1, dest_location2);
		else
		{
			next_node = (Integer)v.elementAt(0);
		}
		return next_node;
	}

	private Direction getDirection(int x1,int y1,int x2,int y2)
	{
		if(x1 == x2 && y1+1 == y2)
			return Direction.E;
		else if(x1 == x2 && y1-1 == y2)
			return Direction.W;
		else if(x1+1 == x2 && y1+1 == y2)
			return Direction.SE;
		else if(x1+1 == x2 && y1 == y2)
			return Direction.S;
		else if(x1-1 == x2 && y1 == y2)
			return Direction.N;
		else //if(x1-1 == x2 && y1-1 == y2)
			return Direction.NW;
//		return null;
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
	
	 private class Rate_Pair implements Comparable
     {
         int i, j;
         double delta;
         private Rate_Pair(int i, int j, double delta)
         {
             this.i = i;
             this.j = j;
             this.delta = delta;
         }
         public int compareTo(Object t) 
         {
             if(((Rate_Pair)t).delta == this.delta)
                 return 0;
             else if(this.delta < ((Rate_Pair)t).delta)
                 return 1;
             else
                 return -1;
         }
     }
     private class Rate implements Comparable
     {
         int i;
         double rate;
         private Rate(int i, double rate)
         {
             this.i = i;
             this.rate = rate;
         }
         public int compareTo(Object t) 
         {
             if(((Rate)t).rate == this.rate)
                 return 0;
             else if(this.rate < ((Rate)t).rate)
                 return 1;
             else
                 return -1;
         }
     }
     public void getMappingData(ArrayList<Rate_Pair> deltaListOverall, ArrayList<Rate_Pair> [] deltaListSpecific, 
             ArrayList<Rate> rateValueList, double [] rate)
     {
         ArrayList<Rate_Pair>deltaList = new ArrayList();
         
         for(int i = 0; i < rate.length; i++)
         {
             ArrayList<Rate_Pair> temp = new ArrayList();
             for(int j = 0; j < rate.length; j++)
             {
                 double deltaValue = 0;
                 if(i == j)
                     continue;
                 
                 deltaValue = Math.abs(rate[i] - rate[j]);
                 Rate_Pair x = new Rate_Pair(i,j,deltaValue);
                 deltaListOverall.add(x);
                 temp.add(x);
             }
             deltaListSpecific[i] = temp;
             Collections.sort(deltaListSpecific[i]);
             rateValueList.add(new Rate(i,rate[i]));
         }
         Collections.sort(deltaListOverall); 
         Collections.sort(rateValueList);
         
     }
	
	private int checkPosWin(double[] rate)
    {
		int count = 0;
		int toWin = 0;
    	for (int i=0; i<savedSack.length; i++) {
    		count += savedSack[i]*rate[i];
    		toWin += initialSack[i]*4*rate[i];
    	}
    	if (count > toWin)
    		return 1;
    	else
    		return 0;
    }
    
	public void trade(double[] rate, int[] request, int[] give)
	{
		for(int i=0; i < threshold.length; i++)
		{
			if (base_thresh[i] > walk_thresh[i])
				threshold[i] = base_thresh[i];
			else
				threshold[i] = walk_thresh[i];
			walk_thresh[i] = 0;
		}
		if (board_size == 10 && initialSack[0] < 15)
			threshold = base_thresh;
        double monies = 0.0;
        int best_deal = 0;
        if(checkPosWin(rate) == 1)
        {
            for(int i = 0; i < savedSack.length; i++)
            {
                give[i] = savedSack[i];
                request[i] = initialSack[i]*4;
            }
        }
        else
        {
            for (int i = 0; i < savedSack.length; ++i)
            {
            	give[i] = 0;
            	request[i] = 0;
            	while (savedSack[i] > threshold[i]) {
            		give[i]++;
            		monies += rate[i];
            		savedSack[i]--;
            	}
            }
            
            for (int i = 0; i < savedSack.length; ++i)
            {
            	while (savedSack[i] < threshold[i] && monies-rate[i] > 0) {
            		request[i]++;
            		savedSack[i]++;
            		monies -= rate[i];
            	}
            }
            
            for (int i = 0; i < rate.length; ++i)
            {
            	if (rate[i] < rate[best_deal])
            		best_deal = i;
            }
            
            while (monies-rate[best_deal] > 0)
            {
            	request[best_deal]++;
            	monies -= rate[best_deal];
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
}