package cell.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class Player implements cell.sim.Player, Logger {

	boolean DEBUG= true;
	private int thresHold = 0;
	private int iniMarble = 0;
	private Random gen = new Random();
	private int[] savedSack;
	private Graph graph=null;
	private RouteAnalyzer routeAnalyzer=null;
	private ArrayList<Node> nextSteps= new ArrayList<Node>();
	ArrayList<Path> paths;
	private int turns=1;
	private static int turnsConsole=1;
	private static int instance=0;
	private static int count=0;
	private static HashMap<Node, Integer> currentLocation;

	public void log(String message){
		if(DEBUG)
			System.err.println(message);
	}

	public Player()
	{
		instance++;
	}
	public String name() {
		return "G1";
	}
	
	//Multiple instance will report location to this method every turn.
	public static int console(Node location, int turn){
		if(count==0 || turn!=turnsConsole){
			currentLocation=new HashMap<Node, Integer>();
			turnsConsole=turn;
		}
		count++;
		if(currentLocation.containsKey(location)){
			currentLocation.put(location, currentLocation.get(location)+1);
			return currentLocation.get(location);
		}
		else{
			currentLocation.put(location,1);
			return 1;
		}
	}

	public Direction move(int[][] board, int[] location, int[] sack,
			int[][] players, int[][] traders)
	{
		++turns;
		Direction dir=null;
		if(iniMarble==0){
			iniMarble = sack[0];
		}

		int l = board.length;
		int n1 = (3*l*l+1)/4;
		int p = 0;
		for(int i=0; i<players.length; i++){
			if(players[i]==null)
				continue;
			p++;
		}
		int t = traders.length;
		//thresHold= (int)(l/3*Math.sqrt(p/t));
		thresHold = (int)(Math.sqrt(n1*p/t)*1.414/2) + 1;
		if(thresHold >= iniMarble - 5)
			thresHold = iniMarble - 4;

		savedSack=sack;
		if(graph==null)
		{
			graph=new Graph(board);
		}
		int rank=console(graph.getNode(location),turns);
		if(rank!=1)
			log("Self Detected");

		if(routeAnalyzer==null)
			routeAnalyzer=new RouteAnalyzer(graph);
		/*	for (int i=0; i<traders.length; i++ )
		{
			nextPerTrader.add(graph.getNextStep(location, traders[i]));
		}
		 */

		//int[] closest=graph.nearestTrader(location,traders);
		int[] closest=routeAnalyzer.getDestination1(location,sack, players, traders,rank);
		if(closest==null)
			closest=routeAnalyzer.getDestination2(location,sack, players, traders,rank);
		nextSteps=graph.getNextStep(location, closest);
		/* USE TO TEST PATHS */
		paths=graph.getPath(location, closest);
		if(paths.size()>1)
			log(paths.toString());
		
		Node chosen=null;
		int max=0;
		for (Node n: nextSteps)
		{
			int color=n.getColor();
			if(max<sack[color])
			{
				max=sack[color];
				chosen=n;
			}
		}
		if(chosen==null){
			for (;;) {
				Direction tempdir = randomDirection();
				int[] new_location = move(location, tempdir);
				int color = color(new_location, board);
				if (color >= 0 && sack[color] != 0) {
					chosen=new Node(new_location[0],new_location[1],color);
					break;
				}
			}
		}
		
		log("chosen:"+Arrays.toString(chosen.getLocation()));
		int di=chosen.getLocation()[0]-location[0];
		int dj=chosen.getLocation()[1]-location[1];
		log(di+".."+dj);
		if(di==0 && dj==-1)
		{
			dir=Player.Direction.W;
		} else if(di==0 && dj==1)
		{
			dir=Player.Direction.E;
		} else if(di==-1 && dj==-1)
		{
			dir=Player.Direction.NW;
		} else if(di==-1 && dj==0)
		{
			dir=Player.Direction.N;
		} else if(di==1 && dj==0)
		{
			dir=Player.Direction.S;
		} else if(di==1 && dj==1)
		{
			dir=Player.Direction.SE;
		} else
		{
			System.out.println("You CAN'T move that way!!!");
		}

		if(chosen!=null) 
			savedSack[chosen.color]--;
		return dir;	
	}

	public void trade(double[] rate, int[] request, int[] give)
	{
		log("data" + iniMarble);
		log("threshold" + thresHold);
		TradeAnalyzer ta = new TradeAnalyzer(graph);
		double[] thresholdRatio = ta.getRatio(null, null);
		int[] finalThreshold = new int[6];
		for(int i=0; i<6; i++){
			finalThreshold[i] = (int)(thresHold*thresholdRatio[i]);
		}
		for(int i=0; i<6; i++){
			if(finalThreshold[i]==0 && thresholdRatio[i]!=0)
				finalThreshold[i] = 1;
		}
		int highestIdx = -1;
		int highest = 0;
		for(int i = 0; i<6; i++){
			if(savedSack[i]>highest){highest=savedSack[i]; highestIdx = i;}				
		}
		if(canWin(rate, savedSack))
		{
			for(int i=0; i<6 ;i++){
				if(i==highestIdx)
					give[i] = savedSack[i] - iniMarble*4;
				else
					give[i] = 0;
			}
			for(int i=0; i<6 ;i++){
				if(i==highestIdx)
					request[i] = 0;
				else
					request[i] = iniMarble*4 - savedSack[i];;
			}
			return;
		}

		double rv = 0.0, gv = 0.0;
		double lowestRate=2.1;
		int lowestColor=-1;
		for(int i=0; i<6; i++)
		{
			if(rate[i]<lowestRate)
			{	lowestRate=rate[i];
			lowestColor=i;
			}
		}
		for (int r = 0 ; r != 6 ; ++r)
		{	
			request[r] = give[r] = 0;
			if(savedSack[r] < finalThreshold[r])
			{
				int countR=finalThreshold[r]-savedSack[r];
				request[r]=request[r]+countR;
				rv+=rate[r]*countR;
			}
			else
			{
				if(rate[r]>lowestRate)
				{
					int countG=savedSack[r]-finalThreshold[r];
					give[r]=give[r]+countG;
					gv+=rate[r]*countG;
				}
			}
		}
		int count=0;
		while(rv>gv)
		{
			for(int i = 0; i<6; i++){
				request[i] =0;
				give[i] = 0;
			}
			if(count>3)
				break;
			else ++count;
		}
		while (true) {
			if (rv + rate[lowestColor] >= gv) break;
			request[lowestColor]++;
			rv += rate[lowestColor];
		}
	}

	private boolean canWin(double[] rt,  int[] sack){
		int highestIdx = -1;
		int highest = 0;
		int need = 0;
		int give = 0;
		double base =0.0;
		for(int i = 0; i<6; i++){
			if(sack[i]>highest){highest=sack[i]; highestIdx = i; base=rt[i];}				
		}
		give = (int)((sack[highestIdx] - iniMarble*4)*base);
		for (int i = 0; i<6; i++){
			if(i == highestIdx)
				continue;
			else{
				need+= (int)(iniMarble*4 - sack[i])*rt[i];
			}
		}
		if(give>need)
			return true; 
		else
			return false;
	}

	public Direction moveRandom(int[][] board, int[] location, int[] sack,
			int[][] players, int[][] traders)
	{
		savedSack = copyI(sack);
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

	private int[] copyI(int[] a)
	{
		int[] b = new int [a.length];
		for (int i = 0 ; i != a.length ; ++i)
			b[i] = a[i];
		return b;
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

	private static int color(int[] location, int[][] board)
	{
		int i = location[0];
		int j = location[1];
		int dim2_1 = board.length;
		if (i < 0 || i >= dim2_1 || j < 0 || j >= dim2_1)
			return -1;
		return board[i][j];
	}
}
