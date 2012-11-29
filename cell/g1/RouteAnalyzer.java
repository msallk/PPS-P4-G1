package cell.g1;

import java.util.Arrays;

public class RouteAnalyzer implements Logger{
	Graph g;
	public RouteAnalyzer(Graph g){
		this.g=g;
	}

	//TODO: get the best leprechaun location. Make sure we can get there faster than others 
	public int[] getDestination1(int[] current, int[] sack,	int[][] players, int[][] traders){
		int distance=Integer.MAX_VALUE;
		int [] trader=null;
		int [] nearest=new int[2];
		for(int i=0;i<traders.length;i++){
			log("min distance: "+distance);
			log("trader: "+Arrays.toString(trader));
			int temp=g.getDistance(current,traders[i]);
			log("next trader: "+Arrays.toString(traders[i]));
			log("new distance: "+temp);
			if(temp<distance && checkOthers1(traders[i],players, temp)){
				trader=traders[i];
				distance=temp;
			}
		}
		if(trader==null)
			trader=getAlternative(current,sack, players);
		return trader;
	}

	public int[] getDestination2(int[] current, int[] sack,	int[][] players, int[][] traders){
		int distance=Integer.MAX_VALUE;
		int [] trader=null;
		int [][] others=checkOthers2(traders, players);
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
						if(g.getDistance(players[j], o)<temp)
							near=false;
					}
				}
				if(near){
					trader=traders[i];
					distance=temp;
				}
			}
		}
		if(trader==null)
			trader=getAlternative(current,sack, players);
		return trader;
	}

	private int[] getAlternative(int[] current, int[] sack, int[][] players) {
		return null;
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
			if(g.getDistance(p, trader)<distance)
				return false;
		}
		return true;
	}

	@Override
	public void log(String message) {
		System.err.println(message);
	}
}
