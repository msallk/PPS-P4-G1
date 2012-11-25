package cell.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Graph {
	private int[][] map;
	private Node[][] mapNodes;
	private int[][] traders; 
	
	public Graph(int[][] map){
		this.map=map;
		mapNodes=new Node[map[0].length][map.length];
		for(int i=0;i<map[0].length;i++)
			for(int j=0;j<map.length;j++){
				mapNodes[i][j]=new Node(i,j,map[i][j]);
			}
	}

	public int[] NearestTrader(int[][] t){
		this.traders=t;
		return null;
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
		if(y1>y2){
			
		}else{
			
		}
		return null;
	}
	
	private void BFS(int[] current, int[] dest, ArrayList<Path> paths){
		
	}

}

