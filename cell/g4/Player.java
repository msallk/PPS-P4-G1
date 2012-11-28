package cell.g4;

import java.util.Arrays;
import java.util.Random;

import cell.g4.movement.MoveAlgo;
import cell.g4.movement.ShortestPathMove;
import cell.g4.trade.MaxRateDiffTrade;
import cell.g4.trade.MergeTrade;
import cell.g4.trade.TradeAlgo;
import cell.sim.Player.Direction;

public class Player implements cell.sim.Player {
	private static int versions = 0;
	private int version = ++versions;	
	
	// the map
	private Board board = null;
	// our sack
	private Sack sacks;
	// our location
	private int[] loc = new int[2];
	// movement algorithm
	private MoveAlgo movement;
	// trading algorithm
	private TradeAlgo trading;	

	@Override
	public Direction move(int[][] map, int[] location, int[] sack,
			int[][] players, int[][] traders) {
		// create a map object the first time we move 
		if (board == null) {
			board = new Board(map);
			sacks = new Sack(sack);
			
			trading = new MergeTrade(board, sacks);
			movement = new ShortestPathMove(board, sacks);
		}
		// routines
		loc[0] = location[0];
		loc[1] = location[1];		
		sacks.update(sack);
		
		
		Direction dir = movement.move(location, players, traders);
		int[] new_location = board.nextLoc(location, dir);		
		int color = board.getColor(new_location);		
		sacks.decrease(color);
		
		return dir;
	}

	@Override
	public void trade(double[] rate, int[] request, int[] give) {
		trading.trade(rate, request, give);
	}
	
	@Override
	public String name() {
		return "G4 Player" + (versions > 1 ? " v" + version : "");
	}
}