package cell.g4.movement;

import cell.g4.Board;
import cell.g4.Sack;
import cell.sim.Player.Direction;

public class CheapestPathMove extends MoveAlgo {

	public CheapestPathMove(Board board, Sack sack) {
		super(board, sack);
	}

	@Override
	public Direction move(int[] location, int[][] players, int[][] traders) {
		return null;
	}

}
