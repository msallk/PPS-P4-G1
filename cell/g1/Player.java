package cell.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Player implements cell.sim.Player, Logger {
	
	boolean DEBUG= false;
	private int thresHold = 0;
	private int iniMarble = 0;
	private Random gen = new Random();
	private int[] savedSack;
	private Graph graph=null;
	private ArrayList<Node> nextSteps= new ArrayList<Node>();

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
		Direction dir=null;
		if(iniMarble==0){
			iniMarble = sack[0];
		}
		
		int l = board.length;
		int n1 = (3*l*l+1)/4;
		int p = players.length;
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
		
	/*	for (int i=0; i<traders.length; i++ )
		{
			nextPerTrader.add(graph.getNextStep(location, traders[i]));
		}
		*/
		
		int[] closest=graph.nearestTrader(location,traders);
		nextSteps=graph.getNextStep(location, closest);
		Node chosen=null;
		int max=0;
		for (Node n: nextSteps)
		{
			int color=color(n.getLocation(),board);
			if(color==-1){
				log("Potential Dest Node: "+Arrays.toString(n.getLocation()));
			}
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
				System.err.println("You CAN'T move that way!!!");
			}
		}
		if(chosen!=null) 
			savedSack[chosen.color]--;
		return dir;
		
	}

	public void trade(double[] rate, int[] request, int[] give)
	{
		log("data" + iniMarble);
		log("threshold" + thresHold);
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
			if(savedSack[r] < thresHold)
			{
				int countR=thresHold-savedSack[r];
				request[r]=request[r]+countR;
				rv+=rate[r]*countR;
			}
			else
			{
				if(rate[r]>lowestRate)
				{
					int countG=savedSack[r]-thresHold;
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
			if(count>5)
				break;
			else ++count;
			/*
			else ++count;
			for(int i=0; i<6; i++)
			{
				if(savedSack[i]>thresHold) //only one color has a count that is more than threshold
				{
					int n=(int)((rv-gv)/rate[i]+1);
					int maxGivable=savedSack[i]-thresHold;
					if(maxGivable>n)
					{
						give[i]+=n;
						gv+=rate[i]*n;
					}
					else
					{
						for(int j=0; j<6; j++)
						{
							if(request[j]>0)
							{
								request[j]=request[j]-1;
								rv=rv-rate[j];
								System.out.println("rv is now: "+rv);
								
							}
						} 
					}
				}
			}*/
			//System.out.print("infinite loops!!!!!!!!");
		}
		if(rate[highestIdx]/rate[lowestColor] < 1.2 
				&& 
				savedSack[highestIdx]< (int)(iniMarble*24/1.25)){
			for(int i = 0; i<6; i++){
				request[i] =0;
				give[i] = 0;
				}
			rv = 0;
			gv = 0;
		}
		while (true) {
			if (rv + rate[lowestColor] >= gv) break;
			request[lowestColor]++;
			rv += rate[lowestColor];
		}
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

}
