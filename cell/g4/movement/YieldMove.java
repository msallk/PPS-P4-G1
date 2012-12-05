package cell.g4.movement;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;
import cell.sim.Player.Direction;

/*
 * When we don't have a good trader, or there are conflict, how are we going to move
 */
public abstract class YieldMove extends MoveAlgo {

	public YieldMove(Board board, Sack sack, Player player) {
		super(board, sack, player);
	}

	@Override
	public abstract Direction move(int[] location, int[][] players, int[][] traders);
}
