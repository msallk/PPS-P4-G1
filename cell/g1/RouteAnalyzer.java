package cell.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class RouteAnalyzer implements Logger{
	Graph g;
	boolean DEBUG=false;
	TreeMap<Integer,int[]> traderMap;
	private int selfNumber=-1;
	public RouteAnalyzer(Graph g){
		this.g=g;
	}

	public void findSelfNumber(int[] self, int[][] players)
	{
		for(int i=0; i<players.length; i++)
		{
			if(players[i]==null)
				continue;
			if(players[i][0]==self[0] && players[i][1]==self[1])
			{	
				selfNumber=i;
				return;
			}
			
		}
	}
	
	
	//TODO: get the best leprechaun location. Make sure we can get there faster than others 
	public int[] getDestination1(int[] current, int[] sack,	int[][] players, int[][] traders,int priority,boolean checkNextStep){
		int distance=Integer.MAX_VALUE;
		int [] trader=null;
		TreeMap<Integer,int[]> sortedTraders=new TreeMap<Integer,int[]>();
		traderMap=new TreeMap<Integer,int[]>();
		for(int i=0;i<traders.length;i++){
			log("min distance: "+distance);
			log("trader: "+Arrays.toString(trader));
			int temp=g.getDistance(current,traders[i]);
			if(temp==0)
				continue;
			log("next trader: "+Arrays.toString(traders[i]));
			log("new distance: "+temp);
			traderMap.put(temp, traders[i]);
			if(temp<distance && checkOthers1(traders[i],players, temp,checkNextStep)){
				sortedTraders.put(temp,traders[i]);
				trader=traders[i];
				distance=temp;
			}
			
		}
		if(priority<=sortedTraders.size()){
			Set<Integer> keys=sortedTraders.keySet();
			Iterator<Integer> iterator=keys.iterator();
			for(int i=0;i<priority;i++){
				trader=sortedTraders.get(iterator.next());
			}
		}
		return trader;
	}

	public int[] getDestination2(int[] current, int[] sack,	int[][] players, int[][] traders,int priority){
		int distance=Integer.MAX_VALUE;
		int [] trader=null;
		int [][] others=checkOthers2(traders, players);
		TreeMap<Integer,int[]> sortedTraders=new TreeMap<Integer,int[]>();
		for(int i=0;i<traders.length;i++){
			log("min distance: "+distance);
			log("trader: "+Arrays.toString(trader));
			int temp=g.getDistance(current,traders[i]);
			log("next trader: "+Arrays.toString(traders[i]));
			log("new distance: "+temp);
			if(temp<distance){
				boolean near=true;
				for(int j=0;j<others.length;j++){
					int[] o=others[j];
					if(o[0]==traders[i][0] && o[1]==traders[i][1]){
						if(players[j]!=null)
							if(g.getDistance(players[j], o)<temp)
								near=false;
					}
				}
				if(near){
					trader=traders[i];
					distance=temp;
					sortedTraders.put(temp,traders[i]);
				}
			}
		}
		if(priority<=sortedTraders.size()){
			Set<Integer> keys=sortedTraders.keySet();
			Iterator<Integer> iterator=keys.iterator();
			for(int i=0;i<priority;i++){
				trader=sortedTraders.get(iterator.next());
			}
		}
		if(trader==null)
			trader=getAlternative(current,sack, traders);
		return trader;
	}

	private int[] getAlternative(int[] current, int[] sack, int[][] traders) {
		return getNearest(traders,current);
	}

	private int[][] checkOthers2(int[][] traders, int[][] players) {
		int[][] others=new int[players.length][2];
		for(int i=0;i<players.length;i++){
			if(players[i]!=null)
				others[i]=getNearest(traders,players[i]);

		}
		return others;
	}

	private int[] getNearest(int[][] traders, int[] player) {
		int distance=Integer.MAX_VALUE;
		int [] trader=new int[2];
		for(int i=0;i<traders.length;i++){
			log("min distance: "+distance);
			log("trader: "+Arrays.toString(trader));
			int temp=g.getDistance(player,traders[i]);
			log("next trader: "+Arrays.toString(traders[i]));
			log("new distance: "+temp);
			if(temp<distance){
				trader=traders[i];
				distance=temp;
			}
		}
		return trader;
	}

	private boolean checkOthers1( int[] trader, int[][] players, int distance, boolean checkNextStep) {
		int count=0;
		for(int[] p:players){
			if(p==null)
				continue;
			if(p[0]==players[selfNumber][0] && p[1]==players[selfNumber][1])
			{	
				if(count==0)
				{	
					count++;
					continue;			
				}		
			}
			int modifier=0;
			if(checkNextStep)
				modifier=1;
			if(g.getDistance(p, trader)-modifier<=distance)
				return false;
		}
		return true;
	}
	
	//given a set of paths, return the first path that is affordable
	public Path getAffordable(int[] sack, ArrayList<Path> paths)
	{
		for(Path p: paths)
		{
			int[] reqColors={0,0,0,0,0,0};
			ArrayList<Node> nodes=p.locs;
			//starts from 1 since path includes the starting point
			for(int j=1; j<nodes.size(); j++)
			{
				int c=nodes.get(j).getColor();
				reqColors[c]++;
			}
			for(int i=0; i<6; i++)
			{
				if(sack[i]<reqColors[i])
					break;
				else if(i==5)
					return p;		
			}
		}
		return null;
	}
	

	public Path findCheapest(ArrayList<Path> paths, double[] rate) {
		Path cheapest=null;
		double min=Double.MAX_VALUE;
		for(Path p: paths)
		{
			double totalV=0.0;
			for (Node n: p.locs)
				totalV+=rate[n.color];
			if(totalV<min)
			{	
				min=totalV;	
				cheapest=p;
			}
		}
		return cheapest;
	}

	@Override
	public void log(String message) {
		if(DEBUG)
			System.err.println(message);
	}

}
