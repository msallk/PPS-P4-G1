package cell.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Graph implements Logger{
	private int[][] map;
	private Node[][] mapNodes;
	private int[][] traders; 
	
	public void log(String m){
		System.err.println(m);
	}
	public Graph(int[][] map){
		this.map=map;
		mapNodes=new Node[map[0].length][map.length];
		for(int i=0;i<map.length;i++)
			for(int j=0;j<map[0].length;j++){
				mapNodes[i][j]=new Node(i,j,map[i][j]);
			}
	}

	public int[] nearestTrader(int[] current, int[][] t){
		this.traders=t;
		int distance=Integer.MAX_VALUE;
		int [] trader=new int[2];
		for(int i=0;i<t.length;i++){
			log("min distance: "+distance);
			log("trader: "+Arrays.toString(trader));
			int temp=getDistance(current,t[i]);
			log("next trader: "+Arrays.toString(t[i]));
			log("new distance: "+temp);
			if(temp<distance){
				trader=t[i];
				distance=temp;
			}
		}
		return trader;
	}
	
	public int getDistance(int[] curr, int[] trader){
		int x1=curr[1];
		int y1=curr[0];
		int x2=trader[1];
		int y2=trader[0];
		if(y1==y2){
			return Math.abs(x1-x2);
		}
		else if(y1>y2){
			if(x1>x2){
				return Math.abs(y1-y2)+((x1-x2)<=(y1-y2)?0:((x1-x2)-(y1-y2)));
			}else{
				return Math.abs(y1-y2)+Math.abs(x1-x2);
			}
		}
		else{
			if(x1<x2){
				return Math.abs(y1-y2)+((x2-x1)<=(y2-y1)?0:(x2-x1)-(y2-y1));
			}else{
				return Math.abs(y1-y2)+Math.abs(x1-x2);
			}
		}
	}
	
	
	public ArrayList<Path> getPath(int[] current, int[] dest){
		ArrayList<Path> paths=new ArrayList<Path>();
		BFS(current, dest, paths);
		Collections.sort(paths);
		return paths;
	}
	
	public ArrayList<Node> getNextStep(int[] current, int[] dest){
		int x1=current[1];
		int y1=current[0];
		int x2=dest[1];
		int y2=dest[0];
		log("curr: "+Arrays.toString(current));
		log("dest: "+Arrays.toString(dest));
		ArrayList<Node> nodes=new ArrayList<Node>();
		if(x1==x2 && y1==y2){
			nodes.add(mapNodes[y1][x1]);
			return nodes;
		}
		if(y1==y2 && x1>x2){
			nodes.add(mapNodes[y1][x1-1]);
			return nodes;
		}
		if(y1==y2 && x1<x2){
			nodes.add(mapNodes[y1][x1+1]);
			return nodes;
		}
		if(y1!=y2){
			if(getDistance(current,dest)==1){
				nodes.add(mapNodes[y2][x2]);
				return nodes;
			}
			if(y1>y2 && x1>x2 && (y1-y2)>=(x1-x2)){
				nodes.add(mapNodes[y1-1][x1-1]);
				return nodes;
			}
			if(y1<y2 && x1<x2 && (y2-y1)>=(x2-x1)){
				nodes.add(mapNodes[y1+1][x1+1]);
				return nodes;
			}
			if(y1<y2){
				nodes.add(mapNodes[y1+1][x1]);
			}else{
				nodes.add(mapNodes[y1-1][x1]);
			}
			if(x1>x2){
				nodes.add(mapNodes[y1][x1-1]);
			}else{
				nodes.add(mapNodes[y1][x1+1]);
			}
			return nodes;
		}
		return null;
	}
	
	private void BFS(int[] current, int[] dest, ArrayList<Path> paths){
		
	}

}

