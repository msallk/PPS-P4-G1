package cell.g4;

import java.util.List;

import cell.sim.Player.Direction;

public class Path {
	/*
	 * 
	 * starting state : beginning of the game/after landing on where the leperchan was// 
	 *                  or when the leperchaun was gone(because other player took it)
	 * middle state : while the player is on his/her way
	 * final state: when he is reached to a leperachaun or lepechaun is gone
	 * 
	 * 
	 */
	final Board board;
	final int[][] trader;
	
	int[] startPos;
	int[] finalPos;
	
	public Path(Board board, int[][] trader, int[] startPos, int[] finalPos) {
		this.board = board;
		this.trader = trader;
		this.startPos = startPos;
		this.finalPos = finalPos;
	}
	
	//see if the final position has still the trader
	public boolean isPathStillValid(){
		//assume not there
		boolean traderDetected=false;
		for(int i=0; i < trader.length ; i++){
			if(finalPos.equals(trader[i])){
				traderDetected=true;
			}
		}
		return traderDetected;
	}
	
//	//TODO
//	public List<Direction> shortestPath(){
//		board.n
//		return null;
//	}
//	
//	public List<Direction> cheapestPath(){
//		return null;
//	}
	
}
