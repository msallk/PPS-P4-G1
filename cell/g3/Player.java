package cell.g3;

import java.util.Random;

public class Player implements cell.sim.Player {

	public static boolean DEBUG = true;
	public static boolean PLAYER_DEBUG = false;
	public Random gen = new Random();
	public int[] savedSack;
	public static int versions = 0;
	public int version = ++versions;
	public int quadCount = 0;
	public int[][] board;
	public int[] currentLocation;
	public int[] nextLocation;
	public int[][] traders;
	public int[][] players;
	
	//TODO: Map-analysis, so as to generate the nextThreshold --> threshold for getting to the immediate next leprechaun
											//	globalThreshod --> depends on the state of the sack and location on map
											// 	to decide what to use.. next or global...
//	public int[] maxNextThreshold = new int[] {9,9,9,9,9,9};
	public int[] minNextThreshold = new int[] {-1,-1,-1,-1,-1,-1};
	

	public String name() { return "G3" + (version != 1 ? " v" + version : ""); }

	public Direction move(int[][] board, int[] location, int[] sack,
	                      int[][] players, int[][] traders)
	{
		this.players = copyII(players);
		this.traders = copyII(traders);
		this.board = copyII(board);
		this.currentLocation = copyI(location);
		pLogln("PLAYER MOVE fn");
		savedSack = copyI(sack);
		
		if (quadCount == 0)
			quadCount = 4 * sack[0];
		
		Mover mover = new Mover(this);
		for (;;) {
			Direction dir = mover.getNextStep();
			int[] new_location = move(location, dir);
			int color = color(new_location, board);
			pLogln("new loc: " + new_location + ". color: " + color + ". No of that color: " + sack[color]);
			if (color >= 0 && sack[color] != 0) {
				savedSack[color]--;
				nextLocation = move(currentLocation, dir); //store next location to compute min thresholds
				return dir;
			}
		}
	}

	public static Direction randomDirection(Random gen)
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
		Trader trader = new Trader(this);
		
		MapAnalyzer ma = new MapAnalyzer(board, nextLocation, this);
		minNextThreshold = ma.getMinRequired();
		///
		pLogln("current: " + currentLocation + ". next: " + nextLocation);
		for(int i : minNextThreshold)
			pLogln("min threshhold for" + name() + " " + i);
		///
		
		
		//Winning trade... the final step to victory !
		if (trader.winningTrade(rate, request, give))
			return;
		
		//Threshold decision -- next or global
		//Use sortedIndices(rate) to get an array of indices of rate, sorted by rate in ascending order. 
		//TODO: Jama implementation for more than 1 color trading

		//Greedy trade that also saves some colors.
		trader.greedyTrade(rate, request, give);
	}

	public static int[] move(int[] location, Player.Direction dir)
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
	
	public int maxIndex(double[] a)
	{
		int maxIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] > a[maxIndex])
				maxIndex = i;
		}
		return maxIndex;
	}
	
	public int minIndex(double[] a)
	{
		int minIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] < a[minIndex])
				minIndex = i;
		}
		return minIndex;
	}
	
	public int maxIndex(int[] a)
	{
		int maxIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] > a[maxIndex])
				maxIndex = i;
		}
		return maxIndex;
	}
	
	public int minIndex(int[] a)
	{
		int minIndex = 0;
		for(int i = 0; i < a.length; i ++)
		{
			if(a[i] < a[minIndex])
				minIndex = i;
		}
		return minIndex;
	}
	
	public int total(int[] a)
	{
		int total = 0;
		for(int i = 0; i < a.length; i ++)
		{
			total += a[i];
		}
		return total;
	}
	
	public static int color(int[] location, int[][] board)
	{
		int i = location[0];
		int j = location[1];
		int dim2_1 = board.length;
		if (i < 0 || i >= dim2_1 || j < 0 || j >= dim2_1)
			return -1;
		return board[i][j];
	}

	public int[] copyI(int[] a)
	{
		int[] b = new int [a.length];
		for (int i = 0 ; i != a.length ; ++i)
			b[i] = a[i];
		return b;
	}
	
	public int[][] copyII(int[][] a)
	{
		int[][] b = new int [a.length][a[0].length];
		for (int i = 0 ; i != a.length ; ++i)
			for(int j = 0; j != a[i].length; ++j)
				b[i][j] = a[i][j];
		return b;
	}

	public int[] sortedIndices(double[] a)
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
	
	public int[][] getBoard() {
		return board;
	}
	
	public Random getGen() {
		return gen;
	}

	public int[] getSavedSack() {
		return savedSack;
	}

	public static int getVersions() {
		return versions;
	}

	public int getVersion() {
		return version;
	}

	public int getQuadCount() {
		return quadCount;
	}

	public int[] getCurrentLocation() {
		return currentLocation;
	}

	public int[] getNextLocation() {
		return nextLocation;
	}

//	public int[] getMaxNextThreshold() {
//		return maxNextThreshold;
//	}

	public int[] getMinNextThreshold() {
		return minNextThreshold;
	}
	
	public void pLog(Object o)
	{
		if(PLAYER_DEBUG && DEBUG)
		{
			System.out.print("pLogDEBUG<P-" + name() + "><C-" + color(currentLocation, board) + "> " + o);
		}
	}
	public void pLogln(Object o)
	{
		if(PLAYER_DEBUG && DEBUG)
		{
			System.out.println("pLogDEBUG<" + name() + "> " + o);
		}
	}
}