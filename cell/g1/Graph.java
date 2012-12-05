package cell.g1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Graph implements Logger{
	private int[][] map;
	private Node[][] mapNodes;
	private int[][] traders; 
	private boolean DEBUG=false;

	public void log(String m){
		if(DEBUG)
			System.err.println(m);
	}
	
	public Node[][] getMapnodes(){
		return mapNodes;
	}
	public Graph(int[][] map){
		this.map=map;
		mapNodes=new Node[map[0].length][map.length];
		for(int i=0;i<map.length;i++)
			for(int j=0;j<map[0].length;j++){
				mapNodes[i][j]=new Node(i,j,map[i][j]);
			}
	}
	
	public Node getNode(int[] location){
		return mapNodes[location[0]][location[1]];
	}

	/*
	 * Method returns the number of each colored tile on
	 * the map.
	 */
	public int[] graphColors(){
		int[] graphColors = { 0, 0, 0, 0, 0, 0};
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				int color = map[i][j];
				graphColors[color]++;
			}
		}
		return graphColors;
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
	//not used now
	public int getDistance2(int[] curr, int[] trader){
		int x1=curr[1];
		int y1=curr[0];
		int x2=trader[1];
		int y2=trader[0];
		if(y1==y2){
			return Math.abs(x1-x2);
		}
		else if(x1==x2){
			return Math.abs(y1-y2);
		}
		else if((x1-x2)==(y1-y2)){
			return Math.abs(y1-y2);
		}
		else{
			return 1;
		}
	}

	//NOT IN USE
	public ArrayList<Path> getPath(int[] current, int[] dest){
		ArrayList<Path> paths=new ArrayList<Path>();
		buildPaths(current,(int[])dest.clone(),paths,null);
		Collections.sort(paths);
		return paths;
	}

	private void buildPaths(int[] curr, int[] dest, ArrayList<Path> paths, Path p){
		if(p==null)
			p=new Path();
		p.add(mapNodes[curr[0]][curr[1]]);
		if(curr[0]==dest[0] && curr[1]==dest[1]){
			paths.add((Path)p.clone());
			p.remove(mapNodes[curr[0]][curr[1]]);
			return;
		}
		for(Node b:getNextStep(curr,dest)){
			//log(getNextStep(curr,dest).toString());
			if(!b.equals(mapNodes[curr[0]][curr[1]]))
				if(getDistance(b.getLocation(),dest)<getDistance(curr,dest))
					buildPaths(b.getLocation(),dest,paths,p);
		}
		p.remove(mapNodes[curr[0]][curr[1]]);
	}

	public ArrayList<Node> getNextStep(int[] current, int[] dest){
		int x1=current[1];
		int y1=current[0];
		int x2=dest[1];
		int y2=dest[0];
		log("curr: "+Arrays.toString(current));
		log("dest: "+Arrays.toString(dest));
		ArrayList<Node> nodes=new ArrayList<Node>();
		//Already at destination
		if(x1==x2 && y1==y2){
			nodes.add(mapNodes[y1][x1]);
			return nodes;
		}
		//same line
		if(y1==y2 && x1>x2){
			if(mapNodes[y1][x1-1].color!=-1)
				nodes.add(mapNodes[y1][x1-1]);
			return nodes;
		}
		if(y1==y2 && x1<x2){
			if(mapNodes[y1][x1+1].color!=-1)
				nodes.add(mapNodes[y1][x1+1]);
			return nodes;
		}
		//different line
		if(y1!=y2){
			//one step away
			if(getDistance(current,dest)==1){
				nodes.add(mapNodes[y2][x2]);
				return nodes;
			}
			//1st diagonal
			if(y1>y2 && (y1-y2)==(x1-x2)){
				nodes.add(mapNodes[y1-1][x1-1]);
				return nodes;
			}
			if(y1<y2 && (y2-y1)==(x2-x1)){
				nodes.add(mapNodes[y1+1][x1+1]);
				return nodes;
			}
			//2nd diagonal
			if(x1==x2 && y1>y2){
				nodes.add(mapNodes[y1-1][x1]);
				return nodes;
			}
			if(x1==x2 && y1<y2){
				nodes.add(mapNodes[y1+1][x1]);
				return nodes;
			}
			//not on diagonal
			if(y1<y2 && x1<x2){
				if(x2-x1==1){
					if(mapNodes[y1+1][x1].color!=-1)
						nodes.add(mapNodes[y1+1][x1]);
					if(mapNodes[y1+1][x1+1].color!=-1)
						nodes.add(mapNodes[y1+1][x1+1]);
				}
				else{
					if(y1<mapNodes.length-1){
						if(mapNodes[y1][x1+1].color!=-1)
							nodes.add(mapNodes[y1][x1+1]);
						if(x1<mapNodes[0].length-1)
							if(mapNodes[y1+1][x1+1].color!=-1)
								nodes.add(mapNodes[y1+1][x1+1]);
					}
				}
				//checkValid(nodes);
				return nodes;
			}
			if(y1<y2 && x1>x2){
				if(x1>0){
					if(mapNodes[y1][x1-1].color!=-1)
						nodes.add(mapNodes[y1][x1-1]);
				}
				if(mapNodes[y1+1][x1].color!=-1)
					if(mapNodes[y1+1][x1].color!=-1)
						nodes.add(mapNodes[y1+1][x1]);
				//checkValid(nodes);
				return nodes;
			}
			if(y1>y2 && x1>x2){
				if(x1-x2==1){
					if(mapNodes[y1-1][x1].color!=-1){
						nodes.add(mapNodes[y1-1][x1]);
						if(mapNodes[y1-1][x1-1].color!=-1)
							nodes.add(mapNodes[y1-1][x1-1]);
					}
				}
				else{
					if(x1>0){
						if(mapNodes[y1][x1-1].color!=-1)
							nodes.add(mapNodes[y1][x1-1]);
						if(y1>0)
							if(mapNodes[y1-1][x1-1].color!=-1)
								nodes.add(mapNodes[y1-1][x1-1]);
					}
				}
				//checkValid(nodes);
				return nodes;
			}
			if(y1>y2 && x1<x2){
				if(x1<mapNodes[0].length-1){
					if(mapNodes[y1][x1+1].color!=-1)
						nodes.add(mapNodes[y1][x1+1]);
				}
				if(y1<mapNodes.length-1)
					if(mapNodes[y1-1][x1].color!=-1)
						nodes.add(mapNodes[y1-1][x1]);
				//checkValid(nodes);
				return nodes;
			}
		}
		return null;
	}

	
	//NOT IN USE
	private void BFS(int[] current, int[] dest, ArrayList<Path> paths){

	}

	private void checkValid(ArrayList<Node> nodes){
		for(Node n:nodes){
			if(n.color==-1)
				nodes.remove(n);
		}
	}
}

