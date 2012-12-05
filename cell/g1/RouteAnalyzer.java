package cell.g1;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

public class RouteAnalyzer implements Logger{
	Graph g;
	boolean DEBUG=false;
	TreeMap<Integer,int[]> traderMap;
	public RouteAnalyzer(Graph g){
		this.g=g;
	}

	//TODO: get the best leprechaun location. Make sure we can get there faster than others 
	public int[] getDestination1(int[] current, int[] sack,	int[][] players, int[][] traders,int priority){
		int distance=Integer.MAX_VALUE;
		int [] trader=null;
		TreeMap<Integer,int[]> sortedTraders=new TreeMap<Integer,int[]>();
		traderMap=new TreeMap<Integer,int[]>();
		for(int i=0;i<traders.length;i++){
			log("min distance: "+distance);
			log("trader: "+Arrays.toString(trader));
			int temp=g.getDistance(current,traders[i]);
			log("next trader: "+Arrays.toString(traders[i]));
			log("new distance: "+temp);
			traderMap.put(temp, traders[i]);
			if(temp<distance && checkOthers1(traders[i],players, temp)){
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

	private boolean checkOthers1(int[] trader, int[][] players, int distance) {
		for(int[] p:players){
			if(p==null)
				continue;
			if(g.getDistance(p, trader)<distance)
				return false;
		}
		return true;
	}

	@Override
	public void log(String message) {
		if(DEBUG)
			System.err.println(message);
	}
}
