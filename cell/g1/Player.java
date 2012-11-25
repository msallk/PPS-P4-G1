package cell.g1;

import java.util.ArrayList;
import java.util.Random;


public class Player implements cell.sim.Player, Logger {
	
	boolean DEBUG=true;
	private Random gen = new Random();
	private int[] savedSack;
	private Graph graph=null;
	private ArrayList<Node> nextSteps= new ArrayList<>();

	public void log(String message){
		if(DEBUG)
			System.err.println(message);
	}
	
	public String name() {
		return "G1";
	}

	public Direction move(int[][] board, int[] location, int[] sack,
			int[][] players, int[][] traders)
	{
		if(graph==null)
		{
			graph=new Graph(board);
		}
		
	/*	for (int i=0; i<traders.length; i++ )
		{
			nextPerTrader.add(graph.getNextStep(location, traders[i]));
		}
		*/
		
		int[] closest=graph.nearestTrader(location,traders);
		nextSteps=graph.getNextStep(location, closest);
		Node chosen=null;
		int max=0;
		Direction dir=null;
		for (Node n: nextSteps)
		{
			int color=color(n.getLocation(),board);
			if(max<sack[color])
			{
				max=sack[color];
				chosen=n;
			}
		}
		
		
		if(chosen==null)
		{
			System.err.println("Oops! Ran out of marbles, now YOU ARE DEAD!!!!");
		}
		else
		{
			int di=chosen.getLocation()[0]-location[0];
			int dj=chosen.getLocation()[0]-location[1];
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
				System.err.println("You CAN'T move that way!!! Either Jiang Wu or Tianchen Yu screwed up!");
			}
		}
		
		return dir;
		
		
		/*ArrayList<Path> min=null;
		for (ArrayList<Path> path: pathsPerTrader)
		{
			if(min==null)
			{
				min=path;
			}
			else
			{
				if(path.get(0).length<min.get(0).length)
				{
					min=path; 
				}
			}			
		}		
		Path chosenPath=min.get(0);  */		
		/*for (;;) {
			Direction dir = randomDirection();
			int[] new_location = move(location, dir);*/

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
		double rv = 0.0, gv = 0.0;
		for (int r = 0 ; r != 6 ; ++r)
		{	request[r] = give[r] = 0;
			if(savedSack[r] < 3)
			{
				int count=3-savedSack[r];
				request[r]=count;
				rv+=rate[r]*count;
			}
		}
		
		for (int i = 0 ; i != 10 ; ++i) {
			int j = gen.nextInt(6);
			if (give[j] == savedSack[j]) break;
			give[j]++;
			gv += rate[j];
		}
		for (;;) {
			int j = gen.nextInt(6);
			if (rv + rate[j] >= gv) break;
			request[j]++;
			rv += rate[j];
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

	private int[] copyI(int[] a)
	{
		int[] b = new int [a.length];
		for (int i = 0 ; i != a.length ; ++i)
			b[i] = a[i];
		return b;
	}

}
