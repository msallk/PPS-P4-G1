package cell.g4.movement;

import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;
import cell.sim.Player.Direction;

public class MaxControlYieldMove extends YieldMove {

	public MaxControlYieldMove(Board board, Sack sack, Player player) {
		super(board, sack, player);
	}
 
	private int getControlCount(int[] loc, int[][] players) {
		int count = 0;
		
		int[][] map = board.getBoard();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (!board.isValid(new int[] {i, j}))
					continue;
				
				boolean controlled = true;
				int ourdist = board.mindist(loc, new int[] {i,j});
				for (int p = 0; p < players.length; p++) {
					if (p == player.getOurIndex())
						continue;
					if (board.mindist(players[p], new int[] {i,j}) < ourdist) {
						controlled = false;
						break;
					}
				}
				if (controlled)
					count++;
			}
		}
		
		return count;
	}
	
	@Override
	public Direction move(int[] location, int[][] players, int[][] traders) {
		
		Direction dirs[] =
			{Direction.W,  Direction.E,
			 Direction.NW, Direction.N,
			 Direction.S, Direction.SE};
		
		int maxControl = 0;
		Direction d = null;
		for (Direction dir : dirs) {
			int[] loc = board.nextLoc(location, dir);
			if (!board.isValid(loc))
				continue;
			int controlCount = getControlCount(loc, players);
			if (controlCount > maxControl) {
				maxControl = controlCount;
				d = dir;
			}
			
		}
		return d;
	}

}
