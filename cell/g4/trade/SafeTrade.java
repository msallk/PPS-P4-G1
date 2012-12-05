package cell.g4.trade;

import java.util.List;

import cell.g4.Path;
import cell.g4.Board;
import cell.g4.Player;
import cell.g4.Sack;
import cell.g4.movement.SafeTraderFinder;

/*
 * Change the semantic of a RiskyTrade
 * When we meet a trader, and we find a next trader occupied by us
 * Then we trade everything, except the marbles needed to get that trader
 * 
 * But this trade eliminates the opportunity that there will a closer trader appearing nearby
 * Well, most of the time, we cannot find a ours trader 
 */
public class SafeTrade extends TradeAlgo {
	
	private SafeTraderFinder finder;
	
	public SafeTrade(Board board, Sack sack, Player player) {
		super(board, sack, player);
		finder = new SafeTraderFinder(board, player);
	}	
	
	@Override
	public void trade(double[] rate, int[] request, int[] give, 
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		
		// USEFUL when there is only 1 playerZ		
		// ourTrader may be less than 1, because we add the +1 constraint
		List<Integer> ourTraders = finder.findOurTrader(savedLocation, savedPlayers, savedTraders, true);
		
		assert(ourTraders.size() > 1);
		
		if (ourTraders.size() > 1) {
			
			int nextTrader = ourTraders.get(1);
			
			int[] reserves = board.getCostOfFirstPath(savedLocation, savedTraders[nextTrader]);
			
			Path savedPath = board.getFirstPath(savedLocation, savedTraders[nextTrader]);
			player.setSavedPath(savedPath);
			
			sack.setReserves(reserves);
		}
		
		int totalValue = 0;
		int invaluable = invaluableColor(rate);
		
		for (int i = 0 ; i != 6 ; ++i)
			request[i] = give[i] = 0;
		
		for (int i=0; i != 6; ++i){
			int amount = maxAmountToGive(i);
			give[i] = amount;
			totalValue += (amount * rate[i]);
		}		
		
		// first replenish
		for (int i = 0; i < 6; i++) {
			if (sack.getStock(i) < sack.getReserve(i)) {
				System.err.println("Replenish color " + i + " to " + sack.getReserve(i));
				request[i] = Math.min((int)(totalValue / rate[i]), sack.getReserve(i) - sack.getStock(i));
				totalValue -= (request[i] * rate[i]);
			}
		}
		
		// then buy invaluable
		request[invaluable] += (int)(totalValue / rate[invaluable]);	
	}	
	
	private int invaluableColor(double[] rate) {
		double minrate = Double.MAX_VALUE;
		int color = -1;
		for (int i = 0; i < rate.length; i++) {
			if (rate[i] < minrate) {
				minrate = rate[i];
				color = i;
			}
		}
		return color;
	}
	
	private int maxAmountToGive(int color) {
		return Math.max(0, sack.getStock(color) - sack.getReserve(color));
	}

	@Override
	public boolean toUse(double[] rate, int[] request, int[] give,
			int[] savedLocation, int[][] savedPlayers, int[][] savedTraders) {
		List<Integer> ourTraders = finder.findOurTrader(savedLocation, savedPlayers, savedTraders, true);
		if (ourTraders.size() > 1) 
			return true;
		else
			return false;
	}
}
