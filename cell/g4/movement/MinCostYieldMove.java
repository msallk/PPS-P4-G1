package cell.g4.movement;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;
import cell.sim.Player.Direction;

public class MinCostYieldMove extends YieldMove {
	
	public MinCostYieldMove(Board board, Sack sack, Player player) {
		super(board, sack, player);
	}

	@Override
	public Direction move(int[] location, int[][] players, int[][] traders) {
		Direction dirs[] =
			{Direction.W,  Direction.E,
			 Direction.NW, Direction.N,
			 Direction.S, Direction.SE};
		
		int max = 0;
		Direction d = null;
		for (Direction dir : dirs) {
			int[] loc = board.nextLoc(location, dir);
			if (!board.isValid(loc))
				continue;
			
			int color = board.getColor(loc);
			
			if (sack.getStock(color) > max) {
				max = sack.getStock(color);
				d = dir;
			}
		}
		return d;
	}
}
