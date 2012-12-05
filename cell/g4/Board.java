package cell.g4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cell.dumb.Player;
import cell.sim.Player.Direction;

public class Board {
	private int[][] board;
	
	public Board(int[][] board) {
		this.board = board;
	}
	
	public int dimension(){
		return board.length / 2;
	}
	
	public double[] getColorDistribution(){
		double[] dist= new double[6];
		for(int i=0; i < board.length ; i++){
			for(int j=0; j < board[0].length; j++){
				switch (board[i][j]) {
				case 0:
					dist[0]++;
					break;
				case 1:
					dist[1]++;
					break;
				case 2:
					dist[2]++;
					break;
				case 3:
					dist[3]++;
					break;
				case 4:
					dist[4]++;
					break;
				case 5:
					dist[5]++;
					break;
				default:
					break;
				}
			}
		}
		
		int sum=0;
		for(int i=0;i<dist.length;i++){
			sum+=dist[i];
		}
		
		for(int i=0; i <dist.length;i++){
			dist[i] /= sum;
		}
		return dist;
	}
	
	public int getColor(int[] location) {
		int i = location[0];
		int j = location[1];
		int dim2_1 = board.length;
		if (i < 0 || i >= dim2_1 || j < 0 || j >= dim2_1)
			return -1;
		return board[i][j];
	}
	
	public int[] nextLoc(int[] location, Player.Direction dir) {
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

	public boolean isValid(int[] location) {
		return getColor(location) >= 0;
	}
	
	public List<Direction> nextMove(int[] from, int[] to) {
		Set<Direction> directions = new HashSet<Direction>();
		
		int diffi = to[0] - from[0];
		int diffj = to[1] - from[1];
		
		assert(diffi != 0 || diffj != 0);
			
		if (diffi * diffj <= 0) {
			if (diffi < 0)
				directions.add(Direction.N);
			else if (diffi > 0)
				directions.add(Direction.S);
			if (diffj < 0)
				directions.add(Direction.W);
			else if (diffj > 0)
				directions.add(Direction.E);
		}		
		else if (diffi * diffj >= 0) {		
			// In this case, we can either go diagonal first
			// Or we can line up two axis by going straight

			// Go diagonal
			if (diffi > 0)
				directions.add(Direction.SE);
			else if (diffi < 0)
				directions.add(Direction.NW);
			
			// Go straight
			if (Math.abs(diffi) > Math.abs(diffj)) {
				if (diffi < 0)
					directions.add(Direction.N);
				else if (diffi > 0)
					directions.add(Direction.S);
			}
			else if (Math.abs(diffj) > Math.abs(diffi)) {
				if (diffj < 0)
					directions.add(Direction.W);
				else if (diffj > 0)
					directions.add(Direction.E);
			}
		}
		
		
		List<Direction> dirs = new ArrayList<Direction>();
		for (Direction d : directions) {
			if (isValid(nextLoc(from, d))) {
				dirs.add(d);
			}
		}
		
		return dirs;
	}
	
	public int mindist(int[] loc1, int[] loc2) {
		int diffi = loc2[0] - loc1[0];
		int diffj = loc2[1] - loc1[1];
		
		if (diffi == 0 && diffj == 0)
			return 0;
		
		if (diffi == 0)
			return Math.abs(diffj);
		if (diffj == 0)
			return Math.abs(diffi);
		
		// if diff in i and j are of opposite signs
		// first go N/S to matches i, then go E/W matches j
		if (diffi * diffj < 0)
			return Math.abs(diffi) + Math.abs(diffj);
		
		// if diff in and and j are of the same sign
		// first go NW/SE, then go N/W or S/E 
		int dist = Math.min(Math.abs(diffi), Math.abs(diffj));
		int lefti = Math.abs(diffi) - dist;
		int leftj = Math.abs(diffj) - dist;
		dist = dist + lefti + leftj;
		
		return dist;
	}
	
	public int[][] getBoard(){
		return board;
	}

	
	public int[] getCostOfFirstPath(int[] src, int[] dest) {
		int[] cost = new int[6];
		
		int[] curloc = new int[2];
		curloc[0] = src[0];
		curloc[1] = src[1];
		
		while (curloc[0] != dest[0] || curloc[1] != dest[1]) {
			List<Direction> dirs = nextMove(curloc, dest);
			
			// TODO: consider more options
			Direction dir = dirs.get(0);
			curloc = nextLoc(curloc, dir);
			cost[getColor(curloc)]++;
		}
		
		return cost;
	}
	
	public Path getFirstPath(int[] src, int[] dest) {
		Path path = new Path();
		
		int[] curloc = new int[2];
		curloc[0] = src[0];
		curloc[1] = src[1];
		
		while (curloc[0] != dest[0] || curloc[1] != dest[1]) {
			List<Direction> dirs = nextMove(curloc, dest);
			Direction dir = dirs.get(0);
			curloc = nextLoc(curloc, dir);
			path.addNext(dir);
		}
		
		return path;
	}
}
